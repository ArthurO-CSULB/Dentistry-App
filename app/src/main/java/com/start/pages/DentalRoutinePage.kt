package com.start.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.start.notificationhandlers.NotificationHelper
import com.start.ui.theme.Purple80
import com.start.ui.theme.PurpleGrey40
import kotlinx.coroutines.runBlocking
import java.sql.Time
import java.time.Clock
import java.time.Instant.now
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

// Page that details tips for having better dental care and reminders for brushing teeth now
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentalRoutinePage(navController: NavController) {

    // data handlers for popup dialogs
    val openInfoDialog = remember {mutableStateOf(false)}
    val openTimePickerDialog = remember {mutableStateOf(false)}
    var currentDay = getCurrentDay()

    // time picker state to be used for time picking
    val timePickerState = rememberTimePickerState(
        is24Hour = false,
    )

    val morningNotifID = "morningNotif"
    val afternoonNotifID = "afternoonNotif"
    val eveningNotifID = "eveningNotif"



    // handlers for time values extracted from time picker
    var selectedTime = timePickerState.hour * 60 + timePickerState.minute
    var morningTimeHandler = remember { mutableStateOf<Int?>(null)}
    var afternoonTimeHandler = remember { mutableStateOf<Int?>(null)}
    var eveningTimeHandler = remember { mutableStateOf<Int?>(null)}

    // value that determines where to save the time
    var timeDialogOpener = remember {mutableStateOf("empty")}

    val context = LocalContext.current

    // Create a Scaffold for the top bar
    Scaffold(

        // Topbar implementation
        topBar = {
            CenterAlignedTopAppBar(
                title = {Text("Dental Routine Page")}, // top bar title

                // back button implementation
                navigationIcon = {
                    // back button
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to previous page"
                        )
                    }
                                 },

                // display page info and instructions
                actions = {
                IconButton(onClick = { openInfoDialog.value = true}) {
                    Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Display info page"
                    )
                }
            },
            // Specifies Color of the top bar
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Purple80,
                titleContentColor = PurpleGrey40
            )
        )
        }
    )
    { innerPadding ->
        // main column of the page
        Column(
            modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //Display times selected by the user
            Spacer(Modifier.padding(16.dp))
            Text(
                text = "Current set brushing times:",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )

            // Morning time
            Spacer(Modifier.padding(8.dp))

            Row() {
                TextButton(onClick = {
                    timeDialogOpener.value = "Morning"
                    openTimePickerDialog.value = true
                })
                {
                    if (morningTimeHandler.value == null) {
                        Text("Morning: Not set")
                    }
                    else {
                        Text("Morning: ${minutesToTime(morningTimeHandler.value!!)}")
                    }
                }

                if (morningTimeHandler.value != null) {
                    IconButton(onClick = { morningTimeHandler.value = null}) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Morning Notification Time"
                        )
                    }
                }
            }

            Row() {
                // Afternoon time
                TextButton(onClick = {
                    timeDialogOpener.value = "Afternoon"
                    openTimePickerDialog.value = true
                }
                ) {
                    if (afternoonTimeHandler.value == null) {
                        Text("Afternoon: Not set")
                    }
                    else {
                        Text("Afternoon: ${minutesToTime(afternoonTimeHandler.value!!)}")
                    }
                }
                if (afternoonTimeHandler.value != null) {
                    IconButton(onClick = { afternoonTimeHandler.value = null}) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Afternoon Notification Time"
                        )
                    }
                }
            }

            Row() {
                // Evening time
                TextButton(onClick = {
                    timeDialogOpener.value = "Evening"
                    openTimePickerDialog.value = true
                })
                {
                    if (eveningTimeHandler.value == null) {
                        Text("Evening: Not set")
                    }
                    else {
                        Text("Evening: ${minutesToTime(eveningTimeHandler.value!!)}")
                    }
                }
                if (eveningTimeHandler.value != null) {
                    IconButton(onClick = { eveningTimeHandler.value = null}) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove Afternoon Notification Time"
                        )
                    }
                }
            }



            // Save the times inputted
            Spacer(Modifier.padding(16.dp))
                Button(onClick = {
                    NotificationHelper(context).cancelScheduledNotification(morningNotifID)
                    NotificationHelper(context).cancelScheduledNotification(afternoonNotifID)
                    NotificationHelper(context).cancelScheduledNotification(eveningNotifID)
                    NotificationHelper(context).cancelScheduledNotification("testNotifID") //  testing
                    if (morningTimeHandler.value != null) {
                        NotificationHelper(context).scheduleNotification(
                            eventID = morningNotifID,
                            timeInMillis = currentDay + (morningTimeHandler.value!! * 60 * 1000),
                            title = "Morning Brush Reminder",
                            description = "It's time to brush your teeth!")
                    }
                    if (afternoonTimeHandler.value != null) {
                        NotificationHelper(context).scheduleNotification(
                            eventID = afternoonNotifID,
                            timeInMillis = currentDay + (afternoonTimeHandler.value!! * 60 * 1000),
                            title = "Afternoon Brush Reminder",
                            description = "It's time to brush your teeth!")
                    }
                    if (eveningTimeHandler.value != null) {
                        NotificationHelper(context).scheduleNotification(
                            eventID = eveningNotifID,
                            timeInMillis = currentDay + (eveningTimeHandler.value!! * 60 * 1000),
                            title = "Evening Brush Reminder",
                            description = "It's time to brush your teeth!")
                    }
                    // test notification
                    if (morningTimeHandler.value != null) {
                        NotificationHelper(context).scheduleNotification(
                            eventID = "testNotif",
                            timeInMillis = System.currentTimeMillis() + 5000,
                            title = "Test Brush Reminder",
                            description = "It's time to brush your teeth!")
                    }
                }) {
                    Text("Save times")
                }

            // Tips for having great dental hygiene
            Spacer(Modifier.padding(24.dp))
            Text(
                text = "Tips for having great dental hygiene:",
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(2.dp))
            Text(
                text = "Brush your teeth at least twice a day.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(2.dp))
            Text(
                text = "Floss once a day.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(2.dp))
            Text(
                text = "Limit sugary foods and drinks.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(2.dp))
            Text(
                text = "Drink lots of water.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(2.dp))
            Text(
                text = "Avoid smoking.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(2.dp))
            Text(
                text = "See a dentist regularly for cleanings and checkups.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(2.dp))
            Text(
                text = "When in doubt, go see a dentist.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(2.dp))
            Spacer(Modifier.padding(40.dp))
            }
        }

    // Pop up Info Dialog when button is pressed
    if (openInfoDialog.value) {
        InfoDialog(
            onDismissRequest = { openInfoDialog.value = false },
            dialogTitle = "Page Info",
            dialogText = "Give yourself a reminder to brush your teeth by setting times in this page." +
                    "We will send a notification when that time is reached.",
            icon = Icons.Default.Info
        )
    }

    // Open time picker dialog if buttons are pressed
    if (openTimePickerDialog.value) {
        SelectableTimePickerDialog(
            onDismiss = { openTimePickerDialog.value = false },
            onConfirm = {
                openTimePickerDialog.value = false
            },
            title = timeDialogOpener.value
        ) {
            TimePicker(
                state = timePickerState,
            )
        }
    } else {
        if (timeDialogOpener.value != "empty") {
            if (timeDialogOpener.value == "Morning") {
                morningTimeHandler.value = selectedTime
            }
            else if (timeDialogOpener.value == "Afternoon") {
                afternoonTimeHandler.value = selectedTime
            }
            else {
                eveningTimeHandler.value = selectedTime
            }
        }
    }


}

// Preview page
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TestDentalPage() {
        DentalRoutinePage(rememberNavController())
}

// Implementation of Info Dialog, mayh be reused by other pages
@Composable
fun InfoDialog(
    onDismissRequest: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {},
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

// Implementation of Time Picker Dialog, reusable
@Composable
fun SelectableTimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() },
        title = {Text("Select $title brushing time")}
    )
}

fun minutesToTime(time: Int): String {
    val meridian = if (time < 720) "AM" else "PM"
    var hours = if (time < 720) time / 60 else (time - 720) / 60
    if (hours == 0) hours = 12
    val minutes = time % 60

    return String.format("%02d:%02d $meridian", hours, minutes)
}

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentDay(): Long {
    val currentDate = LocalDate.now().atStartOfDay().toEpochSecond(ZoneId.of("UTC").rules.getOffset(LocalDateTime.now())) * 1000
    return currentDate
}
