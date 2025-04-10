package com.start.pages

import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.dentalhygiene.BuildConfig.MAPS_API_KEY
import com.example.dentalhygiene.R
import com.google.android.libraries.places.api.Places
import com.start.PlacesApiService
import com.start.model.PlaceDetails
import com.start.viewmodels.BookmarksViewModel
import com.start.viewmodels.RatingViewModel
import com.start.viewmodels.ClinicDetailsViewModel

// Function that handles UI for the ClinicDetails Page (at least on my branch)
// This branch is also being implemented by Kevin so changes may appear in the future
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicDetailsPage(
    placeId: String?,// Place ID that is taken from one of the clinics in the Clinic Search Page
    navController: NavController,
    clinicDetailsViewModel : ClinicDetailsViewModel = viewModel(), // Viewmodel to allow sending clinic data to the database
    ratingViewModel: RatingViewModel,
    bookmarksViewModel: BookmarksViewModel
) {

    // context and ClinicDetails for fetching clinic data
    val context = LocalContext.current
    var clinicDetails by remember { mutableStateOf<PlaceDetails?>(null) }
    var isExpanded by remember {mutableStateOf(false)} //State for the collapsable Opening Hours list


    // Gets details about the clinic when the page loads
    LaunchedEffect(placeId) {
        placeId?.let {
            val clinicInfo = PlacesApiService.getPlaceDetails(it, MAPS_API_KEY)

            clinicDetails = clinicInfo

            // Adds the clinic to the database with its PlaceID as a unique identifier
            clinicInfo?.let {
                clinicDetailsViewModel.addClinicToDb(it)
                ratingViewModel.getClinicRatings(it.placeId)
            }
        }
    }

    // main UI handler
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        clinicDetails?.let { clinic ->
            TopAppBar(
                title =
                {
                    Text(
                        text = "Back to Map",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults. topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )
            {
                Text(text = clinic.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            }

            // Displays the clinic's photos if they have any, if it's null, then displays default clipart

            if (clinic.photos != null) {
                clinic.photos.let { photos ->
                    Log.d("ClinicDetails", "${clinic.name} has ${photos.size} photos") //For testing
                    if ((photos.isNotEmpty())) {
                        LazyRow( // Allows for a scrollable list of photos
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(photos.size ?: 0) { photo ->
                                val photoReference = photos.get(photo).photoReference ?: return@items
                                val imageUrl = getPhotoUrl(photoReference, MAPS_API_KEY)
                                AsyncImage( // Asynchronously renders the photos
                                    modifier = Modifier
                                        .size(300.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.LightGray),
                                    model = imageUrl,
                                    contentDescription = "Clinic Photo",
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            } else{
                    Log.d("ClinicDetails", "No Photos") // For testing
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ){
                        Image(
                            painter = painterResource(id = R.drawable.dental_clinic_clipart),
                            contentDescription = "Clinic Picture",
                            modifier = Modifier
                                .size(260.dp)
                                .fillMaxSize()
                        )
                    }
            }

            //Clinic Address Row
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ){
                Image(
                    painter = painterResource(id = R.drawable.address_pin),
                    contentDescription = "Address Pin Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
                clinic.address?.let { Text(text = it, fontSize = 20.sp, fontWeight = FontWeight.SemiBold) }
            }

            //Clinic Phone Number Row
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ){
                Image(
                    painter = painterResource(id = R.drawable.clinic_phone),
                    contentDescription = "Phone Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
                clinic.phoneNumber?.let { Text(text = it, fontSize = 20.sp, fontWeight = FontWeight.SemiBold) }
            }

            // Clinic Opening Hours Row
            clinic.openingHours?.weekdayText?.let { hours ->
                Column(
                    //horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isExpanded = !isExpanded }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.clock),
                            contentDescription = "Clock Icon",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(end = 8.dp)
                        )
                        Text(
                            text = "Opening Hours",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand/Collapse Icon",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(start = 8.dp)
                        )
                        //Displays green Open sign if clinic is currently open
                        if (clinic.openingHours.open) {
                            Text(
                                text = "Open",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Green,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                            )
                        }
                        //Displays red Closed sign if clinic is currently closed
                        else{
                            Text(
                                text = "Closed",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                            )
                        }
                    }
                    //Displays all opening days/hours
                    AnimatedVisibility(visible = isExpanded) {
                        Column (
                            horizontalAlignment = Alignment.Start,
                            modifier = Modifier
                                .padding(start = 52.dp)
                                .fillMaxWidth()
                        )
                        {
                            hours.forEach { daysHours ->
                                Text(
                                    text = daysHours,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } ?: Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.clock),
                    contentDescription = "Clock Icon",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )

                Text( //If there are no opening hours available
                    text = "Opening hours not available",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Gray
                )
            }
        } ?: Text("Loading clinic details...") //If details are null, shows this


        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),
            thickness = 2.dp,
            color = Color.Black
        )

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
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


// Makes the request to the API based on the clinic's photo reference
fun getPhotoUrl(photoRef: String, apiKey: String): String {
    return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoRef&key=$apiKey"
}