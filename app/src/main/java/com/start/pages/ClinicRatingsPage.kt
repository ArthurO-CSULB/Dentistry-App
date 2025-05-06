package com.start.pages

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.viewmodels.ClinicReview
import com.start.viewmodels.RatingState
import com.start.viewmodels.RatingViewModel
import com.start.ui.theme.Purple80
import com.start.ui.theme.PurpleGrey40
import kotlinx.coroutines.runBlocking
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.toString

// Function that handles UI for the ClinicDetails Page (at least on my branch)
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
    val clinicList by ratingViewModel.clinicRatingsList.collectAsState() // holds the list of clinics ratings dynamically
    var listCount = ratingViewModel.clinicRatingsCount.collectAsState() // holds the number of ratings dynamically
    var ratingAverage = ratingViewModel.clinicRatingAverage.collectAsState() // holds the clinic rating average dynamically

    // Gets details about the clinic when the page loads
    LaunchedEffect(clinicID, ratingState) {
        clinicID.let {
            try {
                ratingViewModel.getClinicRatings(clinicID.toString()) // get all the ratings of the clinic
                ratingViewModel.calculateClinicRatingAverage(clinicID.toString()) // calculate the rating average of the clinic
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
                title = {

                    // text to be displayed in the top bar
                    // if clinic name is too long, display part of it then add ellipses to cut off
                    // otherwise, show everything
                    val textToDisplay: String = if (clinicName?.length!! < 27) clinicName else "${clinicName.take(24)}..."

                    // title of the Page
                    Text (text = textToDisplay, fontWeight = FontWeight.Bold) },

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
                    //TODO Implement Refresh Page then add it to this page
                    IconButton(onClick = {
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
                        // when the button is clicked, go to rating creation page and enter to RatingCreation State
                        onClick = {
                            ratingViewModel.ratingCreationEnter()
                            navController.navigate("createRating/$clinicID,$clinicName")
                                  },
                        //UI qualities of the bottom bar
                        containerColor = BottomAppBarDefaults.bottomAppBarFabColor, // bar color
                        elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation() // how much bar rises from button
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Extra space between top bar and additional text
            Spacer(Modifier.height(12.dp))

            // Text that details number of reviews the clinic has
            Text(
                text = "Number of Reviews: ${listCount.value}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Specifies the format of the rating average to only show up to 1 decimal place
            val formattedRatingAverage = "%.1f".format(ratingAverage.value)

            // Shows the rating average of the clinic
            Text(
                text = "Current Rating Average: $formattedRatingAverage",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Declare the dropdown menu for sorting options of the ratings
            ClinicSortingDropdownMenu(ratingViewModel)

            // Tab that shows all reviews on the clinic
            ReviewsTab(clinicList, ratingViewModel, clinicID)
        }
    }
}

// A composable function that holds a review
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClinicReviewItem(clinicReview: ClinicReview, ratingViewModel: RatingViewModel, clinicID: String) {
    // Variables to keep track
    var likeCount by remember { mutableStateOf(clinicReview.likeCount) }
    var dislikeCount by remember { mutableStateOf(clinicReview.dislikeCount) }
    var likeDislike by remember { mutableStateOf(clinicReview.likeDislike) }


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

            // Profile Picture handler
            Image(
                painter = painterResource(id = R.drawable.ic_profile),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(48.dp)
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
                    text = "${clinicReview.raterFirstName} ${clinicReview.raterLastName}",
                    modifier = Modifier.padding(12.dp, 3.dp),
                    fontSize = 18.sp
                )

                // Date when review was made
                Text(
                    text = "${clinicReview.createdAt.format(formatter)}",
                    modifier = Modifier.padding(12.dp, 0.dp),
                    color = Color.Gray,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.weight(1f))
            // Implementation of delete button
            // First checks if the current user owns the review
            // If yes, present the delete button on the review
            if (ratingViewModel.checkClinicReviewOwnership(clinicReview.raterID)) {

                // If the delete button is clicked, delete the review then refresh the user ratings list
                IconButton(onClick = {
                    runBlocking{
                        ratingViewModel.deleteClinicReview(clinicReview.ratingID)
                        TimeUnit.SECONDS.sleep(2L)
                    }
                    ratingViewModel.getClinicRatings(clinicID.toString()) // get all the ratings of the clinic
                    ratingViewModel.calculateClinicRatingAverage(clinicID.toString()) // calculate the rating average of the clinic

                })
                {
                    Icon(
                        modifier = Modifier.size(50.dp),
                        imageVector = Icons.TwoTone.Delete,
                        contentDescription = "Delete Rating",
                    )
                }
            }
        }
        // star bar that specifies how much rating it got on this review
        StarBar(
            rating = clinicReview.rating,
            modifier = Modifier.padding(16.dp, 8.dp)
            )
        // Text that shows the review details of a rating
        Text(
            text = clinicReview.review,
            modifier = Modifier.padding(18.dp, 0.dp),
            fontSize = 16.sp)

        // Like and dislike buttons for a rating
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            // Like button
            Button(
                onClick = {
                        ratingViewModel.updateLikeDislike(clinicID, clinicReview.ratingID, "like")

                    // update local variables
                    likeCount = clinicReview.likeCount
                    dislikeCount = clinicReview.dislikeCount
                    likeDislike = clinicReview.likeDislike
                },
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
                Text("$likeCount")
            }

            // Dislike Button
            Button(
                onClick = {
                        ratingViewModel.updateLikeDislike(clinicID, clinicReview.ratingID, "dislike")

                    // update local variables
                    likeCount = clinicReview.likeCount
                    dislikeCount = clinicReview.dislikeCount
                    likeDislike = clinicReview.likeDislike
                },
                colors = ButtonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.Unspecified,
                    disabledContainerColor = Color.Gray,
                    disabledContentColor = Color.Unspecified)
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Dislike Rating"
                )
                Spacer(Modifier.width(8.dp))
                Text("$dislikeCount")
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
                tint = if (i <= rating) Purple80 else Color.Gray,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

// Implementation of the sorting menu to show relevant reviews
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicSortingDropdownMenu(ratingViewModel: RatingViewModel) {

    // variables needed to for the menu to function
    var expanded by remember { mutableStateOf(false) } // checks if the menu is closed or not
    var currentSortBy by remember { mutableStateOf("Most Helpful")} // checks the current sorting specification of the reviews

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
                // TODO: fix to correct specifications when Kelson finishes Likes and Dislikes
                Text("Sort by: $currentSortBy")

                // Icon for showing if the dropdown menu is open or not
                Icon(
                    // if the menu is open, display a down arrow, otherwise display an up arrow
                    imageVector = if (!expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (!expanded) "Showing" else "Closed",
                    modifier = Modifier.size(24.dp)
                )
            }

            // Dropdown Menu specifications
            DropdownMenu(
                expanded = expanded, // details when the menu is expanded or not
                onDismissRequest = { expanded = false } // if the menu is dismissed, close the menu
            ) {
                // Specifies the menu items in the menu
                // Shows only two sorting methods: most helpful or most recent
                // Show current sorting order in the text button when a menu item is clicked,
                // then sort the reviews accordingly
                DropdownMenuItem(
                    text = { Text("Most Helpful") },
                    onClick = {
                        currentSortBy = "Most Helpful"
                        ratingViewModel.sortClinicReviews("Most Helpful")
                              },
                    modifier = menuItemsModifier
                )
                DropdownMenuItem(
                    text = { Text("Most Recent") },
                    onClick = {
                        currentSortBy = "Most Recent"
                        ratingViewModel.sortClinicReviews("Most Recent")
                              },
                    modifier = menuItemsModifier
                )
            }
        }
    }
}

// Composable function for all the ratings in the clinic
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReviewsTab(clinicRatings: List<ClinicReview>, ratingViewModel: RatingViewModel, clinicID: String?) {
    LazyColumn {
        // display all ratings
        items(clinicRatings) { rating ->
            if (clinicID != null) {
                ClinicReviewItem(rating, ratingViewModel, clinicID)
            }
            Spacer(Modifier.height(2.dp))
        }
    }
}

/*
//Test Review Item
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
*/