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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.start.viewmodels.RatingState
import com.start.viewmodels.RatingViewModel
import java.nio.file.WatchEvent
import java.time.LocalDateTime

@Composable
fun ClinicDetailsPage(
    placeId: String?,// Place ID that is taken from one of the clinics in the Clinic Search Page
    navController: NavController,
    ratingViewModel: RatingViewModel
) {
    val context = LocalContext.current
    var clinicDetails by remember { mutableStateOf<PlaceDetails?>(null) }



    // Gets details about the clinic when the page loads
    LaunchedEffect(placeId) {
        placeId?.let {
            clinicDetails = PlacesApiService.getPlaceDetails(it, MAPS_API_KEY)
        }
        Log.d("ClinicDetails", "Received clinicId: $placeId") // For testing purposes
    }

    var ratingState = ratingViewModel.ratingState.observeAsState()

    LaunchedEffect(ratingState.value) {
        when (ratingState.value) {
            is RatingState.CreatingARating -> navController.navigate("ratingsPage/$placeId,${clinicDetails?.name}")
            is RatingState.Idle -> Unit
            is RatingState.UpdatingEntries -> Unit
            is RatingState.Error -> Toast.makeText(context,
                (ratingState.value as RatingState.Error).message, Toast.LENGTH_SHORT).show()
            is RatingState.Success -> Toast.makeText(context,
                (ratingState.value as RatingState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    // Gets rating viewmodel state when the page loads and check movement
    LaunchedEffect(ratingViewModel.ratingState) { }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Button(
            onClick = { navController.popBackStack() })
        {
            Text("Back to Map")
        }

        clinicDetails?.let { clinic ->
            Text(text = clinic.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text(text = "Address: ${clinic.address}")
            Text(text = "Phone: ${clinic.phoneNumber ?: "N/A"}")
        }
            ?: Text("Loading clinic details...") //If details are null, shows this

        // A row designated for the
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    ratingViewModel.ratingCreationEnter()
                    navController.navigate("ratingsPage/$placeId,${clinicDetails?.name}")
                          },
                modifier = Modifier.padding(16.dp)) {
                Text("Make a Rating")
            }
            Button(
                onClick = { navController.popBackStack()},
                modifier = Modifier.padding(16.dp)) {
                Text("Bookmark")
            }
        }

        Spacer( modifier = Modifier.height(24.dp))

        // Place that details the clinic's recent reviews
        Text(text = "Recent Reviews:",
            fontSize =  24.sp,
            fontWeight = FontWeight.Bold)

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
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Image(
                painter = painterResource(id = R.drawable.basicprofilepic),
                contentDescription = "Profile Picture",
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.width(6.dp))
            Column {
                Text(
                    text = "A mOral User",
                    Modifier.padding(12.dp, 3.dp)
                )
                Text(
                    text = "${clinicReview.createdAt.toLocalDate()}",
                    modifier = Modifier.padding(12.dp, 0.dp),
                    color = Color.Gray
                )
            }
        }
        StarBar(
            rating = clinicReview.rating,
            modifier = Modifier.padding(0.dp, 16.dp)
            )
        Text(
            clinicReview.review
        )
    }
    
}

@Composable
fun StarBar(rating: Int, modifier: Modifier) {
    Row(modifier = modifier) {
        for (i in 1.. 5) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = if (i<= rating) "Rated $i stars" else "Rate $i stars",
                tint = if (i <= rating) Color.Yellow else Color.Gray
            )
        }
    }
}


// Data class for storing clinic ratings
data class ClinicReview(val rating: Int, val review: String, val createdAt: LocalDateTime)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun TestReviewItem(){
    var testClinic = ClinicReview(5, "This clinic is very good. Not to mention the doctor is very pretty :)", LocalDateTime.now())
    ClinicReviewItem(testClinic)
}