package com.start.pages.profile_pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.viewmodels.PointsProgressionViewModel
import com.start.viewmodels.Prestige
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PointsProgressionPage(modifier: Modifier, navController: NavController,
                          pointsProgressionViewModel: PointsProgressionViewModel
) {

    // Collect the flows of prestige and exp emitted from view model.
    val prestige = pointsProgressionViewModel.prestige.collectAsState()
    val exp = pointsProgressionViewModel.experience.collectAsState()
    val prestigeInfo: Prestige = pointsProgressionViewModel.prestiges[prestige.value.toInt()]
    // Get the max experience for the current prestige of the user from the array that
    // stores objects of prestiges.
    val maxExp: Long = pointsProgressionViewModel.prestiges[prestige.value.toInt()].maxExp

    // Variable to store the level progress of the progress bar. Will be updated iteratively
    // in a coroutine to display the progress increasing/decreasing. Initialize it with the initial
    // progress calculated by user exp and max exp for their prestige. Will be remembered across all
    // recompositions.
    var levelProgress by remember {mutableStateOf(exp.value.toFloat() / maxExp.toInt())}

    // Coroutine scope to launch the coroutine to update the progress bar.
    val scope = rememberCoroutineScope()

    LaunchedEffect(exp.value) {

    }

    Column (
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()

    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.weight(1f)

                //.border(width = 1.dp, color = Color.Black)
        ) {
            // Image for the prestige/user
            Image(
                painter = painterResource(id = R.drawable.basicprofilepic),
                contentDescription = "Profile Picture",
                modifier = modifier.size(160.dp)
            )
            Spacer(modifier = modifier.height(8.dp))
            Text(
                text = "Current Rank:",
                lineHeight = 1.5.em,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Text(
                text = prestigeInfo.toString(),
                fontSize = 24.sp,
                lineHeight = 1.5.em
            )
            Text(
                text = "Experience Points:",
                lineHeight = 1.5.em,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Text(
                text = exp.value.toString(),
                lineHeight = 1.5.em,
                fontSize = 24.sp
            )
            // Button to prestige/rank up the user.
            Button(
                onClick= {
                    // If the user has enough points for their particular prestige, initiate
                    // the progress bar to decrease from the max to zero.
                    if (exp.value >= maxExp) {
                        // Launch the coroutine that will reset the progress bar.
                        scope.launch {
                            // Call resetProgress to reset the progress bar asynchonously.
                            resetProgress { progress ->
                                levelProgress = progress
                            }
                            // Increase the prestige of the user by one and reset the points to zero.
                            pointsProgressionViewModel.prestige()
                            pointsProgressionViewModel.resetPoints()
                        }
                    }

                }

            ) {
                Text("Next Level")
            }
        }

        Text("${exp.value.toInt()} / ${maxExp.toInt()}")
        LinearProgressIndicator(
            progress = {levelProgress},
            modifier = modifier.fillMaxWidth()
        )

    }

    /*
    DOCUMENTATION EXAMPLE CODE

    ************************************************************************
    var currentProgress by remember { mutableStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Create a coroutine scope

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Button(onClick = {
            loading = true
            scope.launch {
                loadProgress { progress ->
                    currentProgress = progress
                }
                loading = false // Reset loading when the coroutine finishes
            }
        }, enabled = !loading) {
            Text("Start loading")
        }

        if (loading) {
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Iterate the progress value */
suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(5)
    }

     */

}
// Iterate the progress value from 100 to 0.
suspend fun resetProgress(updateProgress: (Float) -> Unit) {
    // max delay and min delay
    val maxDelay = 15L
    val minDelay = 5L
    var currDelay = maxDelay

    // Iterate from 100 to 0
    for (i in 100 downTo 0) {
        // Update the progress of the progress bar.
        updateProgress(i.toFloat() / 100)

        // Reference: ChatGPT
        // Progress of how finished the progress animation is completed. As progress increases,
        // currDelay decreases.
        val progress = 1f - (i.toFloat() / 100)
        delay(currDelay)
        // Accelerate the progress bar. Delay will decrease as the progress bar is almost done
        // resetting.
        currDelay = maxDelay - ((maxDelay - minDelay) * progress).toLong()
    }
}


