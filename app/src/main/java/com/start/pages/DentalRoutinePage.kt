package com.start.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.start.ui.theme.Purple80
import com.start.ui.theme.PurpleGrey40

// Page that details tips for having better dental care and reminders for brushing teeth now
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DentalRoutinePage(navController: NavController) {

    // data handlers for popup dialogs
    val openInfoDialog = remember {mutableStateOf(false)}
    val openTimePickerDialog = remember {mutableStateOf(false)}

    // time picker state to be used for time picking
    val timePickerState = rememberTimePickerState(
        is24Hour = false,
    )

    // handlers for time values extracted from time picker
    var selectedTime = timePickerState.hour * 60 + timePickerState.minute
    var morningTimeHandler = remember { mutableIntStateOf(0)}
    var afternoonTimeHandler = remember { mutableIntStateOf(0)}
    var eveningTimeHandler = remember { mutableIntStateOf(0)}

    // value that determines where to save the time
    var timeDialogOpener = "empty"

    // Create a Scaffold for the topbar
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
            TextButton(onClick = {
                timeDialogOpener = "morning"
                openTimePickerDialog.value = true
            })
            {
                Text("Morning: ${morningTimeHandler.intValue}")
            }

            // Afternoon time
            TextButton(onClick = {
                timeDialogOpener = "afternoon"
                openTimePickerDialog.value = true
            }
            ) {
                Text("Afternoon: ${afternoonTimeHandler.intValue}")
            }

            // Evening time
            TextButton(onClick = {
                timeDialogOpener = "evening"
                openTimePickerDialog.value = true
            }
            )
            {
                Text("Evening: ${eveningTimeHandler.intValue}")
            }

            // Save the times inputted
            Spacer(Modifier.padding(16.dp))
                Button(onClick = {}) {
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
                    "The program will then give you a notification when that time is reached.",
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
        ) {
            TimePicker(
                state = timePickerState,
            )
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
        text = { content() }
    )
}
