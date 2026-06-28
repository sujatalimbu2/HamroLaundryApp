package com.example.hamrolaundryapp.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrolaundryapp.model.UserModel
import com.example.hamrolaundryapp.ui.theme.Blue
import com.example.hamrolaundryapp.ui.theme.DarkBlue
import com.example.hamrolaundryapp.viewmodel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationScreen(
    userViewModel: UserViewModel,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var visibility by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val isLoading by userViewModel.loading.observeAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                "Create Account",
                style = TextStyle(color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 32.sp)
            )

            Text(
                "Sign up to get started",
                style = TextStyle(color = Color.Gray, fontSize = 16.sp),
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            RegistrationInputField(value = name, onValueChange = { name = it }, label = "Full Name", icon = Icons.Default.Person)
            Spacer(modifier = Modifier.height(16.dp))
            RegistrationInputField(value = address, onValueChange = { address = it }, label = "Address", icon = Icons.Default.LocationOn)
            Spacer(modifier = Modifier.height(16.dp))
            RegistrationInputField(value = email, onValueChange = { email = it }, label = "Email Address", icon = Icons.Default.Email)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = DarkBlue) },
                visualTransformation = if (visibility) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { visibility = !visibility }) {
                        Icon(imageVector = if (visibility) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = Color.Gray)
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val trimmedEmail = email.trim()
                    if (trimmedEmail.isNotEmpty() && password.isNotEmpty() && name.isNotEmpty()) {
                        userViewModel.register(trimmedEmail, password) { success, msg, userId ->
                            if (success) {
                                val model = UserModel(id = userId, name = name, email = trimmedEmail, address = address, contact = "")
                                userViewModel.addUser(userId, model) { addSuccess, addMsg ->
                                    if (addSuccess) {
                                        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                                        onRegisterSuccess()
                                    } else {
                                        Toast.makeText(context, addMsg, Toast.LENGTH_LONG).show()
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
                    modifier = Modifier.clickable { onLoginClick() },
                    style = TextStyle(color = Blue, fontWeight = FontWeight.Bold)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun RegistrationInputField(value: String, onValueChange: (String) -> Unit, label: String, icon: ImageVector) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = { Icon(icon, contentDescription = null, tint = DarkBlue) },
        singleLine = true
    )
}
