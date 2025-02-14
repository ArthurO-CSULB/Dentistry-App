package com.start.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.start.EventViewModel

@Composable
fun AddEventPage(navController: NavController, date: String, eventViewModel: EventViewModel = viewModel()) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }

    Spacer(modifier = Modifier.height(15.dp))

    Column(modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Add Event on $date", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") })

        Button(onClick = {
            eventViewModel.addEvent(title, description, date, time)
            navController.popBackStack()
        }) { Text("Save Event") }
    }
}

@Composable
fun EditEventPage(navController: NavController, eventID: String, eventViewModel: EventViewModel = viewModel()) {
    val event = eventViewModel.events.value?.find { it.eventID == eventID }
    var title by remember { mutableStateOf(event?.title ?: "") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var date by remember { mutableStateOf(event?.date ?: "") }
    var time by remember { mutableStateOf(event?.time ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Edit Event", style = MaterialTheme.typography.headlineSmall)
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
        OutlinedTextField(value = time, onValueChange = { time = it }, label = { Text("Time") })

        Row {
            Button(onClick = {
                eventViewModel.updateEvent(eventID, title, description, date, time)
                navController.popBackStack()
            }) { Text("Save Changes") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                eventViewModel.deleteEvent(eventID)
                navController.popBackStack()
            }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Delete")
            }
        }
    }
}
