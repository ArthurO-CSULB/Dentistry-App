package com.start.pages

import android.media.Rating
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.BuildConfig.MAPS_API_KEY
import com.example.dentalhygiene.R
import com.google.android.libraries.places.api.Places
import com.start.PlacesApiService
import com.start.model.PlaceDetails
import com.start.viewmodels.ClinicReview
import com.start.viewmodels.RatingState
import com.start.viewmodels.RatingViewModel
import java.time.LocalDateTime

// Function that handles UI for the ClinicDetails Page (at least on my branch)
// This branch is also being implemented by Kevin so changes may appear in the future
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClinicDetailsPage(
    placeId: String?,// Place ID that is taken from one of the clinics in the Clinic Search Page
    navController: NavController,
    ratingViewModel: RatingViewModel
) {

    // context and ClinicDetails for fetching clinic data
    val context = LocalContext.current
    var clinicDetails by remember { mutableStateOf<PlaceDetails?>(null) }

    // variables for handling state changes and displaying ratings on the clinic
    var ratingState = ratingViewModel.ratingState
    val clinicList by ratingViewModel.clinicList.collectAsState()

    // Gets details about the clinic when the page loads
    LaunchedEffect(placeId, ratingState) {
        placeId?.let {
            try {
                // get place details of the clinic
                clinicDetails = PlacesApiService.getPlaceDetails(it, MAPS_API_KEY)
                clinicDetails?.let { details ->
                    // and then add the clinic to the database then get the ratings
                    ratingViewModel.addClinicToDB(details)
                    ratingViewModel.getClinicRatings(details.placeId)
                }
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
            is RatingState.CreatingARating -> navController.navigate("ratingsPage/$placeId,${clinicDetails?.name}")
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

    // main UI handler
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // back button
        Button(onClick = { navController.popBackStack() }) {
            Text("Back to Map")
        }

        clinicDetails?.let { clinic ->
            Text(text = clinic.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "Address: ${clinic.address}")
            Text(text = "Phone: ${clinic.phoneNumber ?: "N/A"}")
        }
            ?: Text("Loading clinic details...") //If details are null, shows this

            // Row for handling buttons in the ratings
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Rating creation button specifics
                Button(
                    onClick = {
                        ratingViewModel.ratingCreationEnter()
                        navController.navigate("ratingsPage/$placeId,${clinicDetails?.name}")
                    },
                    modifier = Modifier.padding(16.dp))
                    {
                        Text("Make a Rating")
                    }

                // Bookmark button specifics
                // currently does nothing, since it was Kelson who was supposed to implement that lol
                Button(
                    onClick = {},
                    modifier = Modifier.padding(16.dp))
                {
                    Text("Bookmark")
                }
            }

            // Extra space
            Spacer(modifier = Modifier.height(24.dp))

            // Location that specifies the recent reviews
            Text(text = "Recent Reviews:", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            // Tab that shows all reviews on the clinic
            ReviewsTab(clinicList)
        }
    }



// A composable function that holds a review
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClinicReviewItem(clinicReview: ClinicReview) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // Make a row for handling user infor
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


            Column {
                // User name
                // Users are anonymous in the program so don't use real name
                Text(
                    text = "A mOral User",
                    Modifier.padding(12.dp, 3.dp)
                )
                // Date when review was made
                Text(
                    text = "${clinicReview.createdAt.toLocalDate()}",
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
            Log.d("Review Tab Output", "${rating.review} rating successfully created")
        }
    }
}

// test review item
@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TestReviewItem(){
    var testClinic = ClinicReview(5, "This clinic is very good. Not to mention the dentist is very pretty :)", LocalDateTime.now())
    ClinicReviewItem(testClinic)
}

// test review tab
@RequiresApi(Build.VERSION_CODES.O)
@Preview (showBackground = true)
@Composable
fun TestReviewTab() {
    var testReviews = mutableListOf<ClinicReview>()
    testReviews.add(ClinicReview(5, "Hello", LocalDateTime.now()))
    testReviews.add(ClinicReview(4, "The person aboves me stinks", LocalDateTime.now()))
    testReviews.add(ClinicReview(1, "I hate this place", LocalDateTime.now()))
    testReviews.add(ClinicReview(3, "michael!! don't leave me here!!!", LocalDateTime.now()))
    testReviews.add(ClinicReview(2, "michael!!!!!!!!!!!!!!! please!!!!!", LocalDateTime.now()))

    ReviewsTab(testReviews)
}