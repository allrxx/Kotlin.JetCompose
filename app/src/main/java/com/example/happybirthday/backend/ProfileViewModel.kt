package com.example.happybirthday.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.util.Log

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

    // Modify loadProfile to accept userId and be public
    fun loadProfile(userId: String) {
        Log.d("ProfileViewModel", "loadProfile called for userId: $userId")
        viewModelScope.launch {
            _isLoading.value = true
            repository.getUserProfile(userId) // Use the passed userId
                .catch { e ->
                    Log.e("ProfileViewModel", "Error loading profile flow for $userId", e)
                    _isLoading.value = false
                    // Optionally emit error to a flow
                }
                .collectLatest { profile ->
                    Log.d("ProfileViewModel", "Profile Flow for $userId emitted: Name='${profile?.name}', Username='${profile?.username}'")
                    _userProfile.value = profile
                    // Reset fields based on loaded profile or clear if null
                    _name.value = profile?.name ?: ""
                    _username.value = profile?.username ?: ""
                    _dateOfBirth.value = profile?.dateOfBirth ?: ""
                    _gender.value = profile?.gender ?: ""
                    _isLoading.value = false
                }
        }
    }

    // Function to clear the profile state (e.g., on logout or error)
    fun clearProfile() {
        Log.d("ProfileViewModel", "Clearing profile state")
        _userProfile.value = null
        _name.value = ""
        _username.value = ""
        _dateOfBirth.value = ""
        _gender.value = ""
        _isLoading.value = false
        _saveState.value = SaveState.Idle
    }

    // Functions to update state from UI
    fun onNameChange(newName: String) { _name.value = newName }
    fun onUsernameChange(newUsername: String) { _username.value = newUsername }
    fun onDateOfBirthChange(newDob: String) { _dateOfBirth.value = newDob }
    fun onGenderChange(newGender: String) { _gender.value = newGender }

    // saveProfile still uses the internal getter for the *current* auth user
    fun saveProfile() {
        val currentUid = auth.currentUser?.uid // Use current auth user for saving
        if (currentUid == null) {
             _saveState.value = SaveState.Error("User not logged in")
             return
        }

        viewModelScope.launch {
            _saveState.value = SaveState.Saving
            try {
                val profileToSave = UserProfile(
                    userId = currentUid, // Make sure save uses the currently logged-in user's ID
                    name = _name.value.takeIf { it.isNotBlank() },
                    username = _username.value.takeIf { it.isNotBlank() },
                    dateOfBirth = _dateOfBirth.value.takeIf { it.isNotBlank() },
                    gender = _gender.value.takeIf { it.isNotBlank() }
                )
                Log.d("ProfileViewModel", "Saving profile for userId: $currentUid")
                repository.saveUserProfile(profileToSave)
                _saveState.value = SaveState.Success
                // Note: The flow collected by loadProfile should automatically update after this save
                kotlinx.coroutines.delay(2000) 
                _saveState.value = SaveState.Idle
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error saving profile for $currentUid", e)
                _saveState.value = SaveState.Error(e.message ?: "Unknown error")
            }
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
 