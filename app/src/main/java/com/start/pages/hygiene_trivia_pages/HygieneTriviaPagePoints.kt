package com.start.pages.hygiene_trivia_pages

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.viewmodels.HygieneTriviaState
import com.start.viewmodels.HygieneTriviaViewModel
import com.start.viewmodels.PointsProgressionViewModel
import com.start.viewmodels.Prestige
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun HygieneTriviaPagePoints(modifier: Modifier, navController: NavController,
                            hygieneTriviaViewModel: HygieneTriviaViewModel,
                            pointsProgressionViewModel: PointsProgressionViewModel
) {
    val hygieneTriviaState = hygieneTriviaViewModel.hygieneTriviaState.collectAsState()
    val numCorrect = hygieneTriviaViewModel.numCorrect()

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
    var progressMoving by remember {mutableStateOf(false)}

    // Navigate Home when the state of the trivia changes to begin.
    LaunchedEffect(hygieneTriviaState.value) {
        when(hygieneTriviaState.value) {
            is HygieneTriviaState.Begin -> navController.navigate("home")
            else -> Unit
        }
    }

    // Launched effect to update the progress bar reactively to the changes in database.
    LaunchedEffect(exp.value) {
        //delay(2000)
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

    // Add points to to the user's account.
    LaunchedEffect(Unit) {
        pointsProgressionViewModel.addTriviaPoints(numCorrect.toLong())
    }

    if (hygieneTriviaState.value != HygieneTriviaState.Begin) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .weight(1f)
                    //.border(3.dp, color = Color.Black)

            ) {
                // Display the number of correct answers the user got.
                Text(
                    text = "You got $numCorrect out of 5 correct!",
                    fontSize = 40.sp,
                    lineHeight = 1.5.em,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "+${numCorrect * 10}",
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 1.5.em,
                    textAlign = TextAlign.Center,
                    color = Color.Green
                )

            }

            Button(
                onClick = {
                    hygieneTriviaViewModel.resetTrivia()
                }
            ) {
                Text("Go Home")
            }

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


}

// Method that when the exp value changes, will update the progress bar. Copied from points progress page.
// Should be in its own object in the future.
suspend fun reactiveProgress(prevPercent: Int = 0, currPercent: Int = 0, updateProgress: (Float) -> Unit) {
    // max delay and min delay
    val maxDelay = 15L
    val minDelay = 5L
    // Variable to store the delay after each iteration. Will get smaller as progress increases.
    var currDelay = maxDelay
    // Iterate from high to low if the previous percentage is greater than the current percentage.
    if (prevPercent > currPercent) {
        for (i in prevPercent downTo currPercent) {
            updateProgress(i.toFloat() / 100)
            // Reference for progress and bar acceleration: ChatGPT
            // Progress of how finished the bar animation is completed. As progress increases,
            // currDelay decreases.
            // Calculate distance of i from prevPercent over the distance of prevPercent to currPercent.
            val progress = (prevPercent - i).toFloat() / (prevPercent - currPercent)
            delay(currDelay)
            // Accelerate the progress bar. Delay will decrease as the progress bar is almost done
            // resetting.
            currDelay = maxDelay - ((maxDelay - minDelay) * progress).toLong()
        }

    }
    // Iterate from low to high if the previous percentage is less than the current percentage.
    if (prevPercent < currPercent) {
        for (i in prevPercent..currPercent) {
            updateProgress(i.toFloat() / 100)

            // Reference for progress and bar acceleration: ChatGPT
            // Progress of how finished the bar animation is completed. As progress increases,
            // currDelay decreases.
            // Calculate the distance of i from prevPercent over the distance of prevPercent to currPercent.
            val progress = (i - prevPercent).toFloat() / (currPercent - prevPercent)
            delay(currDelay)
            // Accelerate the progress bar. Delay will decrease as the progress bar is almost done
            // resetting.
            currDelay = maxDelay - ((maxDelay - minDelay) * progress).toLong()
        }
    }

}