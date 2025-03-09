package com.start.pages.hygiene_trivia_pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.start.viewmodels.HygieneTriviaViewModel


@Composable
fun HygieneTriviaPageFailed(modifier: Modifier, navController: NavController, hygieneTriviaViewModel: HygieneTriviaViewModel) {

    // Use BackHandler to intercept the system back button and navigate to the home screen.
    BackHandler {
        // Navigate back to the home screen when the system back button is pressed
        navController.popBackStack("home", inclusive = false)
    }
    Column(
        modifier= modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("You have failed the hygiene trivia :(")
    }
}