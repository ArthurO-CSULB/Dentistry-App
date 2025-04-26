package com.start.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.ui.theme.Purple80
import com.start.ui.theme.PurpleGrey40
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.start.viewmodels.ToothbrushTrackerViewModel
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import com.start.notificationhandlers.NotificationHelper


const val THREE_MONTHS = 7889238000


// Toothbrush RepLacement Page where user can set a date on when they got their toothbrush.
// User will then be notified 3 months after the set date as a reminder to change their toothbrush
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToothbrushReplacementPage(navController: NavController, toothbrushTrackerViewModel: ToothbrushTrackerViewModel) {

    val fontSize = 18
    val toothbrushUuid = "_toothbrushID"
    val notifTitle = "Toothbrush Replacement Reminder"
    val notifDescSucc = "It has been 3 months since you last replaced your toothbrush. Replace it now."
    val notifDescFail = "There has been an issue setting your notification date. Please try again."

    // date acquired from the database
    val selectedDate by toothbrushTrackerViewModel.toothbrushGetDate.collectAsState()

    // checks if the date picker should be displayed or not
    val openDialog = remember { mutableStateOf(false) }

    // a date picker that handles the date selection
    val datePickerState = remember {
        DatePickerState(
            locale = Locale.getDefault(),
            initialSelectedDateMillis = selectedDate,
            selectableDates = PastOrPresentSelectableDates
    ) }

    // context to be used for notifications
    val context = LocalContext.current

    // handlers for showing the selected date and replacement date
    // also used as paramters for setter functions
    var setDate = datePickerState.selectedDateMillis
    var replacementDate = setDate?.plus(THREE_MONTHS)

    // when app is launched, get the get date from the database
    LaunchedEffect(selectedDate){
       toothbrushTrackerViewModel.getToothbrushGetDate()
    }

    // Creation of a scaffold to have a top bar and navigation buttons automatically laid out
    Scaffold(

        // topBar specifications
        topBar = {CenterAlignedTopAppBar(
            title = {Text("Toothbrush Healthiness Tracker")} ,  // title
            navigationIcon = {
                //  back button of the top bar
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to previous page"
                    )
                }
            },
            // colors of the top bar
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Purple80,
                titleContentColor = PurpleGrey40
            ),
            // scroll behaviour
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        )}

        // body of the scaffold
    ) {innerPadding ->

        // Creation of column composable that handles all the UI elements of the body
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Segment that shows the get date of the toothbrush
            Spacer(Modifier.padding(20.dp))
            Text(
                text = "You last replaced your toothbrush on",
                fontSize = fontSize.sp,
                textAlign = TextAlign.Center)
            Spacer(Modifier.padding(8.dp))
            Text(text = if (setDate == null) convertMillisToDate(selectedDate) else convertMillisToDate(setDate),
                fontSize = 22.sp,
                textAlign = TextAlign.Center)

            // segment that shows the replacement date of the toothbrush
            Spacer(Modifier.padding(8.dp))
            Text(
                text = "When you should replace your toothbrush: ",
                fontSize = fontSize.sp,
                textAlign = TextAlign.Center)
            Spacer(Modifier.padding(8.dp))
            Text(
                text = if (setDate == null) convertMillisToDate(selectedDate) else convertMillisToDate(replacementDate),
                fontSize = 22.sp,
                textAlign = TextAlign.Center)

            // Rationale for making this page
            Spacer(Modifier.padding(12.dp))
            Text(text = "It is recommended that toothbrushes, both manual and mechanical," +
                    " should be replaced every three (3) months. This is to ensure that you are" +
                    " not brushing your teeth with a toothbrush infested by colonies of bacteria.",
                textAlign = TextAlign.Center,
                fontSize = fontSize.sp,
                modifier = Modifier.padding(10.dp))

            // Button for making the dialog picker appear
            Spacer(Modifier.padding(18.dp))
            Button(
                onClick = {openDialog.value = true }
            )
            {
                Text(text = "Set Toothbrush Replacement Date",
                    fontSize = fontSize.sp)
            }

            // Button for saving the selected date
            Spacer(Modifier.padding(10.dp))
            Button(
                onClick = {
                    // if date is saved, set date in database\
                    // create a notification scheduled in the replacement date
                    // then set the two handlers to null
                    toothbrushTrackerViewModel.setToothbrushGetDate(setDate, replacementDate)
                    NotificationHelper(context).cancelScheduledNotification(toothbrushUuid)
                    NotificationHelper(context).scheduleNotification(
                        eventID = toothbrushUuid,
                        timeInMillis = replacementDate?.toLong() ?: (System.currentTimeMillis() + 5000),
                        title = notifTitle,
                        description = if (replacementDate == null) notifDescFail else notifDescSucc)
                    setDate = null
                    replacementDate = null
                },
                // button only works if date picker date is different from get date in database
                enabled = setDate != selectedDate && setDate != null
            )
            {
                Text(text = "Save Date",
                    fontSize = fontSize.sp)
            }

            Spacer(Modifier.padding(18.dp))
            Button(
                onClick = {NotificationHelper(context).scheduleNotification(
                    eventID = "testNotification",
                    timeInMillis = (System.currentTimeMillis() + 5000),
                    title = notifTitle,
                    description = if (replacementDate == null) notifDescFail else notifDescSucc)
                }
            )
            {
                Text(text = "Send Test Notification",
                    fontSize = fontSize.sp)
            }

            // logic if the dialog is open
            if (openDialog.value) {
                //create a date picker dialog for handling user input for the date
                DatePickerDialog(onDismissRequest = {
                },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                            },
                        ) {
                            Text("Choose Date")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDialog.value = false }) { Text("Cancel") }
                    }
                ) {
                    // Pass the date picker to the date picker dialog so dialog can modify the state
                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                    )
                }
            }
        }
    }
}

// helper function for displaying the correct time to the user, regardless of time zone
@RequiresApi(Build.VERSION_CODES.O)
fun convertMillisToDate(millis: Long?): String {
    val offset = OffsetDateTime.now(ZoneId.systemDefault()).offset.totalSeconds * 1000
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    val time = if (millis ==  null)  "No date selected" else formatter.format(Date(millis - offset))
    return time
}

// helper object for the date picker
@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates: SelectableDates {
    @RequiresApi(Build.VERSION_CODES.O)

    // limits the date picker to only let user select past dates up to three months before the current time
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        val offset = OffsetDateTime.now(ZoneId.systemDefault()).offset.totalSeconds * 1000
        return (utcTimeMillis <= System.currentTimeMillis()+offset) and (utcTimeMillis >= System.currentTimeMillis() - THREE_MONTHS + offset)
    }
}


