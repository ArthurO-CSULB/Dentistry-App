package com.start.pages.hygiene_trivia_pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.start.viewmodels.HygieneTriviaState
import com.start.viewmodels.HygieneTriviaViewModel
import com.start.viewmodels.PointsProgressionViewModel

@Composable
fun HygieneTriviaPagePoints(modifier: Modifier, navController: NavController,
                            hygieneTriviaViewModel: HygieneTriviaViewModel,
                            pointsProgressionViewModel: PointsProgressionViewModel
) {
    val hygieneTriviaState = hygieneTriviaViewModel.hygieneTriviaState.collectAsState()
    val numCorrect = hygieneTriviaViewModel.numCorrect()
    // Navigate Home when the state of the trivia changes to begin.
    LaunchedEffect(hygieneTriviaState.value) {
        when(hygieneTriviaState.value) {
            is HygieneTriviaState.Begin -> navController.navigate("home")
            else -> Unit
        }
    }

    // Add points to to the user's account.
    LaunchedEffect(Unit) {
        pointsProgressionViewModel.addTriviaPoints(numCorrect.toLong())
    }

    if (hygieneTriviaState.value != HygieneTriviaState.Begin) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display the number of correct answers the user got.
            Text("You got $numCorrect out of 5 correct!")
            Text("+${numCorrect * 10} points!")
            Button(
                onClick = {
                    hygieneTriviaViewModel.resetTrivia()
                }
            ) {
                Text("Go Home")
            }
        }
    }


}