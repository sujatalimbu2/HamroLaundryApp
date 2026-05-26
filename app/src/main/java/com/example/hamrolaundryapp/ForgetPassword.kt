package com.example.hamrolaundryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme
import com.example.hamrolaundryapp.ui.theme.DarkBlue

class ForgetPassword : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HamrolaundryAppTheme {
                ForgetPasswordScreen(onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgetPasswordScreen(onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    val primaryBlue = Color(0xFF007BFF) // Bright blue for button as seen in image

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Forgot Password",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D2939)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Banner Area (Washing Machine Image placeholder)
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp),
////                    .background(
////                        brush = Brush.verticalGradient(
////                            colors = listOf(Color(0xFFE0F2FE), Color.White)
////                        )
////                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                // Since we don't have the image, using a text placeholder or icon
////                Text(
////                    text = "Illustration Placeholder",
////                    color = Color.LightGray,
////                    fontSize = 12.sp
////                )
//            }

            Spacer(modifier = Modifier.height(100.dp))

            // Locked Mail Icon in Circle
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color(0xFFE0F2FE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = DarkBlue
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Text content
            Text(
                text = "Reset Your Password",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D2939)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Enter your registered email address and we'll send you a link to reset your password.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Input
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Email Address",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1D2939),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("Enter your email address", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = DarkBlue)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Send Button
            Button(
                onClick = { /* TODO: Send reset link logic */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Text("Send Reset Link", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // OR Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Back to Login
            TextButton(onClick = onBack) {
                Text(
                    text = "Back to Login",
                    color = DarkBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ForgetPasswordPreview() {
    HamrolaundryAppTheme {
        ForgetPasswordScreen(onBack = {})
    }
}
