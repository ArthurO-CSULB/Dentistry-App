package com.start.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx. compose. ui. res. painterResource
import androidx.navigation.NavController
import com.start.viewmodels.AuthState
import com.start.viewmodels.AuthViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import com.example.dentalhygiene.R
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun FeatureButton(
    text: String,
    color: Color,
    width: Int, // Custom width
    height: Int = 120, // Default height, can be overridden
    icon: Int, // Icon resource ID
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(width.dp) // Use custom width
            .height(height.dp), // Use custom height
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(40.dp) // Set icon size
            )
            Spacer(modifier = Modifier.height(8.dp)) // Space between icon and text
            Text(text = text, fontSize = 14.sp, color = Color.White)
        }
    }
}
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    // Observe authentication state
    val authState = authViewModel.authState.observeAsState()

    // Handle authentication state changes
    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.UnAuthenticated -> navController.navigate("login")
            is AuthState.Unverified -> navController.navigate("verification")
            else -> Unit
        }
    }

    // Home Page UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Button (Circular)
        Button(
            onClick = { navController.navigate("profile") },
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile), // Add profile icon
                contentDescription = "Profile",
                modifier = Modifier.size(40.dp)
            )
        }

        // Space between profile button and first row
        Spacer(modifier = Modifier.height(96.dp))

        // First Row: Calendar, Timer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeatureButton(
                text = "Calendar",
                color = Color(0xFF4CAF50),
                width = 120, // Set your desired width
                icon = R.drawable.ic_calendar, // Add calendar icon
                onClick = { navController.navigate("calendar") }
            )
            FeatureButton(
                text = "Timer",
                color = Color(0xFF2196F3),
                width = 120, // Set your desired width
                icon = R.drawable.ic_timer, // Add timer icon
                onClick = { navController.navigate("timer") }
            )
        }

        // Space between first and second rows
        Spacer(modifier = Modifier.height(16.dp))

        // Second Row: Glossary, Search
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeatureButton(
                text = "Glossary",
                color = Color(0xFFFFC107),
                width = 120, // Set your desired width
                icon = R.drawable.ic_glossary, // Add glossary icon
                onClick = { navController.navigate("glossary") }
            )
            FeatureButton(
                text = "Search",
                color = Color(0xFF9C27B0),
                width = 120, // Set your desired width
                icon = R.drawable.ic_search, // Add search icon
                onClick = { navController.navigate("search") }
            )
        }

        // Space between second and third rows
        Spacer(modifier = Modifier.height(24.dp))

        // Third Row: Games (with custom dimensions)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeatureButton(
                text = "Games",
                color = Color(0xFFE91E63),
                width = 264, // Set custom width for Games
                height = 100, // Set custom height for Games
                icon = R.drawable.ic_games, // Add games icon
                onClick = { navController.navigate("games") }
            )
        }

        // Sign Out Button (at the bottom)
        Spacer(modifier = Modifier.weight(1f))
        TextButton(onClick = { authViewModel.signout() }) {
            Text(text = "Sign Out", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}