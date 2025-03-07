package com.start.pages

import androidx.compose.foundation.layout.Arrangement
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
import androidx.navigation.compose.rememberNavController


@Composable
fun RatingPage(navController: NavController) {

    var rating by remember {mutableIntStateOf(0)}
    var reviewInput by remember {mutableStateOf("")}

    Column (modifier = Modifier
        .fillMaxSize()
        .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Row(Modifier.fillMaxWidth()
        ) {
            // Back button details
            Button (onClick = {}) {
                Text("Go back to clinic details",)
            }
        }

        // Extra space
        Spacer(
            modifier = Modifier.height(32.dp)
        )
        Column (
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            // Question
            Text("How would you rate this clinic?",
                fontSize = 20.sp
            )

            RatingBar(
                rating = rating,
                onRatingChanged = {rating = it},
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                "What is your review on this clinic?",
                fontSize = 20.sp
                )

            OutlinedTextField(
                value = reviewInput,
                onValueChange = {reviewInput = it},
                label = { Text("Enter review here...")},
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {}
            ) {
                Text("Submit Rating")
            }
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun testRatingPage() {
    RatingPage(navController = rememberNavController())
}