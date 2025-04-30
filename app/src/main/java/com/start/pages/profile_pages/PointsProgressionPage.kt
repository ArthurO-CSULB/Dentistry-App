package com.start.pages.profile_pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dentalhygiene.R
import com.start.viewmodels.Emblem
import com.start.viewmodels.PointsProgressionViewModel
import com.start.viewmodels.Prestige
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PointsProgressionPage(modifier: Modifier, navController: NavController,
                          pointsProgressionViewModel: PointsProgressionViewModel
) {

    // Collect the flows of prestige and exp emitted from view model.
    val prestige = pointsProgressionViewModel.prestige.collectAsState()
    val exp = pointsProgressionViewModel.experience.collectAsState()
    // Store the exp value from the previous recomposition of the UI. Be sure to update it when
    // exp changes.
    val expTemp = remember {mutableIntStateOf(exp.value.toInt())}
    val prestigeTemp = remember {mutableIntStateOf(prestige.value.toInt())}

    val prestigeInfo: Prestige = pointsProgressionViewModel.prestiges[prestige.value.toInt()]
    // Get the max experience for the current prestige of the user from the array that
    // stores objects of prestiges.
    val maxExp: Long = pointsProgressionViewModel.prestiges[prestigeTemp.intValue].maxExp

    // Gets the user's equipped emblem and loads it when the page loads
    val emblem by pointsProgressionViewModel.equippedEmblem
    LaunchedEffect(Unit) {
        pointsProgressionViewModel.loadEquipped()
    }

    // Variable to store the level progress of the progress bar. Will be updated iteratively
    // in a coroutine to display the progress increasing/decreasing. Initialize it with the initial
    // progress calculated by user exp and max exp for their prestige. Will be remembered across all
    // recompositions.
    var levelProgress by remember {mutableStateOf(exp.value.toFloat() / maxExp.toInt())}

    // Coroutine scope to reset points and increase prestige.
    val scope = rememberCoroutineScope()

    // Boolean to store if the progress bar is moving or not.
    var progressMoving by remember {mutableStateOf(false)}

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
        reactiveProgress(prevPercentage, currPercentage) { progress ->
            // Update the progress bar from the value that is passed in.
            levelProgress = progress
        }

        // When progression is finished, temp values become the current values.
        expTemp.intValue = exp.value.toInt()
        prestigeTemp.intValue = prestige.value.toInt()

        // State that the progress bar is not moving.
        progressMoving = false
    }

    Column (
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()

    ) {
        TopAppBar(
            title =
            {
                Text(
                    text = "Back to Profile Page",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            navigationIcon = {
                IconButton(onClick = {navController.popBackStack()}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Button",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults. topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
        )
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.weight(1f)

                //.border(width = 1.dp, color = Color.Black)
        ) {
            // TODO: This image should be an emblem that a user can buy/unlock. Customizable.
            // Emblem should also be a 512 x 512 png
            // Image for the prestige/user
            // If the user doesn't have an equipped emblem, shows the default one
            if (emblem == null){
                Image(
                    painter = painterResource(R.drawable.basicprofilepic),
                    contentDescription = "Profile Picture",
                    modifier = modifier.size(160.dp)
                )
            } else
            {
                // Displays the user's equipped emblem
                AsyncImage(
                    model = emblem,
                    contentDescription = "Emblem",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(160.dp)
                        .padding(8.dp)
                        //.clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = modifier.height(8.dp))
            Text(
                text = "Current Rank:",
                lineHeight = 1.5.em,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
            Text(
                text = "Prestige ${prestigeInfo.prestigeLevel.toInt()} - ${prestigeInfo.toString()}",
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
            // Button to prestige/rank up the user. Only show button if user is not at max prestige.
            if (prestige.value < pointsProgressionViewModel.prestiges.last().prestigeLevel) {
                Button(
                    onClick= {
                        // If the user has enough points for their particular prestige, initiate
                        // the progress bar to decrease from the max to zero.
                        if (exp.value >= maxExp) {
                            // Boolean to store if the progress bar is moving or not. Disable.
                            // progressMoving = true
                            // Launch the coroutine that will reset the progress bar.
                            scope.launch {

                                /*
                                // Call resetProgress to reset the progress bar asynchonously.
                                resetProgress { progress ->
                                    levelProgress = progress
                                }
                                 */
                                // Increase the prestige of the user by one and reset the points to zero.
                                pointsProgressionViewModel.prestige()
                                pointsProgressionViewModel.resetPoints()
                                //delay(2000)
                                // Enable the progress bar again.
                                // progressMoving = false
                            }
                        }
                    },
                    enabled = !progressMoving
                ) {
                    Text("Next Level")
                }
            }
            // Navigates to the Emblem Shop page
            Button(onClick={navController.navigate("emblems")}) {
                Text(text = "Emblems")
            }
        }



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
// Method that when the exp value changes, will update the progress bar
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


