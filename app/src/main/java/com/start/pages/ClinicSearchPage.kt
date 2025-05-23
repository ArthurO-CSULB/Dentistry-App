package com.start.pages

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.dentalhygiene.BuildConfig.MAPS_API_KEY
import com.example.dentalhygiene.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.start.PlacesApiService
import com.start.model.Geometry
import com.start.model.LocationLatLng
import com.start.model.PlaceResult
import kotlinx.coroutines.launch

/*
We have a composable clinic search page which will handle the UI for clinic search.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("PotentialBehaviorOverride")
@Composable
fun ClinicSearchPage(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    //Gets user's location
    val fusedLocationProviderClient = remember {LocationServices.getFusedLocationProviderClient(context)}
    //UI for the Google Map
    val mapView = remember { MapView(context) }
    //User's coordinates
    var userLoc by remember { mutableStateOf<LatLng?>(null)}
    //List of dental clinics that can change
    var dentalClinics by remember { mutableStateOf<List<PlaceResult>>(emptyList()) }
    //Initializes the Places API client
    val placesClient = remember { Places.createClient(context) }
    //Coroutine for fetching the nearby dental clinics within the map
    val coroutineScope = rememberCoroutineScope()
    //Creates an instance of the Google Map
    var googleMapInstance: GoogleMap? by remember { mutableStateOf(null) }

    // Launcher for requesting location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                // If permission is granted, get the current location
                getUserLocation(context, fusedLocationProviderClient) { location ->
                    userLoc = location
                }
            }
        }
    )

    //Side effect that checks user's permissions and asks to turn them on if they are off
    LaunchedEffect(Unit){
        if (ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getUserLocation(context, fusedLocationProviderClient) { location ->
                userLoc = location
            }
        } else {
            locationPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    //Defaults the user's search radius to 5 miles
    var userRad by remember { mutableStateOf(8046)} // Default ~5 miles


    var toothIcon: BitmapDescriptor? by remember { mutableStateOf(null) }
    // Creates custom bitmap for tooth marker icon and loads it before map is ready
    LaunchedEffect(Unit) {
        toothIcon = BitmapDescriptorFactory.fromResource(R.drawable.tooth_icon)
    }

    // Variable for the double click feature
    var lastClickedPlaceId by remember { mutableStateOf<String?>(null) }

    // Update camera when user location is set
    LaunchedEffect(userLoc, userRad, toothIcon) {
        userLoc?.let { location ->
            googleMapInstance?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(location, 12f)
            )
            // Fetch nearby clinics when userLoc is available
            coroutineScope.launch {
                val clinics = PlacesApiService.searchNearbyClinics(
                    location.latitude,
                    location.longitude,
                    userRad,
                    MAPS_API_KEY
                )
                //Log.d("ClinicSearch", "Clinics fetched: ${clinics.size}")
                dentalClinics = clinics

                // Add markers for each clinic on the map
                googleMapInstance?.let { map ->
                    // Clear previous markers in case of radius change
                    map.clear()
                    clinics.forEach { clinic ->
                        val clinicLatLng =
                            LatLng(clinic.geometry.location.lat, clinic.geometry.location.lng)
                        val marker = map.addMarker(
                            MarkerOptions()
                                .position(clinicLatLng)
                                .title(clinic.name)
                                //.icon(toothIcon)
                        )
                        marker?.tag = clinic.placeId // Sets the place_id as the tag of the marker
                    }
                }
                // Marker tap listener to switch to clinic details page upon double-click
                googleMapInstance?.setOnMarkerClickListener { marker ->
                    Log.d("MarkerClick", "Clicked on marker: ${marker.title}") // For testing purposes
                    val clickedPlaceId = marker.tag as? String

                    if (clickedPlaceId != null) {
                        // If place was already clicked previously
                        if (lastClickedPlaceId == clickedPlaceId) {
                            // Navigate to details page on second click
                            navController.navigate("clinicDetails/$clickedPlaceId")
                            lastClickedPlaceId = null // Reset after navigation
                        } else {
                            // Store the first click for reference
                            lastClickedPlaceId = clickedPlaceId
                            marker.showInfoWindow() // Show marker info like normal
                        }
                    }
                    false // Return false to revert back to default single click
                }
            }
            // For single click detail navigation
            //googleMapInstance?.setOnMarkerClickListener { marker ->
            //    val clickedClinic = dentalClinics.find {
            //        it.name == marker.title
            //    }
            //    clickedClinic?.let { clinic ->
            //        Log.d("ClinicNavigation", "Navigating to details page with ID: ${clinic.placeId}")
            //        navController.navigate("clinicDetails/${clinic.placeId}")
            //    }
            //    true
            //}
        }
    }

    // Ensures lifecycle methods are properly managed
    DisposableEffect(context) {
        // Ensure to properly handle the lifecycle
        mapView.onCreate(null)
        mapView.onResume()
        onDispose {
            mapView.onPause()
            mapView.onDestroy() // Make sure to call onDestroy when the composable is disposed
        }
    }

    // Launch effect to set up the map and markers
    LaunchedEffect(mapView) {
        mapView.onResume() // Ensure the map resumes when Composable is launched
        mapView.getMapAsync { googleMap ->
            googleMapInstance = googleMap

            //Settings for Zoom In and Zoom Out capabilities
            googleMap.uiSettings.isZoomControlsEnabled = true
            googleMap.uiSettings.isZoomGesturesEnabled = true

            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
            }

            // Test: Adds a marker for CSULB
            //val location = LatLng(33.7838, -118.1141)
            //googleMap.addMarker(MarkerOptions().position(location).title("CSULB"))
            //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))

        }
    }

    // Clinic Search Page UI
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title =
            {
                Text(
                    text = "Find a Dental Clinic",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            navigationIcon = {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Button",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
        )
        // Display map
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .height(430.dp)
        )
        //UI for the Search Bar and the Radius buttons
        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Black
        )
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(),
                text = "Locate: ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
        SearchBar(placesClient, googleMapInstance) { latLng ->
            googleMapInstance?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
        Text("Select Search Radius")
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { userRad = 8047 }) {
                Text("5 miles")
            }
            Button(
                onClick = { userRad = 16093 }) {
                Text("10 miles")
            }
            Button(
                onClick = { userRad = 32187 }) {
                Text("20 miles")
            }
        }
    }
}

//Composable for the search bar with autocomplete
@Composable
fun SearchBar(placesClient: PlacesClient, googleMapInstance: GoogleMap?, onPlaceSelected: (LatLng) -> Unit) {
    var query by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    Column {
        OutlinedTextField(
            value = query,
            onValueChange = { newValue ->
                query = newValue
                if (newValue.length > 2) {
                    fetchPlaceSuggestions(newValue, placesClient) { result ->
                        predictions = result
                    }
                } else {
                    predictions = emptyList()
                }
            },
            label = { Text("Search for dental clinics") },
            modifier = Modifier.fillMaxWidth()
        )

        predictions.forEach { prediction ->
            TextButton(onClick = {
                googleMapInstance?.let {map ->
                    fetchPlaceDetails(prediction.placeId, placesClient, map) { latLng ->
                        onPlaceSelected(latLng)
                        query = prediction.getPrimaryText(null).toString()
                        predictions = emptyList()
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f)) // Moves map to the place searched
                    }
                }
            }) {
                Text(prediction.getPrimaryText(null).toString())
            }
        }
    }
}
//Function to get Google Place autocomplete suggestions
fun fetchPlaceSuggestions(query: String, placesClient: PlacesClient, onResult: (List<AutocompletePrediction>) -> Unit) {
    val request = FindAutocompletePredictionsRequest.builder()
        .setQuery(query)
        .build()

    placesClient.findAutocompletePredictions(request)
        .addOnSuccessListener { response ->
            onResult(response.autocompletePredictions)
        }
        .addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Autocomplete request failed", exception)
        }
}
// Gets the place's details like name and address
fun fetchPlaceDetails(placeId: String, placesClient: PlacesClient, googleMap: GoogleMap, onResult: (LatLng) -> Unit) {
    val request = FetchPlaceRequest.builder(placeId, listOf(Place.Field.LAT_LNG, Place.Field.NAME)).build()

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response ->
            response.place.latLng?.let { latlng ->
                onResult(latlng)

                // Adds a marker when a place is searched
                val marker = googleMap.addMarker(
                    MarkerOptions()
                        .position(latlng)
                        .title(response.place.name)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))// Blue marker for searched places
                )
                marker?.tag = placeId
            }
        }
        .addOnFailureListener { exception ->
            Log.e("PlacesAPI", "Place details request failed", exception)
        }
}
// Helper function to get the user's location
private fun getUserLocation(
    context: Context,
    fusedLocationProviderClient: FusedLocationProviderClient,
    onLocationReceived: (LatLng) -> Unit
) {
    if (ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                onLocationReceived(LatLng(it.latitude, it.longitude))
            }
        }
    }
}
//Test composable to see the layout in the preview without having to run the app
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicSearchLayout(modifier: Modifier = Modifier, navController: NavController) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title =
            {
                Text(
                    text = "Find a Clinic",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            navigationIcon = {
                IconButton(onClick = { navController.navigate("home") }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Button",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
        )

        // Display map
        Box(
            modifier = modifier
                .height(330.dp)
                .fillMaxWidth()
                .background(Color.Blue)
        )

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Black
        )
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start){
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(),
                text = "Locate: ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        var query by remember { mutableStateOf("") }

        TextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Search for dental clinics") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )
        Text("Select Search Radius")
        Spacer(modifier = Modifier.padding(bottom = 8.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                onClick = { null }) {
                Text("5 miles")
            }
            Button(
                onClick = { null }) {
                Text("10 miles")
            }
            Button(
                onClick = { null }) {
                Text("20 miles")
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun ClinicSearchPagePreview() {
    ClinicSearchLayout(navController = rememberNavController())
}