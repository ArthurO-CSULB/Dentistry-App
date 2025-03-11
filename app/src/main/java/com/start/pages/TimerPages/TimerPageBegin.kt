package com.start.pages.TimerPages

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun TimerPageBegin(modifier: Modifier, navController: NavController, timerViewModel: TimerViewModel) {

    // Use BackHandler to intercept the system back button and navigate to the home screen.
    BackHandler {
        // Navigate back to the home screen when the system back button is pressed
        navController.popBackStack("home", inclusive = false)
    }

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


    // Upon recomposition, check if POST_NOTIFICATIONS is granted or not. If it is not, launch
    // a permission request for it.
    LaunchedEffect(postNotificationPermission.status) {
        if (!postNotificationPermission.status.isGranted) {
            postNotificationPermission.launchPermissionRequest()
        }
    }

    // When in a starting state, we navigate to the main UI for counting.
    LaunchedEffect(timerState.value) {
        when(timerState.value) {
            is TimerState.Counting -> navController.navigate("timer_counting")
            is TimerState.Finished -> navController.navigate("timer_finish")
            is TimerState.Cancel -> navController.navigate("timer_cancel")
            else -> Unit
        }
    }

    // Text that displays the time of the timer.
    val timerText: @Composable () -> Unit = {
        Text(formatLongToMmSs(toothBrushTimer.value), fontSize = 128.sp, fontWeight = FontWeight.Bold)
    }
    Column (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the timer text.
        timerText()
        // Create a button to start the timer.
        Button(onClick = {timerViewModel.startTimer()}) {
            Text(text="Start")
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