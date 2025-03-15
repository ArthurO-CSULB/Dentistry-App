package com.start.pages

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.dentalhygiene.BuildConfig.MAPS_API_KEY
import com.example.dentalhygiene.R
import com.google.android.libraries.places.api.Places
import com.start.PlacesApiService
import com.start.model.PlaceDetails

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicDetailsPage(
    placeId: String?,// Place ID that is taken from one of the clinics in the Clinic Search Page
    navController: NavController
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
                                .size(300.dp)
                                .fillMaxSize()
                        )
                    }
                }
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
            Button(onClick = { navController.popBackStack()}) {
                Text("Make a Rating")
            }
            Button(onClick = { navController.popBackStack()}) {
                Text("Bookmark Clinic")
            }
        }
    }
}

// Makes the request to the API based on the clinic's photo reference
fun getPhotoUrl(photoRef: String, apiKey: String): String {
    return "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=$photoRef&key=$apiKey"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicDetailsPageLayout(
    navController: NavController
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
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
            Text(text = "Dinkleberg Dentistry", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
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
                    .size(300.dp)
                    .fillMaxSize()
            )
        }
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.address_pin),
                contentDescription = "Address Pin",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
            Text(text = "96024 Applebottom Ave.", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ){
            Image(
                painter = painterResource(id = R.drawable.clinic_phone),
                contentDescription = "Address Pin",
                modifier = Modifier
                    .size(40.dp)
                    .padding(end = 8.dp)
            )
            Text(text = "555-555-5555", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        }
            ?: Text("Loading clinic details...") //If details are null, shows this
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
            Button(onClick = { navController.popBackStack()}) {
                Text("Make a Rating")
            }
            Button(onClick = { navController.popBackStack()}) {
                Text("Bookmark Clinic")
            }
        }
    }

}
@Preview(showBackground = true)
@Composable
fun ClinicDetailsPagePreview() {
    ClinicDetailsPageLayout(rememberNavController())
}