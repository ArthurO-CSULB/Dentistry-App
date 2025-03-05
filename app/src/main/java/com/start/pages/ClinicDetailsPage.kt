package com.start.pages

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.BuildConfig.MAPS_API_KEY
import com.google.android.libraries.places.api.Places
import com.start.PlacesApiService
import com.start.model.PlaceDetails

@Composable
fun ClinicDetailsPage(
    placeId: String?,
    navController: NavController
) {
    val context = LocalContext.current
    var clinicDetails by remember { mutableStateOf<PlaceDetails?>(null) }

    //Gets details about the clinic when the page loads
    LaunchedEffect(placeId) {
        placeId?.let {
            clinicDetails = PlacesApiService.getPlaceDetails(it, MAPS_API_KEY)
        }
        Log.d("ClinicDetails", "Received clinicId: $placeId")
    }
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
            Text(text = clinic.name, fontSize = 24.sp,
                fontWeight = FontWeight.Bold)
            Text(text = "Address: ${clinic.address}")
            Text(text = "Phone: ${clinic.phoneNumber ?: "N/A"}")
            Text(text = "Rating: ${clinic.rating} ⭐")
        } ?: Text("Loading clinic details...")
    }
}