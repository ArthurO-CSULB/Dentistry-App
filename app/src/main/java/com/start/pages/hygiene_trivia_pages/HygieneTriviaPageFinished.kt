package com.start.pages.hygiene_trivia_pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.start.viewmodels.HygieneTriviaState
import com.start.viewmodels.HygieneTriviaViewModel

@Composable
fun HygieneTriviaPageFinished(modifier: Modifier, navController: NavController, hygieneTriviaViewModel: HygieneTriviaViewModel) {

    // Use BackHandler to intercept the system back button and navigate to the home screen.
    BackHandler {
        // Navigate back to the home screen when the system back button is pressed
        navController.popBackStack("home", inclusive = false)
    }

    // Collect the index, state of the trivia. Recompose when it changes.
    val resultsIndex = hygieneTriviaViewModel.resultsIndex.collectAsState()

    val hygieneTriviaQuestions = hygieneTriviaViewModel.questions.collectAsState()

    // List of questions 1-5 asked.
    val questions: List<String> = hygieneTriviaQuestions.value.map {it.question}
    // List of answers 1-5
    val answers: List<String> = hygieneTriviaQuestions.value.map {it.answer}
    // Choices 1-5 with same indexed the same as questions.
    val choices: List<List<String>> = hygieneTriviaQuestions.value.map {it.choices}
    // Get the indexes of the user's answers.
    val userAnswers = hygieneTriviaViewModel.userAnswersIndex
    // Store the state of the trivia.
    val hygieneTriviaState = hygieneTriviaViewModel.hygieneTriviaState.collectAsState()

    // Text to display whether the user got the question right or wrong.
    val resultText: @Composable (questionIndex: Int, userAnswerIndex: Int) -> Unit = { questionIndex, userAnswerIndex ->
        // Display the question for the particular question passed in.
        Text(text = "Question ${questionIndex + 1}:\n${questions[questionIndex]}", textAlign = TextAlign.Center)

        // We compare the correct answers with the user's answers and display the result of if
        // they got it wrong or right. Display the correct answer for the question if they got it
        // wrong.
        if (answers[questionIndex] == choices[questionIndex][userAnswerIndex]) {
            Text(text = "✅ Your Answer: ${choices[questionIndex][userAnswerIndex]}", textAlign = TextAlign.Center)
        }
        // Else we display that the user did not get the question right.
        else {
            Text(text = "❌ Your Answer: ${choices[questionIndex][userAnswerIndex]}\n" +
                    "✅ Correct Answer: ${answers[questionIndex]}", textAlign = TextAlign.Center)
        }
    }



    LaunchedEffect(hygieneTriviaState.value) {
        when(hygieneTriviaState.value) {
            is HygieneTriviaState.Begin -> navController.navigate("home")
            else -> Unit
        }
    }



    // Column for the trivia page.
    Column(
        modifier = modifier
            .fillMaxSize()
            .border(1.dp, Color.Black),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!userAnswers.isEmpty()) Text("Results!")
        when(resultsIndex.value) {

            0 -> if (!userAnswers.isEmpty()) resultText(0, userAnswers[0])
            1 -> if (!userAnswers.isEmpty()) resultText(1, userAnswers[1])
            2 -> if (!userAnswers.isEmpty()) resultText(2, userAnswers[2])
            3 -> if (!userAnswers.isEmpty()) resultText(3, userAnswers[3])
            4 -> if (!userAnswers.isEmpty()) resultText(4, userAnswers[4])
            else -> Unit
        }

        when(resultsIndex.value){
            // When on the last result, display button to go home.
            4 -> {
                Button(onClick = {
                    hygieneTriviaViewModel.resetTrivia()
                }) {
                    Text("Go Home")
                }
            }
            else -> {
                if (userAnswers.isEmpty()) {}
                else {
                    Button(onClick = {
                        hygieneTriviaViewModel.nextResult()
                    }) {
                        Text("Next Result")
                    }
                }

            }
        }


    }
}