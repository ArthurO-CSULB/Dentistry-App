package com.start.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

/*
We have a composable clinic search page which will handle the UI for clinic search.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */
@Composable
fun ClinicSearchPage(modifier: Modifier = Modifier, navController: NavController) {

    // Calendar Page UI
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title of Calendar Page
        Text(
            text = "Prototype Clinic Search Page", fontSize = 32.sp
        )
        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home")
        }
    }
}