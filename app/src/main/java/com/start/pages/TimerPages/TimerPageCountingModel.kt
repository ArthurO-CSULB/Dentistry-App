package com.start.pages.TimerPages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.notificationhandlers.TimerNotificationHandler
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
fun TimerPageCountingModel(modifier: Modifier, navController: NavController, timerViewModel: TimerViewModel) {

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

    // State of whether toggle button for tooth model.
    val timerModelEnabled = timerViewModel.timerModelEnabled.collectAsState()

    // Launched effect to check when timer is canceled or finished.
    LaunchedEffect(timerState.value) {
        when(timerState.value) {
            is TimerState.Cancel -> navController.navigate("timer_cancel")
            is TimerState.Finished -> navController.navigate("timer_finish")
            else -> Unit
        }
    }

    // Launched effect to check tooth has been toggled.
    LaunchedEffect(timerModelEnabled.value) {
        if (!timerModelEnabled.value) navController.navigate("timer_counting")
    }

    // Text that displays the time of the timer, but for when the tooth model is displayed.
    val timerTextModel: @Composable () -> Unit = {
        Text(formatLongToMmSs(toothBrushTimer.value), fontSize = 64.sp, fontWeight = FontWeight.Bold)
    }
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
    // Button to cancel the timer.
    val cancelButton: @Composable () -> Unit = {
        Button(
            onClick = {timerViewModel.cancelTimer()},
            // Set the color of button to red.
            colors=ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(text="Cancel")
        }
    }
    // Button to demo the finish.
    val demoFinishButton: @Composable () -> Unit = {
        Button(
            onClick = {timerViewModel.demoFinish()}
        ) {
            Text(text="Demo Finish")
        }
    }

    // Button to toggle the teeth.
    val toggleTeethButton: @Composable () -> Unit = {
        Button(
            onClick = {timerViewModel.timerModelEnabled.value = !timerViewModel.timerModelEnabled.value},
            colors=ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(text="Toggle Teeth")
        }
    }

    // We create the column for the timer.
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // We display the tooth model.
            Image(
                // Using the painter Resource API we display the image.
                painter = painterResource(id = R.drawable.toothmodel_3_adobestock),
                contentDescription = stringResource(id = R.string.tooth_model_initial),
                // We crop the image to our liking.
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(250.dp)
                    .clip(CircleShape)
            )
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // We display the time counting down right below the tooth model.
            timerTextModel()
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // We display the time counting down right below the tooth model.
            // We display the buttons to pause and cancel the timer.
            pauseResumeButton()
            demoFinishButton()
            // Button to cancel the timer.
            cancelButton()
        }
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Button to toggle teeth.
            toggleTeethButton()
        }
    }
}