package com.start

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.start.ui.theme.DentalHygieneTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding

class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel : AuthViewModel by viewModels()
        setContent {
            DentalHygieneTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PageNavigation(modifier = Modifier.padding(innerPadding), authViewModel = authViewModel)
                }
            }
        }
    }
}