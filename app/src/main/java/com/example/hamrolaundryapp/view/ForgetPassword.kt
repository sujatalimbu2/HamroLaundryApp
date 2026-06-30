package com.example.hamrolaundryapp.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import com.example.hamrolaundryapp.repo.UserRepoImpl
import com.example.hamrolaundryapp.ui.theme.DarkBlue
import com.example.hamrolaundryapp.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(
    viewModel: UserViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return UserViewModel(UserRepoImpl()) as T
        }
    }),
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isLoading by viewModel.loading.observeAsState(initial = false)

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Forgot Password", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D2939)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Box(
                modifier = Modifier.size(100.dp).background(Color(0xFFE0F2FE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(40.dp), tint = DarkBlue)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "Reset Your Password", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = DarkBlue)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Enter your registered email address and we'll send you a link to reset your password.",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                Text(text = "Email Address", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1D2939), modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Enter your email address", color = Color.LightGray) },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = DarkBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (email.isNotEmpty()) {
                        viewModel.forgetPassword(email) { success, msg ->
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            if (success) {
                                onBack()
                            }
                        }
                    } else {
                        Toast.makeText(context, "Please enter your email", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Send Reset Link", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextButton(onClick = onBack) {
                Text(text = "Back to Login", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgetPasswordScreenPreview() {
    HamrolaundryAppTheme {
        ForgetPasswordScreen(onBack = {})
    }
}
