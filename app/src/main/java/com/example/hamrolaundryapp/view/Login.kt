package com.example.hamrolaundryapp.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import com.example.hamrolaundryapp.repo.UserRepoImpl
import com.example.hamrolaundryapp.ui.theme.Blue
import com.example.hamrolaundryapp.ui.theme.DarkBlue
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import com.example.hamrolaundryapp.viewmodel.UserViewModel

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HamrolaundryAppTheme {
                val userViewModel = remember { UserViewModel(UserRepoImpl()) }
                LoginScreen(userViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(userViewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isLoading by userViewModel.loading.observeAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        
        // Logo Placeholder
        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(24.dp),
            color = DarkBlue.copy(alpha = 0.1f)
        ) {
            Icon(
                Icons.Default.LocalLaundryService,
                contentDescription = null,
                modifier = Modifier.padding(20.dp).size(60.dp),
                tint = DarkBlue
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Welcome Back",
            style = TextStyle(
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        )

        Text(
            "Login to your account",
            style = TextStyle(color = Color.Gray, fontSize = 16.sp),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            placeholder = { Text("Enter your email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DarkBlue) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkBlue,
                unfocusedBorderColor = Color.LightGray
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DarkBlue) },
            visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { visibility = !visibility }) {
                    Icon(
                        imageVector = if (visibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = DarkBlue,
                unfocusedBorderColor = Color.LightGray
            )
        )

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            TextButton(onClick = {
                context.startActivity(Intent(context, ForgetPassword::class.java))
            }) {
                Text("Forgot Password?", color = DarkBlue, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val trimmedEmail = email.trim()
                val trimmedPassword = password.trim()
                if (trimmedEmail.isNotEmpty() && trimmedPassword.isNotEmpty()) {
                    userViewModel.login(trimmedEmail, trimmedPassword) { success, message ->
                        if (success) {
                            val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
                            sharedPreferences.edit { putBoolean("isLoggedIn", true) }
                            context.startActivity(Intent(context, Dashboard::class.java))
                            (context as? Activity)?.finish()
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please enter credentials", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Login", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Don't have an account? ", color = Color.Gray)
            Text(
                "Sign Up",
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, RegistrationActivity::class.java))
                },
                style = TextStyle(color = Blue, fontWeight = FontWeight.Bold)
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    HamrolaundryAppTheme {
        LoginScreen(userViewModel = UserViewModel(UserRepoImpl()))
    }
}
