package com.example.hamrolaundryapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hamrolaundryapp.ui.theme.DarkBlue
import kotlinx.coroutines.delay



class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashBody()
        }
    }
}

@Composable
fun SplashBody() {
    val context = LocalContext.current
    LaunchedEffect(Unit) {

        delay(3000)
        val sharedPreferences = context.getSharedPreferences(
            "User",
            Context.MODE_PRIVATE
        )
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(context, Dashboard::class.java)
            context.startActivity(intent)
        } else {
            val intent = Intent(context, Login::class.java)
            context.startActivity(intent)
        }
        context.findActivity()?.finish()

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = DarkBlue),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.logo), // Changed from logo as it was missing
            contentDescription = null,
            modifier = Modifier
                .height(400.dp)
                .width(400.dp)
        )
        CircularProgressIndicator()

    }
}

@Preview
@Composable
fun SplashPreview() {
    SplashBody()
}
