package com.start.pages.timer_pages

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.viewmodels.TimerState
import com.start.viewmodels.TimerViewModel

/*
We have a composable timer page which will handle the UI the toothbrush timer.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.

Author Referenced for structure of page: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
Author Referenced for permission API: Meet Patadia
URL: https://meetpatadia9.medium.com/local-notification-in-android-with-jetpack-compose-437b430710f3
 */

@Composable
fun TimerPageCounting(modifier: Modifier, navController: NavController, timerViewModel: TimerViewModel) {

    // Use BackHandler to intercept the system back button and navigate to the home screen.
    BackHandler {
        // Navigate back to the home screen when the system back button is pressed
        navController.popBackStack("home", inclusive = false)
    }

    // From the passed in TimerViewModel, we get the state flow timerState of the timer and use
    // collectAsState() to subscribe to the state flow and track its changes. page recomposes
    // everytime data changes.
    val timerState = timerViewModel.timerState.collectAsState()

    // From the passed in TimerViewModel, we get the state flow toothbrush timer and use
    // collectAsState() to subscribe to the state flow and track its changes. Page recomposes
    // everytime data changes.
    val toothBrushTimer = timerViewModel.toothBrushTimer.collectAsState()

    // Timer fun facts for the timer.
    val timerFunFact = timerViewModel.timerFact.collectAsState()

    // State of whether toggle button for tooth model.
    val timerModelEnabled = timerViewModel.timerModelEnabled.collectAsState()


    // Launched Effect to check if timer is canceled or finished.
    LaunchedEffect(timerState.value) {
        when(timerState.value) {
            is TimerState.Cancel -> navController.navigate("timer_cancel")
            is TimerState.Finished -> navController.navigate("timer_finish")
            else -> Unit
        }
    }

    // Launched Effect to check if tooth model is toggled.
    LaunchedEffect(timerModelEnabled.value) {
        if (timerModelEnabled.value) navController.navigate("timer_counting_model")
    }

    // Image of play/pause button that acts as a button to pause and resume the timer.
    val pauseResumeImage: @Composable () -> Unit = {
        // Store the state of the pause button.

        // If paused show play button. Else show pause button.
        Image(
            painter = painterResource(id = if (timerState.value == TimerState.Pause) R.drawable.timer_resume else R.drawable.timer_pause),
            contentDescription = "Pause/Resume Button",
            modifier = modifier
                .size(48.dp)
                .clickable {
                    // Depending on the timer state...
                    when (timerState.value) {
                        // when the timer is counting or resumed, we pause the timer.
                        TimerState.Counting, TimerState.Resumed -> {
                            timerViewModel.pauseTimer()
                        }
                        // when the timer is paused, we start the timer.
                        TimerState.Pause -> timerViewModel.startTimer()
                        // else nothing.
                        else -> Unit
                    }
                }
        )
    }

    /*
    // Button to pause and resume the timer.
    val pauseResumeButton: @Composable () -> Unit = {
        Button(
            onClick = {
                // Depending on the timer state...
                when (timerState.value) {
                    // when the timer is counting or resumed, we pause the timer.
                    TimerState.Counting, TimerState.Resumed -> {
                        timerViewModel.pauseTimer()
                    }
                    // when the timer is paused, we start the timer.
                    TimerState.Pause -> timerViewModel.startTimer()
                    // else nothing.
                    else -> Unit
                }
            },

            colors = ButtonDefaults.buttonColors(
                // If the timer is counting, the button is red. if it is paused it is green.
                containerColor = if (timerState.value == TimerState.Counting ||
                    timerState.value == TimerState.Resumed) Color.DarkGray else Color.Green
            )) {
            // Depending on the state of the timer
            Text(
                text =
                // If the timer is counting or resumed, we display pause.
                if (timerState.value == TimerState.Counting ||
                    timerState.value == TimerState.Resumed) "Pause"
                // If the timer is paused, we display resume.
                else "Resume"
            )
        }
    }
     */

    // Image of cancel button, acts as button to cancel the timer.
    val cancelImage: @Composable () -> Unit = {
        Image(
            painter = painterResource(id = R.drawable.timer_cancel),
            contentDescription = "Cancel Button",
            modifier = modifier
                .size(48.dp)
                .clickable {
                    timerViewModel.cancelTimer()
                }
        )
    }

    /*

    // Button to cancel the timer.
    val cancelButton: @Composable () -> Unit = {
        Button(
            onClick = {timerViewModel.cancelTimer()},
            // Set the color of button to red.
            colors= ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text="Cancel")
        }
    }

     */
    // Button to demo the finish.
    val demoFinishButton: @Composable () -> Unit = {
        Button(
            onClick = {timerViewModel.demoFinish()}
        ) {
            Text(text="Demo Finish")
        }
    }

    val toggleTeethImage: @Composable () -> Unit = {
        Image(
            painter = painterResource(id = R.drawable.timer_teeth_toggle),
            contentDescription = "Toggle Teeth Button",
            modifier = modifier
                .size(48.dp)
                .clickable {
                    timerViewModel.toggleTeeth()
                }
        )
    }

    /*

    // Button to toggle the teeth.
    val toggleTeethButton: @Composable () -> Unit = {
        Button(
            onClick = {timerViewModel.toggleTeeth()},
            colors= ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(text="Toggle Teeth")
        }
    }

     */

    // Text that displays the time of the timer.
    val timerText: @Composable () -> Unit = {
        Text(formatLongToMmSs(toothBrushTimer.value), fontSize = 128.sp, fontWeight = FontWeight.Bold)
    }

    Column(
        modifier = modifier
            //.border(width = 2.dp, color = Color.Black)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the timer above.
        timerText()

        Column(
            modifier = modifier
                .weight(2f)
                //.border(width = 2.dp, color = Color.Black)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // Animate the fun facts.
            AnimatedContent(
                targetState=timerFunFact.value,
                // slideInVertically{ height -> -height} +
                transitionSpec= { fadeIn(animationSpec = tween(1000, 800)) togetherWith slideOutVertically{ height -> height} + fadeOut() },
                label="Fun Fact Transitions"
            ) {
                    timerFunFactState ->
                // Display fun facts. When the fun fact changes from the
                // view model, we display a new one animated.
                Text(
                    text="$timerFunFactState",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontStyle = FontStyle.Italic
                    ),
                    textAlign= TextAlign.Center,
                    lineHeight=2.em
                )
            }
        }
        Column(
            modifier = modifier
                .weight(1f),
                //.border(width = 2.dp, color = Color.Black)
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = modifier
                    //.border(width = 2.dp, color = Color.Black)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Display the pauseResume button and the cancel button.
                pauseResumeImage()
                toggleTeethImage()
                cancelImage()
            }


        }

        // Button to toggle the teeth.
        demoFinishButton()

    }
}