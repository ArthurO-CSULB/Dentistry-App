package com.start.pages

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.ui.theme.Purple80
import com.start.viewmodels.RatingState
import com.start.viewmodels.RatingViewModel


// Function that handles the UI components of the page
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateRatingPage(
    navController: NavController,
    ratingViewModel: RatingViewModel,
    clinicID: String,
    clinicName: String
)
{

    // initialization values used to hold the rating details
    var rating by remember { mutableIntStateOf(0) }
    var reviewInput by remember { mutableStateOf("") }

    // state and context values needed for state changes and Toast messages respectively
    val ratingState by ratingViewModel.ratingState.collectAsState()
    val context = LocalContext.current

    // Specifies action states to be taken on certain states
    LaunchedEffect(ratingState) {
        when (ratingState) {
            // Do nothing on these states
            is RatingState.CreatingARating -> Unit
            is RatingState.Idle -> Unit
            is RatingState.UpdatingEntries -> Unit

            // Display error message on this state
            is RatingState.Error -> {
                Toast.makeText(context,
                    (ratingState as RatingState.Error).message, Toast.LENGTH_SHORT).show()
            }
            // Display success rating on this state then go back to previous page
            is RatingState.Success -> {
                Toast.makeText(context,
                    (ratingState as RatingState.Success).message, Toast.LENGTH_SHORT).show()
                // Navigate back to the previous page after showing the success message
            }
            else -> Unit
        }
    }

    // Main composable function that holds the page UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // UI elements and logic for the back button
        Row(Modifier.fillMaxWidth()) {
            Button(onClick = {
                navController.popBackStack()
                ratingViewModel.ratingCreationExit()
            }) {
                Text("Go back to clinic details")
            }
        }

        // Extra Space
        Spacer(modifier = Modifier.height(32.dp))

        // Rating question text
        Text("How would you rate this clinic?", fontSize = 20.sp)

        // Creation for a user-interactive rating bar
        RatingBar(
            rating = rating,
            onRatingChanged = { rating = it },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Extra Space
        Spacer(modifier = Modifier.height(32.dp))

        // Review Question Text
        Text("What is your review on this clinic?", fontSize = 20.sp)

        // Text box for review input
        OutlinedTextField(
            value = reviewInput,
            onValueChange = { reviewInput = it },
            label = { Text("Enter review here...") },
        )

        // Extra Space
        Spacer(modifier = Modifier.height(32.dp))

        // Button on rating creation
        Button(
            onClick = {
                // reviews are required
                // if review box is empty, prompt user to try again
                if (reviewInput.isBlank()) {
                    Toast.makeText(context, "Please enter a review", Toast.LENGTH_SHORT).show()
                }
                // if not, create the rating
                else {
                    ratingViewModel.createRating(
                        rating = rating,
                        review = reviewInput,
                        clinicID = clinicID,
                        clinicName = clinicName
                    )
                    ratingViewModel.ratingCreationExit()
                    navController.popBackStack()
                }
            }
        ) {
            Text("Submit Rating")
        }
    }
}

// Creation of the rating bar
@Composable
fun RatingBar(rating: Int, onRatingChanged: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        // declare 5 stars
        for (i in 1..5) {
            // logic for handling the rating changes
            IconToggleButton(
                checked = i <= rating,
                onCheckedChange = { if (it) onRatingChanged(i) else onRatingChanged(i - 1) },
                modifier = Modifier.size(48.dp)
            ) {
                // logic for the star icons
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = if (i <= rating) "Rated $i stars" else "Rate $i stars",
                    tint = if (i <= rating) Purple80 else Color.Gray
                )
            }
        }
    }
}