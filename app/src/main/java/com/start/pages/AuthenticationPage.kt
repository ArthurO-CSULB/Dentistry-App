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
import com.start.AuthViewModel

/*
Authentication page called after registering for an account
 */

@Composable
fun AuthenticationPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    // Calendar Page UI
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(16.dp))
        // Title of Calendar Page
        Text(
            text = "Prototype Calendar Page", fontSize = 32.sp
        )
        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home")
        }
    }
}