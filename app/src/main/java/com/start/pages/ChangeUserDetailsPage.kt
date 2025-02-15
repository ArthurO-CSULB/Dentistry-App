package com.start.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/*
We have a composable settings page which will handle the UI for settings
where the user can change many of their app preferences.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */

//ToDo: 2/11/2025 Make functionality for user to delete their account
@Composable
fun ChangeUserDetailsPage(modifier: Modifier = Modifier, navController: NavController) {

    // Settings Page UI
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(16.dp))
        // Title of Games Page
        Text(
            text = "Prototype User Changes Page", fontSize = 32.sp
        )

        Spacer(modifier=Modifier.height(16.dp))

        // Change Email
        TextButton(onClick = {
            navController.navigate("changeEmail")
        }) {
            Text(text = "Change Email")
        }

        // Change Password
        TextButton(onClick = {
            navController.navigate("changePassword")
        }) {
            Text(text = "Change Password")
        }

        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("settings")
        }) {
            Text(text = "Go back to settings")
        }
    }
}