package com.start.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun GamesPage(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Game Menu", fontSize = 32.sp, color = Color.White, modifier = Modifier.padding(16.dp))

        Button(
            onClick = { navController.navigate("flappytooth") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Play Flappy Tooth")

        }
        Button(
            onClick = { navController.navigate("tooth_snake") },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Play Tooth Snake")
        }

        // Add more buttons later
        // Button to go back home, placed inside the scrollable column
        TextButton(
            onClick = { navController.navigate("home") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Home")
        }
    }
}
