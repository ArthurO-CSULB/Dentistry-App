package com.start.pages.hygiene_trivia_pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.viewmodels.HygieneTriviaState
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
    // Collect the List of DentalTriviaQnA
    val questions = hygieneTriviaViewModel.questions.collectAsState()
    // Collect the timer.
    val hygieneTriviaTimer = hygieneTriviaViewModel.hygieneTriviaTimer.collectAsState()

    // Store the specific question, answer, and choices for the specific trivia question.
    val question: String = questions.value[triviaIndex.value].question
    //val question: String = hygieneTriviaViewModel.questions[triviaIndex.value].question
    val choices: List<String> = questions.value[triviaIndex.value].choices
    //val choices: List<String> = hygieneTriviaViewModel.questions[triviaIndex.value].choices
    val answer: String = questions.value[triviaIndex.value].answer
    //val answer: String = hygieneTriviaViewModel.questions[triviaIndex.value].answer

    // When the state is finished, go to the finished page. When the state is failed,
    // go to the failed page.
    LaunchedEffect(hygieneTriviaState.value) {
        when(hygieneTriviaState.value) {
            is HygieneTriviaState.Finished -> navController.navigate("trivia_finish")
            is HygieneTriviaState.Failed -> navController.navigate("trivia_fail")
            else -> Unit
        }
    }

    // Column for the trivia page to fill the entire screen.
    val triviaUI = @Composable {
        Column(
            modifier = modifier
                .fillMaxSize()
                //.border(1.dp, Color.Black)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row for the question/time.
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    //.border(1.dp, Color.Black)
                    .padding(start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                // Text for the question and the timer counting down from 30 seconds.
                Text("Question ${triviaIndex.value + 1}:\n$question", textAlign = TextAlign.Center,
                    lineHeight = 2.em, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            // Row for the choices columns/rows.
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    //.border(1.dp, Color.Black)
                    .weight(2f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){

                // Column for the choices A and C, each in their respective rows.
                Column(
                    modifier = modifier
                        .weight(1f)
                ) {
                    // Button for choice A.
                    Button(
                        modifier = modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .weight(1f),
                        // Store the user's answer in userAnswers then go to the next question.
                        onClick =
                        {
                            hygieneTriviaViewModel.storeAnswer(0)
                            // If on last question, reset the index and finish the trivia. Else go to next question.
                            if (triviaIndex.value >= hygieneTriviaViewModel.questions.value.size - 1) {
                                //hygieneTriviaViewModel.resetIndex()
                                hygieneTriviaViewModel.finishTrivia()
                            }
                            else hygieneTriviaViewModel.nextQuestion()
                        },
                        shape = RoundedCornerShape(0.dp),
                        // Kahoot Red.
                        colors = ButtonDefaults.buttonColors(containerColor = Color(255,39,77))
                    ) {
                        Text(text = "▲\n${choices[0]}", textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = modifier.height(1.dp))
                    // Button for choice C
                    Button(
                        modifier = modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .weight(1f),
                        // Store the user's answer in userAnswers then go to the next question.
                        onClick =
                        {
                            hygieneTriviaViewModel.storeAnswer(2)
                            // If on last question, reset the index and finish the trivia. Else go to next question.
                            if (triviaIndex.value >= hygieneTriviaViewModel.questions.value.size - 1) {
                                //hygieneTriviaViewModel.resetIndex()
                                hygieneTriviaViewModel.finishTrivia()
                            }
                            else hygieneTriviaViewModel.nextQuestion()
                        },
                        shape = RoundedCornerShape(0.dp),
                        // Kahoot Yellow
                        colors = ButtonDefaults.buttonColors(containerColor = Color(255, 181, 52))
                    ) {
                        Text(text = "●\n${choices[2]}", textAlign = TextAlign.Center)
                    }
                }

                // Column for the a space
                Column(
                    modifier = modifier
                        .weight(.10f)
                        .fillMaxHeight()
                ){}

                // Column for the choices B and D, each in their respective rows.
                Column(
                    modifier = modifier
                        .weight(1f)
                ) {
                    // Button for choice B.
                    Button(
                        modifier = modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .weight(1f),
                        // Store the user's answer in userAnswers then go to the next question.
                        onClick =
                        {
                            hygieneTriviaViewModel.storeAnswer(1)
                            // If on last question, reset the index and finish the trivia. Else go to next question.
                            if (triviaIndex.value >= hygieneTriviaViewModel.questions.value.size - 1) {
                                //hygieneTriviaViewModel.resetIndex()
                                hygieneTriviaViewModel.finishTrivia()
                            }
                            else hygieneTriviaViewModel.nextQuestion()
                        },
                        shape = RoundedCornerShape(0.dp),
                        // Kahoot Blue
                        colors = ButtonDefaults.buttonColors(containerColor = Color(15, 84, 219))
                    ) {
                        Text(text = "◆\n${choices[1]}", textAlign = TextAlign.Center)
                    }
                    Spacer(modifier = modifier.height(1.dp))
                    // Button for choice D.
                    Button(
                        modifier = modifier
                            .fillMaxSize()
                            .aspectRatio(1f)
                            .weight(1f),
                        // Store the user's answer in userAnswers then go to the next question.
                        onClick =
                        {
                            hygieneTriviaViewModel.storeAnswer(3)
                            // If on last question, reset the index and finish the trivia. Else go to next question.
                            if (triviaIndex.value >= hygieneTriviaViewModel.questions.value.size - 1) {
                                //hygieneTriviaViewModel.resetIndex()
                                hygieneTriviaViewModel.finishTrivia()
                            }
                            else hygieneTriviaViewModel.nextQuestion()
                        },
                        shape = RoundedCornerShape(0.dp),
                        // Kahoot Green
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0, 184, 95))
                    ) {
                        Text(text = "■\n${choices[3]}", textAlign = TextAlign.Center)
                    }
                }
            }

            /*
            // Row for the button to go to the next question.
            Row(
                modifier = modifier
                    //.border(1.dp, Color.Black)
            ) {
                Button(onClick = {hygieneTriviaViewModel.nextQuestion()}, enabled = false) {
                    Text(text="Next Question", textAlign = TextAlign.Center)
                }
            }

             */
        }
    }
    // Column for the timer and demo button.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top=8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {

        // Timer will be above the animated content..
        Text(text = "${hygieneTriviaTimer.value / 1000}s", textAlign = TextAlign.Center,
            fontWeight= FontWeight.Bold, fontSize = 28.sp)
        // Button to demo when the timer reaches zero.
        TextButton(
            onClick = {
                hygieneTriviaViewModel.demoFailed()
            }
        ) {
            Text("Demo Fail")
        }

    }


    // Animate when the user selects an answer.
    AnimatedContent(
        // Animate based off of the current question.
        targetState = triviaIndex.value,
        transitionSpec = { slideInHorizontally(tween(durationMillis = 200, delayMillis = 800))
        {width -> -width} togetherWith fadeOut() + slideOutHorizontally{ width -> width}},
        modifier = modifier
    ) { targetIndex ->
        // Animation whenever the user selects an answer.
        when(targetIndex) {
            else -> triviaUI()
        }
    }

}