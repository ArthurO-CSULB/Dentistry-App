package com.start.pages

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.start.EventViewModel
import com.start.notificationhandlers.TimerNotificationHandler
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun AddEventPage(navController: NavController, date: String, eventViewModel: EventViewModel = viewModel()) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(15.dp))

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(modifier = Modifier.height(32.dp))

        Text("Add Event on $date", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })

        // Set the time for alarm
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker(context) { selectedTime -> time = selectedTime } }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Selected time: $time",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp))
        }

        // Save Event
        Button(onClick = {
            val formattedDate = SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).parse(date) ?: Date())
            eventViewModel.addEvent(title, description, formattedDate, time)

            navController.popBackStack()
        }) { Text("Save Event") }

        // Space between save event and cancel
        Spacer(modifier = Modifier.height(15.dp))

        // Cancel
        Button(
            onClick = { navController.popBackStack() }, // Go back without saving
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) { Text("Cancel") }
    }
}

// Edit event page for when we want to modify previously-made events
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun EditEventPage(navController: NavController, date:String, eventID: String, eventViewModel: EventViewModel = viewModel()) {
    // Retrieve the event from ViewModel using the eventID
    val event = eventViewModel.events.value?.find { it.eventID == eventID }
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var time by remember { mutableStateOf(event?.time ?: "") }
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(15.dp))

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(modifier = Modifier.height(32.dp))

        Text("Edit Event on $date", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })

        // Set the time for the alarm (same as in AddEventPage)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showTimePicker(context) { selectedTime -> time = selectedTime } }
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Selected time: $time",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Save the event
        Button(onClick = {
            val formattedDate = SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault()).parse(date) ?: Date())
            eventViewModel.updateEvent(eventID, title, description, formattedDate, time)
            navController.popBackStack()
        }) { Text("Save Changes") }

        // Space between save and cancel buttons
        Spacer(modifier = Modifier.height(15.dp))

        // Cancel button
        Button(
            onClick = { navController.popBackStack() }, // Go back without saving
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
        ) { Text("Cancel") }
    }
}

// Function for time selector
fun showTimePicker(context: Context, onTimeSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            val amPm = if (selectedHour >= 12) "PM" else "AM"
            val hourFormatted = if (selectedHour % 12 == 0) 12 else selectedHour % 12
            val formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourFormatted, selectedMinute, amPm)
            onTimeSelected(formattedTime)
        },
        hour,
        minute,
        false // Use 12-hour format
    ).show()
}

// Function that allows user to get permission
fun requestExactAlarmPermission(context: Context) {
    val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
    context.startActivity(intent)
}

// Helper function to parse the time string
fun parseTime(time: String): Pair<Int, Int> {
    val parts = time.split(":")
    val hour = parts[0].toInt()
    val minute = parts[1].split(" ")[0].toInt()
    val amPm = parts[1].split(" ")[1]
    return if (amPm == "PM" && hour != 12) {
        Pair(hour + 12, minute)
    } else {
        Pair(hour, minute)
    }
}

// Use parseTime to include date
fun parseDateTime(date: String, time: String): Calendar {
    val calendar = Calendar.getInstance()
    val (hour, minute) = parseTime(time) // Parse the time

    // Parse the year, month, and day
    val parts = date.split("-")
    val year = parts[0].toInt()
    val month = parts[1].toInt() - 1
    val day = parts[2].toInt()

    // Set calendar information
    calendar.set(year, month, day, hour, minute, 0)
    Log.d("DateTime", "Date received: $date")

    return calendar
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title") ?: "Reminder"
        val description = intent.getStringExtra("description") ?: "You have an event!"

        // Display the notification
        val notificationHandler = TimerNotificationHandler(context)
        notificationHandler.timerFinishedNotification(title, description)
    }
}