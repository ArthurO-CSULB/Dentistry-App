package com.start.pages.hygiene_trivia_pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.viewmodels.AchievementViewModel
import com.start.viewmodels.HygieneTriviaState
import com.start.viewmodels.HygieneTriviaViewModel
import com.start.viewmodels.PointsProgressionViewModel

@Composable
fun HygieneTriviaPageFinished(modifier: Modifier, navController: NavController,
                              hygieneTriviaViewModel: HygieneTriviaViewModel, achievementViewModel: AchievementViewModel) {

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
        Text(text="Question ${questionIndex + 1}:", textAlign = TextAlign.Center, fontSize = 32.sp,
            lineHeight = 1.5.em, fontWeight = FontWeight.Bold)
        Text(modifier = modifier.padding(horizontal = 16.dp), text = questions[questionIndex], textAlign = TextAlign.Center, fontSize = 32.sp,
            lineHeight = 1.5.em, fontStyle = FontStyle.Italic)

        // We compare the correct answers with the user's answers and display the result of if
        // they got it wrong or right. Display the correct answer for the question if they got it
        // wrong.
        if (answers[questionIndex] == choices[questionIndex][userAnswerIndex]) {
            Text(modifier = modifier.padding(horizontal = 16.dp), text = "✅ Your Answer: ${choices[questionIndex][userAnswerIndex]}",
                textAlign = TextAlign.Center, fontSize = 24.sp, lineHeight = 1.5.em)
        }
        // Else we display that the user did not get the question right.
        else {
            Text(modifier = modifier.padding(horizontal = 16.dp), text = "❌ Your Answer: ${choices[questionIndex][userAnswerIndex]}\n" +
                    "✅ Correct Answer: ${answers[questionIndex]}", textAlign = TextAlign.Center,
                fontSize = 24.sp, lineHeight = 1.5.em)
        }
    }

    // When the state of the trivia is changed to begin, navigate to the home screen.
    LaunchedEffect(hygieneTriviaState.value) {
        when(hygieneTriviaState.value) {
            is HygieneTriviaState.Points -> navController.navigate("trivia_points")
            // is HygieneTriviaState.Begin -> navController.navigate("home")
            else -> Unit
        }
        achievementViewModel.incrementAchievement("trivia_master")
    }



    // Column for the trivia page.
    Column(
        modifier = modifier
            .fillMaxSize(),
            //.border(1.dp, Color.Black),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // If condition so that when it recomposes and the userAnswers has been reset, it
        // it does not display UI
        if (!userAnswers.isEmpty()) Text("Results!", fontSize = 48.sp, fontWeight = FontWeight.Bold)
        when(resultsIndex.value) {

            0 -> if (!userAnswers.isEmpty()) resultText(0, userAnswers[0])
            1 -> if (!userAnswers.isEmpty()) resultText(1, userAnswers[1])
            2 -> if (!userAnswers.isEmpty()) resultText(2, userAnswers[2])
            3 -> if (!userAnswers.isEmpty()) resultText(3, userAnswers[3])
            4 -> if (!userAnswers.isEmpty()) resultText(4, userAnswers[4])
            else -> Unit
        }

        when(resultsIndex.value){
            // When on the last result, display button to check the points.
            4 -> {
                Button(onClick = {
                    hygieneTriviaViewModel.goToPoints()
                }) {
                    Text("View Points")
                }
            }
            else -> {
                // If condition so that when it recomposes and the userAnswers has been reset, it
                // it does not display UI
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