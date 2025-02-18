package com.start.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.start.EventViewModel
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat


@Composable
fun WeeklyCalendarPage(modifier: Modifier = Modifier, navController: NavController,
                       eventViewModel: EventViewModel = viewModel(), startDate: String?, endDate: String?) {
    // Get current week and days
    var currentWeek by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    val events by eventViewModel.events.observeAsState(emptyList())

    // Formatting
    val formattedDate = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentWeek.time)

    val weekStart = currentWeek.apply {
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
    }

    // Weekdays
    val weekdays = List(7) {
        val day = weekStart.clone() as Calendar
        day.add(Calendar.DAY_OF_YEAR, it)
        SimpleDateFormat("dd", Locale.getDefault()).format(day.time)
    }

    // Load events for selected week
    LaunchedEffect(currentWeek) {
        eventViewModel.loadEvents()
    }

    // Weekly Calendar Page UI
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Row alignments
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back arrow for previous week
            TextButton(onClick = {
                currentWeek = (currentWeek.clone() as Calendar).apply {
                    add(Calendar.WEEK_OF_YEAR, -1)
                }
            }) {
                Text(
                    text = "<-",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )
            }

            // Format current date
            Text(text = formattedDate, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            // Forward arrow for next week
            TextButton(onClick = {
                currentWeek = (currentWeek.clone() as Calendar).apply {
                    add(Calendar.WEEK_OF_YEAR, 1)
                }
            }) {
                Text(
                    text = "->",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Blue
                )
            }
        }

        // Show days of the week
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),  // 7 columns for the 7 days
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            items(weekdays) { day ->
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(if (selectedDate == day) Color.Gray else Color.White)
                        .clickable {
                            selectedDate = day
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = day, fontSize = 14.sp, color = Color.Black)
                        val dayEvents = events.filter { it.date == day }
                        if (dayEvents.isNotEmpty()) {
                            Text(text = "${dayEvents.size} events", fontSize = 12.sp, color = Color.Red)
                        }
                    }
                }
            }
        }

        // Display selected date's events
        selectedDate?.let { date ->
            Column (horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(modifier = Modifier.height(18.dp))
                Text("Selected Date: $date", fontSize = 18.sp, color = Color.Black)
                Button(onClick = { navController.navigate("addEvent/$date") }) {
                    Text("Add Event")
                }
                // Option to modify selected date's event
                val dayEvents = events.filter { it.date == date }
                dayEvents.forEach { event ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(event.title, fontSize = 16.sp)
                        Row {
                            TextButton(onClick = { navController.navigate("editEvent/${event.eventID}") }) {
                                Text("Edit")
                            }
                            TextButton(onClick = { eventViewModel.deleteEvent(event.eventID) }) {
                                Text("Delete", color = Color.Red)
                            }
                        }
                    }
                }
            }
        }

        // Go home
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home")
        }
    }
}
