package com.start.pages.profile_pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/*
We have a composable profile page which will handle the UI for profile options.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */
@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController) {
    // From the passed in AuthViewModel, we get the authState of the authentication and use
    // observeAsState() to subscribe to the live data and track its changes.
    //val authState = authViewModel.authState.observeAsState()

    // Grab user information whose profile will be displayed
    var firstName by remember {mutableStateOf("")}
    var lastName by remember {mutableStateOf("")}
    var email by remember {mutableStateOf("")}
    var experience by remember {mutableStateOf("")}

    // Grab user information from database
    val db = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser

    // Fetch user details from Firestore
    LaunchedEffect(user) {
        user?.uid?.let { uid ->
            db.collection("accounts").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        firstName = document.getString("firstName") ?: "N/A"
                        lastName = document.getString("lastName") ?: "N/A"
                        email = document.getString("email") ?: "N/A"
                        experience = document.getLong("experience").toString()
                    }
                }
                .addOnFailureListener {
                    // Handle failure (e.g., log error)
                }
        }
    }

    // Profile Page UI
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Space between top of screen and title text
        Spacer(modifier = Modifier.height(16.dp))

        // Title of Profile Page
        Text(
            text = "Profile Page", fontSize = 32.sp
        )

        // Space between the title text and user information
        Spacer(modifier = Modifier.height(16.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_profile),
            contentDescription = "Profile Picture",
            modifier = Modifier.size(160.dp)
        )

        // Button to edit profile NOT IMPLEMENTED.
        Button(onClick={navController.navigate("changeUserDetails")}) {
            Text(text = "Edit Profile")
        }
        Spacer(modifier = Modifier.height(16.dp))

        // User information
        // Name
        Text(text = "Name: ")
        Box(
            modifier = Modifier
                .background(color = Color.White)
                .padding(8.dp)
        ){
        Text(text = "$firstName $lastName", fontSize = 20.sp)}
        Spacer(modifier = Modifier.height(16.dp))

        // Email
        Text(text = "Email: ")
        Box(
            modifier = Modifier
                .background(color = Color.White)
                .padding(8.dp)
        ) {
            Text(text = email, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Experience
        Text(text = "Experience Points: ")
        Box(
            modifier = Modifier
                .background(color = Color.White)
                .padding(8.dp)
        ) {
            Text(text = experience, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Button to go to Bookmark page.
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            navController.navigate("bookmark")
        }) {
            Text(text = "Clinic Bookmarks", fontSize = 20.sp)
        }

        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.navigate("userRatings") }) {
            Text("User Ratings", fontSize = 20.sp)
        }


        // Button to go to points prestige page.
        TextButton(onClick = {
            navController.navigate("points_progression")
        }) {
            Text(text = "Points and Progression", fontSize = 20.sp)
        }

    }
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home", fontSize = 32.sp)
        }
    }
}