package com.start.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


/*
We have a composable calendar page which will handle the UI for the calendar.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */

@Composable
fun CalendarPage(modifier: Modifier = Modifier, navController: NavController) {
    // Get current month and day
    var currentMonth by remember { mutableStateOf(Calendar.getInstance()) }
    var selectedDate by remember { mutableStateOf(getTodayDate()) }
    val formattedDate =
        SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(currentMonth.time)

    // Calendar Page UI
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Title of Calendar Page
        Text(
            text = "Calendar",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Row alignments
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            // Back arrow for previous month
            TextButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply {
                    add(Calendar.MONTH, -1)
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
            Text(text = formattedDate,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)

            // Forward arrow for next month
            TextButton(onClick = {
                currentMonth = (currentMonth.clone() as Calendar).apply {
                    add(Calendar.MONTH, 1)
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
            // Spacer
            Spacer(modifier = Modifier.height(16.dp))

            // Weekdays
            val weekdays = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                weekdays.forEach { day ->
                    Text(text = day,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray)
                }
            }

            // Spacer
            Spacer(modifier = Modifier.height(10.dp))

            // Grid format for calendar
            val daysInMonth = generateDaysInMonth(currentMonth)

            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier.weight(1f)
            ) {
                items(daysInMonth.size) { index ->
                    val day = daysInMonth[index]
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color.White)
                            .clickable {
                                if (day.isNotEmpty()) {
                                    selectedDate = "$day ${formattedDate}"
                                }
                            },
                        contentAlignment = Alignment.Center
                    )
                    {
                        Text(
                            text = day,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }

            selectedDate?.let {
                Text(text = "Selected Date: $it",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface)
            }
            // Go home
            TextButton(onClick = {
                navController.navigate("home")
            }) {
                Text(text = "Home")
            }

            // Weekly View
            TextButton(onClick = {
                val (weekStart, weekEnd) = getCurrentWeekRange()
                val encodedStart = URLEncoder.encode(weekStart, StandardCharsets.UTF_8.toString())
                val encodedEnd = URLEncoder.encode(weekEnd, StandardCharsets.UTF_8.toString())

                navController.navigate("weeklyCalendar/$encodedStart/$encodedEnd")
            }) {
                Text(text = "Weekly")
            }
        }
    }

// Function to ensure that each month has accurate amount of days
fun generateDaysInMonth(calendar: Calendar): List<String> {
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val firstDayOfMonth = Calendar.getInstance().apply{
        set(year, month, 1)
    }

    val daysInMonth = firstDayOfMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1

    val listOfDays = mutableListOf<String>()

    for (i in 1..42) {
        if (i <= firstDayOfWeek || i > daysInMonth + firstDayOfWeek){
            listOfDays.add("")
        }
        else {
            listOfDays.add((i - firstDayOfWeek).toString())
        }
    }
    return listOfDays
}

// Get date of today
fun getTodayDate(): String {
    val calendar = Calendar.getInstance()
    return SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(calendar.time)
}

// Function to get start and end dates of the current week
fun getCurrentWeekRange(): Pair<String, String> {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    val weekStart = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(calendar.time)

    calendar.add(Calendar.DAY_OF_WEEK, 6) // Move to Saturday
    val weekEnd = SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(calendar.time)

    return Pair(weekStart, weekEnd)
}

// Preview code
@Preview(showBackground = true)
@Composable
fun PreviewCalendar(){
    val testNavController = rememberNavController()
    CalendarPage(navController = testNavController)
}