package com.start.pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.ui.theme.Purple80
import com.start.ui.theme.PurpleGrey40
import com.start.viewmodels.RatingViewModel
import com.start.viewmodels.UserReview
import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable

// Page that displays all ratings a user has made
fun UserRatingsPage(navController: NavController,
                    ratingViewModel: RatingViewModel
) {

    // State handlers for tracking the ratings list and size
    val userRatings by ratingViewModel.userRatingsList.collectAsState()
    val userRatingsCount = ratingViewModel.userRatingsCount.collectAsState()

    // If the page is launched, get the ratings
    LaunchedEffect(ratingViewModel) {
            ratingViewModel.getUserRatings()
    }

    // Create a scaffold to have the topbar and body elements
    Scaffold(

        // Topbar implementation
        topBar = {
            CenterAlignedTopAppBar(
                title = {Text("All User Ratings")},
                navigationIcon = {
                    // back button
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
            )
        }

    ) { innerPadding ->
        // Body implmentation
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Extra space between top bar and additional text
            Spacer(Modifier.height(20.dp))
            Text(text = "${userRatingsCount.value} Ratings made",
                fontSize = 20.sp,
            )
            // Show the dropdown menu
            Spacer(Modifier.height(12.dp))
            UserSortingDropdownMenu(ratingViewModel)
            Spacer(Modifier.height(12.dp))

            // show all reviews made by the user
            UserReviewsTab(
                userRatings = userRatings,
                ratingViewModel = ratingViewModel
            )
        }
    }
}

// A composable function that holds a user review
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserReviewItem(userReview: UserReview, ratingViewModel: RatingViewModel)
{
    // Variables to keep track
    var likeCount by remember { mutableStateOf(userReview.likeCount) }
    var dislikeCount by remember { mutableStateOf(userReview.dislikeCount) }

    Column(
        // UI specifics of a single item
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 5.dp)
            .border(width = 1.dp, color = PurpleGrey40, shape = RoundedCornerShape(4.dp))
    ) {
        // Make a row for handling user information
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            // Extra Space
            Spacer(Modifier.width(6.dp))

            // Create another column for handling more rating data
            Column {
                // specifies the format of the date
                val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm a", Locale.ENGLISH)

                //Display name of the clinic
                Text(
                    text = "Review to ${userReview.clinicName}",
                    modifier = Modifier.padding(12.dp, 3.dp),
                    fontSize = 18.sp
                )

                // Date when review was made
                Text(
                    text = "${userReview.createdAt.format(formatter)}",
                    modifier = Modifier.padding(12.dp, 0.dp),
                    color = Color.Gray,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.weight(1f))

            // Implementation of Delete Button
            // Deletes rating on both clinic and user side when clicked
            IconButton(onClick = {
                runBlocking {
                    ratingViewModel.deleteUserReview(userReview.clinicID)
                    TimeUnit.SECONDS.sleep(2L)
                }
                ratingViewModel.getUserRatings()

            } ) {
                Icon(
                    modifier = Modifier.size(50.dp),
                    imageVector = Icons.TwoTone.Delete,
                    contentDescription = "Delete Rating",
                )
            }
        }
        // star bar that specifies how much rating it got on this review
        StarBar(
            rating = userReview.rating,
            modifier = Modifier.padding(16.dp, 8.dp)
        )
        // Text that shows the review details of a rating
        Text(
            text = userReview.review,
            modifier = Modifier.padding(18.dp, 0.dp),
            fontSize = 16.sp)

        // Like and dislike buttons for a rating
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {

            Text(modifier = Modifier.padding(0.dp, 10.dp , 0.dp , 0.dp ), text = "Liked by $likeCount, disliked by $dislikeCount")
        }
    }
}

// Composable function for all the ratings made by the user
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun UserReviewsTab(userRatings: List<UserReview>, ratingViewModel: RatingViewModel) {
    LazyColumn {
        // display all ratings
        items(userRatings) { rating ->
            UserReviewItem(rating, ratingViewModel
            )
            Spacer(Modifier.height(2.dp))
        }
    }
}

// Dropdown menu for the user ratings page
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserSortingDropdownMenu(ratingViewModel: RatingViewModel) {

    // variables needed to for the menu to function
    var expanded by remember { mutableStateOf(false) } // checks if the menu is closed or not
    var currentSortBy by remember { mutableStateOf("Most Recent")} // checks the current sorting specification of the reviews

    // Set modifier for the menu items
    var menuItemsModifier = Modifier.
    padding(vertical = 4.dp, horizontal = 16.dp).
    border(width = 1.dp, color = PurpleGrey40, shape = RoundedCornerShape(16.dp))

    // Use a box to initially hide the dropdown menu
    // When menu is clicked, unload all UI elements downward
    Box(
        modifier = Modifier
            .padding(12.dp)
    ) {
        // Use a row for handling the dropdown menu
        Row(
            modifier = menuItemsModifier
        ) {
            // Text button for showing the dropdown menu
            TextButton(
                onClick = { expanded = !expanded  } // either close or open the menu when clicked
            )
            {
                // Text that shows the current sorting method
                Text("Sort by: $currentSortBy")

                // Icon for showing if the dropdown menu is open or not
                Icon(
                    // if the menu is open, display a down arrow, otherwise display an up arrow
                    imageVector = if (!expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (!expanded) "Open" else "Closed",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Dropdown Menu specifications
            DropdownMenu(
                expanded = expanded, // details when the menu is expanded or not
                onDismissRequest = { expanded = false } // if the menu is dismissed, close the menu
            ) {
                // Specifies the menu items in the menu
                // Shows only two sorting methods: by clinic name or by most recent
                // Show current sorting order in the text button when a menu item is clicked,
                // then sort the reviews accordingly
                DropdownMenuItem(
                    text = { Text("Clinic Name") },
                    onClick = {
                        currentSortBy = "Clinic Name"
                        ratingViewModel.sortUserReviews("Clinic Name")
                    },
                    modifier = menuItemsModifier
                )
                DropdownMenuItem(
                    text = { Text("Most Recent") },
                    onClick = {
                        currentSortBy = "Most Recent"
                        ratingViewModel.sortUserReviews("Most Recent")
                    },
                    modifier = menuItemsModifier
                )
            }
        }
    }
}