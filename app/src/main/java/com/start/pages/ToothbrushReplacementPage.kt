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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.start.ui.theme.Purple80
import com.start.ui.theme.PurpleGrey40
import androidx.compose.material3.SelectableDates
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.start.viewmodels.ToothbrushTrackerViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date
import java.util.Locale
import java.util.TimeZone

const val FONT_SIZE = 18
const val THREE_MONTHS = 7889238000

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToothbrushReplacementPage(navController: NavController, toothbrushTrackerViewModel: ToothbrushTrackerViewModel) {

    val selectedDate by toothbrushTrackerViewModel.toothbrushGetDate.collectAsState()
    val openDialog = remember { mutableStateOf(false) }
    val datePickerState = remember {
        DatePickerState(
            locale = Locale.getDefault(),
            initialSelectedDateMillis = selectedDate,
            selectableDates = PastOrPresentSelectableDates
    ) }
    val date = datePickerState.selectedDateMillis
    val replacementDate = datePickerState.selectedDateMillis?.plus(THREE_MONTHS)

    LaunchedEffect(selectedDate){
       toothbrushTrackerViewModel.getToothbrushGetDate()
    }

    Scaffold(
        topBar = {CenterAlignedTopAppBar(
            title = {Text("Toothbrush Healthiness Tracker")},
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back to previous page"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Purple80,
                titleContentColor = PurpleGrey40
            ),
            scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        )}

    ) {innerpadding ->
        Column(
            modifier = Modifier
                .padding(innerpadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.padding(20.dp))

            Text(
                text = "You last replaced your toothbrush on: ",
                fontSize = FONT_SIZE.sp,
                textAlign = TextAlign.Center)

            Spacer(Modifier.padding(8.dp))

            Text(text = date.let {convertMillisToDate(it)}.toString(),
                fontSize = 22.sp,
                textAlign = TextAlign.Center)

            Spacer(Modifier.padding(8.dp))

            Text(
                text = "When you should replace your toothbrush: ",
                fontSize = FONT_SIZE.sp,
                textAlign = TextAlign.Center)

            Spacer(Modifier.padding(8.dp))

            Text(
                text = replacementDate.let {convertMillisToDate(it)}.toString(),
                fontSize = 22.sp,
                textAlign = TextAlign.Center)

            Spacer(Modifier.padding(12.dp))

            Text(text = "It is recommended that toothbrushes, both manual and mechanical," +
                    " should be replaced every three (3) months. This is to ensure that you are" +
                    " not brushing your teeth with a toothbrush infested by colonies of bacteria.",
                textAlign = TextAlign.Center,
                fontSize = FONT_SIZE.sp,
                modifier = Modifier.padding(10.dp))

            Spacer(Modifier.padding(18.dp))

            Button(
                onClick = {openDialog.value = true }
            )
            {
                Text(text = "Set Toothbrush Replacement Date",
                    fontSize = FONT_SIZE.sp)
            }

            Spacer(Modifier.padding(10.dp))

            Button(onClick = {toothbrushTrackerViewModel.setToothbrushGetDate(date, replacementDate)}) {
                Text(text = "Save Date", fontSize = FONT_SIZE.sp)
            }

            if (openDialog.value) {
                DatePickerDialog(onDismissRequest = {
                },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openDialog.value = false
                            },
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { openDialog.value = false }) { Text("Cancel") }
                    }
                ) {
                    // The verticalScroll will allow scrolling to show the entire month in case there is not
                    // enough horizontal space (for example, when in landscape mode).
                    // Note that it's still currently recommended to use a DisplayMode.Input at the state in
                    // those cases.

                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier.verticalScroll(rememberScrollState()),
                    )
                }
            }
        }
    }
}

/*
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable

fun TestToothbrush() {
    ToothbrushReplacementPage(rememberNavController())
} */

fun convertMillisToDate(millis: Long?): String {
    val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    val time = if (millis == null) formatter.format(Date(System.currentTimeMillis())) else formatter.format(Date(millis))

    return time
}

@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates: SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        return utcTimeMillis <= System.currentTimeMillis()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun isSelectableYear(year: Int): Boolean {
        return year <= LocalDate.now().year
    }
}

