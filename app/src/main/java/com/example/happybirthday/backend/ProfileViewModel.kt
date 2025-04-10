package com.example.happybirthday.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserProfileRepository) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userId: String? get() = auth.currentUser?.uid

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    // State for input fields
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _dateOfBirth = MutableStateFlow("")
    val dateOfBirth: StateFlow<String> = _dateOfBirth.asStateFlow()

    private val _gender = MutableStateFlow("")
    val gender: StateFlow<String> = _gender.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        userId?.let { uid ->
            viewModelScope.launch {
                _isLoading.value = true
                repository.getUserProfile(uid)
                    .catch { e ->
                        // Handle error
                        _isLoading.value = false
                    }
                    .collectLatest { profile ->
                        _userProfile.value = profile
                        // Initialize input fields with loaded data or empty if no profile exists
                        _name.value = profile?.name ?: ""
                        _username.value = profile?.username ?: ""
                        _dateOfBirth.value = profile?.dateOfBirth ?: ""
                        _gender.value = profile?.gender ?: ""
                        _isLoading.value = false
                    }
            }
        } ?: run {
            // Handle case where user is not logged in (shouldn't happen if screen is protected)
            _isLoading.value = false
        }
    }

    // Functions to update state from UI
    fun onNameChange(newName: String) { _name.value = newName }
    fun onUsernameChange(newUsername: String) { _username.value = newUsername }
    fun onDateOfBirthChange(newDob: String) { _dateOfBirth.value = newDob }
    fun onGenderChange(newGender: String) { _gender.value = newGender }

    fun saveProfile() {
        userId?.let { uid ->
            viewModelScope.launch {
                _saveState.value = SaveState.Saving
                try {
                    val profileToSave = UserProfile(
                        userId = uid,
                        name = _name.value.takeIf { it.isNotBlank() },
                        username = _username.value.takeIf { it.isNotBlank() },
                        dateOfBirth = _dateOfBirth.value.takeIf { it.isNotBlank() },
                        gender = _gender.value.takeIf { it.isNotBlank() }
                    )
                    repository.saveUserProfile(profileToSave)
                    _saveState.value = SaveState.Success
                    // Reset save state after a short delay or user action
                    kotlinx.coroutines.delay(2000) // Example delay
                    _saveState.value = SaveState.Idle
                } catch (e: Exception) {
                    _saveState.value = SaveState.Error(e.message ?: "Unknown error")
                }
            }
        } ?: run {
            _saveState.value = SaveState.Error("User not logged in")
        }
    }
}

// Sealed class for save operation state
sealed class SaveState {
    object Idle : SaveState()
    object Saving : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}

// ViewModel Factory
class ProfileViewModelFactory(private val repository: UserProfileRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
 