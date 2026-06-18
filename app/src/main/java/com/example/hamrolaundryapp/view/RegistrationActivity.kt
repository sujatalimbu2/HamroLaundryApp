package com.example.hamrolaundryapp.view

import android.content.Intent
import android.widget.Toast
import android.os.Bundle
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrolaundryapp.model.UserModel
import com.example.hamrolaundryapp.repo.UserRepoImpl
import com.example.hamrolaundryapp.ui.theme.Blue
import com.example.hamrolaundryapp.ui.theme.DarkBlue
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import com.example.hamrolaundryapp.viewmodel.UserViewModel

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HamrolaundryAppTheme {
                val userViewModel = remember { UserViewModel(UserRepoImpl()) }
                RegistrationScreen(userViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(userViewModel: UserViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
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
        Spacer(modifier = Modifier.height(60.dp))
        
        Text(
            "Create Account",
            style = TextStyle(
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        )

        Text(
            "Sign up to get started",
            style = TextStyle(color = Color.Gray, fontSize = 16.sp),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        // Name Field
        RegistrationInputField(
            value = name,
            onValueChange = { name = it },
            label = "Full Name",
            icon = Icons.Default.Person
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Address Field
        RegistrationInputField(
            value = address,
            onValueChange = { address = it },
            label = "Address",
            icon = Icons.Default.LocationOn
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email Field
        RegistrationInputField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            icon = Icons.Default.Email
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

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                    userViewModel.register(email, password) { success, msg, userId ->
                        if (success) {
                            val model = UserModel(
                                id = userId,
                                name = name,
                                email = email,
                                address = address,
                                contact = ""
                            )
                            userViewModel.addUser(userId, model) { addSuccess, addMsg ->
                                Toast.makeText(context, addMsg, Toast.LENGTH_LONG).show()
                                if (addSuccess) {
                                    context.startActivity(Intent(context, Login::class.java))
                                }
                            }
                        } else {
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
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
                Text("Sign Up", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ", color = Color.Gray)
            Text(
                "Login",
                modifier = Modifier.clickable {
                    context.startActivity(Intent(context, Login::class.java))
                },
                style = TextStyle(color = Blue, fontWeight = FontWeight.Bold)
            )
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun RegistrationInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(icon, contentDescription = null, tint = DarkBlue) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = DarkBlue,
            unfocusedBorderColor = Color.LightGray
        ),
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun RegistrationPreview() {
    HamrolaundryAppTheme {
        RegistrationScreen(userViewModel = UserViewModel(UserRepoImpl()))
    }
}
