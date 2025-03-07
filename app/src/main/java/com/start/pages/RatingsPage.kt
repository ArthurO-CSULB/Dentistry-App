package com.start.pages

import android.os.Build
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.viewmodels.RatingViewModel


// Page for user review creation
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RatingsPage(navController: NavController, ratingViewModel: RatingViewModel, clinicID: String, clinicName: String) {

    // Declaration, initialization of user input handlers
    var rating by remember {mutableIntStateOf(0)}
    var reviewInput by remember {mutableStateOf("")}

    // Creation of Column that holds all buttons in program
    Column (modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        // Row that handles the back button in case user does not want to proceed
        Row(Modifier.fillMaxWidth()
        ) {
            // Back button details
            Button (onClick = {
                // go back to previous page
                navController.popBackStack()}
            ) {
                Text("Go back to clinic details")
            }
        }

        // Extra space
        Spacer(
            modifier = Modifier.height(32.dp)
        )

        // Rating Question
        Text("How would you rate this clinic?", fontSize = 20.sp)

        // Rating Bar that lets user designate between 1 - 5 stars to a clinic
        RatingBar(
            rating = rating,
            onRatingChanged = {rating = it},
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Extra Space
        Spacer(modifier = Modifier.height(32.dp))

        // Review Question
        Text("What is your review on this clinic?", fontSize = 20.sp)

        // Text Box that handles user input
        OutlinedTextField(
            value = reviewInput,
            onValueChange = {reviewInput = it},
            label = { Text("Enter review here...")},
        )

        // Extra Space
        Spacer(modifier = Modifier.height(32.dp))

        // Submit Rating Button
        Button(
            // When the button is pressed by the user...
            onClick =
            {
                // The rating is created
                ratingViewModel.createRating(
                    rating = rating,
                    review = reviewInput,
                    clinicID = clinicID,
                    clinicName = clinicName
                )
            }
        ) {
            Text("Submit Rating")
        }
    }
}

// Implementation of a Rating Bar that handles the rating value
@Composable
fun RatingBar(rating: Int, onRatingChanged: (Int) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            IconToggleButton(
                checked = i <= rating,
                onCheckedChange = { if (it) onRatingChanged(i) else onRatingChanged(i-1)},
                modifier = Modifier.size(48.dp)
            )
            {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = if (i<= rating) "Rated $i stars" else "Rate $i stars",
                    tint = if (i <= rating) Color.Yellow else Color.Gray
                )
            }
        }
    }
}

// Test Rating Page for editing frontend
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TestRatingPage() {
    //RatingsPage(navController = rememberNavController(), ratingViewModel = {var ratingViewModel = {}})
}