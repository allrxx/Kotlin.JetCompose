package com.example.happybirthday

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.happybirthday.ui.theme.AppTheme


@Composable
fun LoginScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.gradient),
            contentDescription = "Background image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            //horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Greeting()
            Text(text = "Don't have an Account? Sign Up",
                style = TextStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, bottom = 2.dp, top = 2.dp),
                textAlign = TextAlign.Start
            )
            Spacer(modifier=Modifier.height(40.dp))
            LoginForm(navController)
            Spacer(modifier = Modifier.height(20.dp))
            OrDivider()
            Spacer(modifier = Modifier.height(20.dp))
            GoogleSignInButton {  }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Text(
        text = "Sign In To Your Account.",
        style = TextStyle(
            fontSize = 84.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            letterSpacing = (-1.5).sp,
            lineHeight = 70.sp,
        ),
        color = Color.White,
        modifier = modifier.padding(top = 55.dp, start = 12.dp, end = 4.dp, bottom = 4.dp),
        textAlign = TextAlign.Left
    )
}

// LoginScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var authError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Email Input
        Text(
            text = "Email",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, bottom = 8.dp),
            textAlign = TextAlign.Start
        )

        CustomTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = null
                authError = null
            },
            label = "Email",
            keyboardType = KeyboardType.Email
        )

        emailError?.let {
            ErrorText(message = it)
        }

        // Password Input
        Text(
            text = "Password",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, top = 8.dp, bottom = 8.dp),
            textAlign = TextAlign.Start
        )

        CustomTextField(
            value = password,
            onValueChange = {
                password = it
                authError = null
            },
            label = "Password",
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Text(
            text = "Forgot Password?",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, top = 8.dp, bottom = 12.dp),
            textAlign = TextAlign.End
        )

        authError?.let {
            ErrorText(message = it)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LoginButton(
            name = "Login",
            isLoading = isLoading,
            onLogin = {
                val error = validateCredentials(email, password)
                if (error != null) {
                    emailError = error
                    return@LoginButton
                }

                isLoading = true
                signInUser(
                    email, password,
                    onSuccess = {
                        isLoading = false
                        navController.navigate("home")
                    },
                    onFailure = { e ->
                        isLoading = false
                        authError = e.message ?: "Authentication failed"
                    }
                )
            }
        )
    }
}

@Composable
fun ErrorText(message: String) {
    Text(
        text = message,
        color = Color.Red,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, top = 4.dp),
        textAlign = TextAlign.Start
    )
}


@Composable
fun OrDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White, thickness = 1.dp)
        Text(
            text = "   or   ",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.White, thickness = 1.dp)
    }
}

@Composable
fun CustomTextField(value: String, onValueChange: (String) -> Unit, label: String, keyboardType: KeyboardType, isPassword: Boolean = false) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color.White, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        modifier = Modifier
            .padding(8.dp)
            .height(48.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_icon),
            contentDescription = "Google Logo",
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Sign in with Google", color = Color.Black)
    }
}

@Composable
fun LoginButton(
    name: String,
    isLoading: Boolean,
    onLogin: () -> Unit
) {
    Button(
        onClick = onLogin,
        enabled = !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .height(48.dp)
            .border(0.1.dp, Color.White, RoundedCornerShape(16.dp))
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = name,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.White
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        val navController = rememberNavController()
        LoginScreen(navController)
    }
}