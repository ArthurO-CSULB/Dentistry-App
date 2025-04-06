package com.start.pages

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.BuildConfig.MAPS_API_KEY
import com.start.PlacesApiService
import com.start.model.PlaceDetails
import com.start.viewmodels.BookmarksViewModel
import com.start.viewmodels.RatingViewModel

// Function that handles UI for the ClinicDetails Page (at least on my branch)
// This branch is also being implemented by Kevin so changes may appear in the future
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ClinicDetailsPage(
    placeId: String?,// Place ID that is taken from one of the clinics in the Clinic Search Page
    navController: NavController,
    ratingViewModel: RatingViewModel,
    bookmarksViewModel: BookmarksViewModel
) {

    // context and ClinicDetails for fetching clinic data
    val context = LocalContext.current
    var clinicDetails by remember { mutableStateOf<PlaceDetails?>(null) }

    // Gets details about the clinic when the page loads
    LaunchedEffect(placeId) {
        placeId?.let {
            try {
                // get place details of the clinic
                clinicDetails = PlacesApiService.getPlaceDetails(it, MAPS_API_KEY)
                clinicDetails?.let { details ->
                    // and then add the clinic to the database then get the ratings
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
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Button to see all ratings on a clinic
            Button(
                onClick = {
                    navController.navigate("clinicRatings/$placeId,${clinicDetails?.name}")
                },
                modifier = Modifier.padding(10.dp)
            )
            {
                Text("View Ratings")
            }


            // Button for creating a rating
            Button(
                onClick = {
                    navController.navigate("createRating/$placeId,${clinicDetails?.name}")

                },
                modifier = Modifier.padding(10.dp)
            )
            {
                Text("Make a Rating")
            }
        }

        // Make another row for a button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {

            // Button for bookmarking this clinic
            Button(
                onClick ={
                    clinicDetails?.let { details ->
                        bookmarksViewModel.addBookmark(
                            clinicID = details.placeId,
                            clinicName = details.name,
                            rating = details.rating
                        )
                        }
                    Toast.makeText(context, "Clinic successfully bookmarked", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Bookmark")
            }
        }
    }
}
