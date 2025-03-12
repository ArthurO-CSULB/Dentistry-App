package com.start.pages

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.viewmodels.ClinicReview
import com.start.viewmodels.RatingState
import com.start.viewmodels.RatingViewModel
import java.time.LocalDateTime
import com.start.ui.theme.Purple80
import com.start.ui.theme.PurpleGrey40
import java.time.format.DateTimeFormatter
import java.util.Locale

// Function that handles UI for the ClinicDetails Page (at least on my branch)
// This branch is also being implemented by Kevin so changes may appear in the future
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClinicRatingsPage
(
    navController: NavController,
    ratingViewModel: RatingViewModel,
    clinicID: String?,
    clinicName: String?
) {

    // context to be used for toast message
    val context = LocalContext.current

    // variables for handling state changes and displaying ratings on the clinic
    var ratingState = ratingViewModel.ratingState
    val clinicList by ratingViewModel.clinicRatingsList.collectAsState()

    // Gets details about the clinic when the page loads
    LaunchedEffect(clinicID, ratingState) {
        clinicID.let {
            try {
                ratingViewModel.getClinicRatings(clinicID.toString())
            }
            // if it fails...
            catch (e: Exception) {
                //log the results then display an error message
                Log.e("ClinicDetails", "Failed to fetch clinic details: ${e.message}")
                Toast.makeText(context, "Failed to load clinic details", Toast.LENGTH_SHORT).show()
            }
        }

        // state handling block
        when (ratingState.value) {
            // when user wants to create a rating, navigate to rating page
            is RatingState.CreatingARating -> navController.navigate("createRating/${clinicID.toString()},${clinicName.toString()}")
            // display error message when error occurs
            is RatingState.Error -> Toast.makeText(context,
                (ratingState.value as RatingState.Error).message, Toast.LENGTH_SHORT).show()
            // display success message when everything goes well
            is RatingState.Success -> Toast.makeText(context,
                (ratingState.value as RatingState.Success).message, Toast.LENGTH_SHORT).show()
            // else, do nothing
            else -> Unit
        }
    }

    // Sets the scroll behavior of the top bar to pinned
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    // Creates a Scaffold Object that contains the UI elements of the page
    Scaffold(
        // Details the specifications of this page's top bar
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text ("Reviews for this clinic") }, // title of the Page
                navigationIcon =
                {
                    // back button
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back to previous page"
                        )
                    }
                },
                // scroll behavior of the top bar
                scrollBehavior = scrollBehavior,

                // Specifies all additional buttons in the top bar

                actions = {
                    // Refresh Button
                    IconButton(onClick = {
                        //navController.navigate("createRating/$clinicID,$clinicName")
                        //ratingViewModel.ratingCreationEnter()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Refresh Page"
                        )
                    }
                },
                // Specifies Color of the top bar
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Purple80,
                    titleContentColor = PurpleGrey40
                )
            )
        },
        // Specifications of the bottom bar
        // only implement a floating action button for creating a review
        bottomBar = {
            BottomAppBar(
                // no additional buttons
                actions = {},
                // floating action button for review creation
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navController.navigate("createRating/$clinicID,$clinicName") },
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
                ) {
                        //Icon used for the button
                        Icon(
                            Icons.Filled.Create,
                            "Create a Rating")
                    }
                }
            )
        }
    ) { innerPadding -> // Inner padding to be used by the content of the page
        Column(
            modifier = Modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Tab that shows all reviews on the clinic
            ReviewsTab(clinicList)
        }
    }
}

// A composable function that holds a review
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClinicReviewItem(clinicReview: ClinicReview) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(width = 1.dp, color = PurpleGrey40, shape = RoundedCornerShape(4.dp))
    ) {
        // Make a row for handling user information
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Profile Picture handler
            Image(
                painter = painterResource(id = R.drawable.basicprofilepic),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(40.dp)
            )

            // Extra Space
            Spacer(Modifier.width(6.dp))

            // Create another column for handling more rating data
            Column {
                // specifies the format of the date
                val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a", Locale.ENGLISH)

                // User name
                // Users are anonymous in the program so don't use real name
                Text(
                    text = "A mOral User",
                    Modifier.padding(12.dp, 3.dp)
                )

                // Date when review was made
                Text(
                    text = "${clinicReview.createdAt.format(formatter)}",
                    modifier = Modifier.padding(12.dp, 0.dp),
                    color = Color.Gray
                )
            }
        }
        // star bar that specifies how much rating it got on this review
        StarBar(
            rating = clinicReview.rating,
            modifier = Modifier.padding(0.dp, 16.dp)
            )
        // Review details
        Text(clinicReview.review)

        // Like and dislike buttons for a rating
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Like button
            Button(
                onClick = {},
                colors = ButtonColors(
                    containerColor = Color.Green,
                    contentColor = Color.Unspecified,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Unspecified)
            ) {
                Icon(
                    Icons.Outlined.ThumbUp,
                    contentDescription = "Like Rating",
                )
                Spacer(Modifier.width(8.dp))
                Text("Like")
            }

            // Dislike Button
            Button(
                onClick = {},
                colors = ButtonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Unspecified,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Unspecified)
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Dislike Rating"
                )
                Spacer(Modifier.width(8.dp))
                Text("Dislike")
            }
        }
    }
}

// Star bar implementation
@Composable
fun StarBar(rating: Int, modifier: Modifier) {
    Row(modifier = modifier) {
        // create 5 stars
        for (i in 1.. 5) {
            // highlight number of stars depending on how many points it got
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = if (i<= rating) "Rated $i stars" else "Rate $i stars",
                tint = if (i <= rating) Color.Yellow else Color.Gray
            )
        }
    }
}

// Composable function for all the ratings in the clinic
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReviewsTab(clinicRatings: List<ClinicReview>) {
    LazyColumn {
        // display all ratings
        items(clinicRatings) { rating ->
            ClinicReviewItem(rating)
            Spacer(Modifier.height(2.dp))
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TestReviewItem() {
    ClinicReviewItem(
        ClinicReview(
            rating = 5,
            review = "test",
            createdAt = LocalDateTime.now()
        )
    )
}