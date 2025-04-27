package com.start.pages.timer_pages

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.viewmodels.PointsProgressionViewModel
import com.start.viewmodels.Prestige
import com.start.viewmodels.TimerViewModel
import com.start.viewmodels.TimerState
import kotlin.math.roundToInt

/*
We have a composable timer page which will handle the UI the toothbrush timer.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.

Author Referenced for structure of page: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
Author Referenced for permission API: Meet Patadia
URL: https://meetpatadia9.medium.com/local-notification-in-android-with-jetpack-compose-437b430710f3
 */

@Composable
fun TimerPageCancel(modifier: Modifier, navController: NavController, timerViewModel: TimerViewModel,
                    pointsProgressionViewModel: PointsProgressionViewModel
) {

    // Use BackHandler to intercept the system back button and navigate to the home screen.
    BackHandler {
        // Navigate back to the home screen when the system back button is pressed
        navController.popBackStack("home", inclusive = false)
    }

    // From the passed in TimerViewModel, we get the state flow timerState of the timer and use
    // collectAsState() to subscribe to the state flow and track its changes. page recomposes
    // everytime data changes.
    val timerState = timerViewModel.timerState.collectAsState()

    // Collect the flows of prestige and exp emitted from view model.
    val prestige = pointsProgressionViewModel.prestige.collectAsState()
    val exp = pointsProgressionViewModel.experience.collectAsState()
    // Store the exp value from the previous recomposition of the UI. Be sure to update it when
    // exp changes.
    val expTemp = remember { mutableIntStateOf(exp.value.toInt()) }
    val prestigeTemp = remember { mutableIntStateOf(prestige.value.toInt()) }
    val prestigeInfo: Prestige = pointsProgressionViewModel.prestiges[prestige.value.toInt()]
    // Get the max experience for the current prestige of the user from the array that
    // stores objects of prestiges.
    val maxExp: Long = pointsProgressionViewModel.prestiges[prestigeTemp.intValue].maxExp
    // Variable to store the level progress of the progress bar. Will be updated iteratively
    // in a coroutine to display the progress increasing/decreasing. Initialize it with the initial
    // progress calculated by user exp and max exp for their prestige. Will be remembered across all
    // recompositions.
    var levelProgress by remember { mutableStateOf(exp.value.toFloat() / maxExp.toInt()) }
    // Boolean to store if the progress bar is moving or not.
    var progressMoving by remember { mutableStateOf(false) }

    // Launched effect to update the progress bar reactively to the changes in database.
    LaunchedEffect(exp.value) {
        // Previous percentage the bar was at based off user experience, vs the current
        // percentage the bar is currently at when it increased/decreased.
        val prevPercentage = ((expTemp.intValue.toFloat() / maxExp.toInt()) * 100).toInt()
        val currPercentage = ((exp.value.toFloat() / maxExp.toInt()) * 100).toInt()

        Log.d("PREV PERCENTAGE", prevPercentage.toString())
        Log.d("CURR PERCENTAGE", currPercentage.toString())

        // State that the progress bar is moving.
        progressMoving = true

        // Call back function to update the progress bar. Pass in integer values of percentage.
        // So if you want 10% pass in 10 etc.
        com.start.pages.profile_pages.reactiveProgress(prevPercentage, currPercentage) { progress ->
            // Update the progress bar from the value that is passed in.
            levelProgress = progress
        }

        // When progression is finished, temp values become the current values.
        expTemp.intValue = exp.value.toInt()
        prestigeTemp.intValue = prestige.value.toInt()

        // State that the progress bar is not moving.
        progressMoving = false
    }


    // Launched Effect for navigating back to the beginning.
    LaunchedEffect(timerState.value) {
        when(timerState.value) {
            is TimerState.Begin -> navController.navigate("timer_begin")
            else -> Unit
        }
    }

    // We create the column to display the UI when cancelled.
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Col for image, text and button.
        Column(
            modifier = modifier
                .weight(2f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // We display the sad face.
            Image(
                // Using the painter Resource API we display the image.
                painter = painterResource(id = R.drawable.sadface_timer),
                contentDescription = stringResource(id = R.string.tooth_model_initial),
                // We crop the image to our liking.
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )

            // Text for message.
            Text(
                text="You don't have to brush all your teeth - just the ones you want to keep...",
                fontSize=24.sp,
                textAlign= TextAlign.Center,
                lineHeight=2.em,
                fontWeight = FontWeight.Bold
            )
            // Text for points received.
            Text(
                text="+0",
                fontSize=70.sp,
                textAlign= TextAlign.Center,
                lineHeight=2.em,
                fontWeight= FontWeight.ExtraBold,
                color = Color.Red
            )
            // Restart Button
            Button(
                onClick = {
                    timerViewModel.resetTimer()
                }
            ){
                Text(text="Go Again :)")
            }
        }
        // Col for progress.
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            if (progressMoving) {
                Text("${(maxExp.toInt() * levelProgress).roundToInt()} / ${maxExp.toInt()}")
            } else {
                Text("${exp.value.toInt()} / ${maxExp.toInt()}")
            }

            // Progress Indicator for the points bar.
            LinearProgressIndicator(
                progress = {levelProgress},
                modifier = modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = Color.Green
            )
        }

    }
}