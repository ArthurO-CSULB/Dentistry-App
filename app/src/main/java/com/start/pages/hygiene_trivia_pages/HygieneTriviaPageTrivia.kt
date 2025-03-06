package com.start.pages.hygiene_trivia_pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.start.viewmodels.HygieneTriviaViewModel

@Composable
fun HygieneTriviaPageTrivia(modifier: Modifier, navController: NavController, hygieneTriviaViewModel: HygieneTriviaViewModel) {

    // Use BackHandler to intercept the system back button and navigate to the home screen.
    BackHandler {
        // Navigate back to the home screen when the system back button is pressed
        navController.popBackStack("home", inclusive = false)
    }

    // Collect the index, state of the trivia.
    val triviaIndex = hygieneTriviaViewModel.triviaIndex.collectAsState()
    val hygieneTriviaState = hygieneTriviaViewModel.hygieneTriviaState.collectAsState()
    // Store the specific question, answer, and choices for the specific trivia question.
    val question: String = hygieneTriviaViewModel.questions[triviaIndex.value].question
    val choices: List<String> = hygieneTriviaViewModel.questions[triviaIndex.value].choices
    val answer: String = hygieneTriviaViewModel.questions[triviaIndex.value].answer



    // Column for the trivia page.
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Question ${triviaIndex.value}: $question")
        Spacer(modifier = Modifier.width(16.dp))
        Text("Choices: $choices")

    }
}