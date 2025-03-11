package com.example.happybirthday

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.happybirthday.ui.theme.HappyBirthdayTheme
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContent {
            HappyBirthdayTheme {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    GreetingWithBackgroundImage()
                }
            }
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

@Composable
fun GreetingWithBackgroundImage() {
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
            LoginForm()
            Spacer(modifier = Modifier.height(20.dp))
            OrDivider()
            Spacer(modifier = Modifier.height(20.dp))
            GoogleSignInButton {  }


        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginForm() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Email or Username",
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
        CustomTextField(value = email, onValueChange = { email = it }, label = "Email", keyboardType = KeyboardType.Email)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Password",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, bottom = 8.dp, top = 8.dp),
            textAlign = TextAlign.Start
        )
        CustomTextField(value = password, onValueChange = { password = it }, label = "Password", keyboardType = KeyboardType.Password, isPassword = true)

        Text(
            text = "Forgot Password?",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                color = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, bottom = 4.dp, top = 8.dp),
            textAlign = TextAlign.End
        )
        Spacer(modifier = Modifier.height(16.dp))
        LoginButton(name = "Login", onLogin = { /* Handle login action */ })
    }
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
fun LoginButton(onLogin: () -> Unit, name: String) {
    Button(
        onClick = { onLogin() },
        //colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFD5856)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
            //.padding(12.dp)
            .height(48.dp)
            .border(0.1.dp, Color.White, RoundedCornerShape(16.dp))
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = name,
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.White
            )

        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HappyBirthdayTheme {
        GreetingWithBackgroundImage()
    }
}
