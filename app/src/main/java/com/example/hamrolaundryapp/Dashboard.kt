package com.example.hamrolaundryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.hamrolaundryapp.ui.theme.HamrolaundryAppTheme

class Dashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HamrolaundryAppTheme {
                Text("Dashboard Screen")
            }
        }
    }
}
