package com.start.pages.TimerPages

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
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

// an annotation that restricts a function or class to be used only on devices running a certain
// API level or higher - Android 13 (API level 33/TIRAMISU). Using POST_NOTFICATIONS requires this
// level.
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
// Enable use of the accompanist permissions api, which is marked as experimental (new/evolving).
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimerPage(modifier: Modifier = Modifier, navController: NavController,
              timerViewModel: TimerViewModel) {

    // Initialize and track the notification permission state using accompanist-permissions api.
    val postNotificationPermission =
        rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

    // From the passed in TimerViewModel, we get the state flow timerState of the timer and use
    // collectAsState() to subscribe to the state flow and track its changes. page recomposes
    // everytime data changes.
    val timerState = timerViewModel.timerState.collectAsState()

    // From the passed in TimerViewModel, we get the state flow toothbrush timer and use
    // collectAsState() to subscribe to the state flow and track its changes. Page recomposes
    // everytime data changes.
    val toothBrushTimer = timerViewModel.toothBrushTimer.collectAsState()

    // Notifications for the timer. Pass in the context.
    val timerNotifications = TimerNotificationHandler(LocalContext.current)

    // Timer fun facts for the timer.
    val timerFunFact = timerViewModel.timerFact.collectAsState()

    // Boolean variable which will be used to toggle the tooth model. Initialized as false.
    var toggleToothModel by remember {mutableStateOf(false)}

    // Text that displays the time of the timer.
    val timerText: @Composable () -> Unit = {
        Text(formatLongToMmSs(toothBrushTimer.value), fontSize = 128.sp, fontWeight = FontWeight.Bold)
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
            onClick = {toggleToothModel = !toggleToothModel},
            colors=ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(text="Toggle Teeth")
        }
    }

    // Upon recomposition, check if POST_NOTIFICATIONS is granted or not. If it is not, launch
    // a permission request for it.
    LaunchedEffect(postNotificationPermission.status) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }
    // We create a launched effect that passes in the value of the timer state. Upon
    // the value changing when calling TimerViewModel methods, the block of code will execute.
    LaunchedEffect(timerState.value) {
        when (timerState.value) {
            is TimerState.Finished -> {
                timerNotifications.timerFinishedNotification()
            }

            else -> Unit
        }
    }
    // Reference for animations
    // URL: https://developer.android.com/develop/ui/compose/animation/composables-modifiers#animatedcontent
    // Timer Page UI
    // UI appears different depending on the state of the Timer.
    // Animated Content that animates composables based on the state.
    AnimatedContent(
        // The state we will animate based off of will be the state of the timer.
        targetState = timerState.value,
        // We Describe the transitions.
        transitionSpec = {
            // We exempt the pause state from any animated transitions.
            if (targetState == TimerState.Pause || targetState == TimerState.Resumed) {
                // No enter or exit transitions.
                EnterTransition.None togetherWith ExitTransition.None
            }
            // We have a fading in and fading out transition for all the other states.
            else {
                fadeIn(animationSpec = tween(1000, 500)) togetherWith fadeOut()
            }
        },
        // Name the animation.
        label = "TimerTransitions"
    ) {
        // The target state which is the timer state.
        stateOfTimer ->
        // Depending on the state of the timer...
        when (stateOfTimer) {
            // When the timer is at the Begin state.
            TimerState.Begin -> {
                // Display the UI for the Begin state.
                navController.navigate("timer_begin")
            }
            // When the timer is at the Counting State or Pause State.
            TimerState.Counting, TimerState.Pause, TimerState.Resumed -> {
                // Display UI depending on whether the tooth model is toggled or not.
                when (toggleToothModel) {
                    // When the tooth model is appearing.
                    true -> {
                        navController.navigate("timer_counting_model")
                    }
                    // UI When the tooth model is not appearing.
                    false -> {

                    }
                }
            }
            // When the timer is at the Canceled State.
            TimerState.Cancel -> {
                navController.navigate("timer_cancel")
            }
            // When the timer is at the Finished State.
            TimerState.Finished -> {
                navController.navigate("timer_finish")
            }
            else -> Unit
        }
    }


}

// Method to format the timer from Long to m:ss
// Referenced from https://www.baeldung.com/java-ms-to-hhmmss
fun formatLongToMmSs(timeInMillis: Long): String {
    // Convert milliseconds to total seconds.
    val totalSeconds = timeInMillis / 1000
    // Convert total seconds to total amount of minutes.
    val minutes = totalSeconds / 60
    // Get the remaining seconds from the total minutes.
    val seconds = totalSeconds % 60

    // Format to m:ss.
    return String.format("%01d:%02d", minutes, seconds)
}