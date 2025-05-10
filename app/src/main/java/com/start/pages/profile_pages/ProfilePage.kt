package com.start.pages.profile_pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dentalhygiene.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.start.pages.ButtonSizes
import com.start.pages.FeatureItem
import com.start.pages.FeatureRow
import com.start.viewmodels.PointsProgressionViewModel

/*
We have a composable profile page which will handle the UI for profile options.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */
@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController, pointsProgressionViewModel: PointsProgressionViewModel) {
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

    // Gets the user's equipped emblem and loads it when the page loads
    val emblem by pointsProgressionViewModel.equippedEmblem
    LaunchedEffect(Unit) {
        pointsProgressionViewModel.loadEquipped()
    }

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
            text = "THE Profile Page", fontSize = 32.sp
        )

        // Space between the title text and user information

        //Spacer(modifier = Modifier.height(4.dp))

        // If the user doesn't have an emblem equipped, shows the default one instead
        if (emblem == "" || emblem == null){
            Image(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = "Profile Picture",
                modifier = modifier.size(160.dp)
            )
        } else
        {
            // Displays the user's equipped emblem
            AsyncImage(
                model = emblem,
                contentDescription = "Emblem",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .size(160.dp)
                    .padding(8.dp)
                    //.clip(RoundedCornerShape(8.dp))
            )
        }

        // Button to edit profile NOT IMPLEMENTED.
        Button(onClick={navController.navigate("changeUserDetails")}) {
            Text(text = "Edit Profile")
        }
        Spacer(modifier = Modifier.height(8.dp))

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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Text(text = "Experience Points: ")
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(color = Color.White, shape = RoundedCornerShape(4.dp))
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = experience,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // Button to go to Bookmark page.
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = {
//            navController.navigate("bookmark")
//        }) {
//            Text(text = "Clinic Bookmarks", fontSize = 20.sp)
//        }
//
//        Spacer(Modifier.height(16.dp))
//        Button(onClick = { navController.navigate("userRatings") }) {
//            Text("User Ratings", fontSize = 20.sp)
//        }
        FeatureRow(
            features = listOf(
                FeatureItem(
                    iconRes = R.drawable.bookmark,
                    label = "Clinic Bookmarks",
                    color = Color(0xFFA020F0),
                    containerSize = ButtonSizes.REGULAR_CONTAINER,
                    iconSize = ButtonSizes.REGULAR_ICON,
                    width = ButtonSizes.REGULAR_WIDTH,
                    shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
                )

                {
                    navController.navigate("bookmark")
                },

                FeatureItem(
                    iconRes = R.drawable.ic_ratings, // Add an appropriate icon resource
                    label = "User Ratings",
                    color = Color(0xFFFFFF00),
                    containerSize = ButtonSizes.REGULAR_CONTAINER,
                    iconSize = ButtonSizes.REGULAR_ICON,
                    width = ButtonSizes.REGULAR_WIDTH,
                    shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
                ) {
                    navController.navigate("user_Ratings")
                }

            ),

            navController = navController
        )

        Spacer(modifier = Modifier.height(8.dp))
        // Button to go to the leaderboards and stats page.
        FeatureRow(
            features = listOf(
                FeatureItem(
                    iconRes = R.drawable.stats, // Your image resource
                    label = "Leaderboards " +
                            "and Stats",
                    color = Color(0xFFFFFF00),
                    containerSize = ButtonSizes.REGULAR_CONTAINER,
                    iconSize = ButtonSizes.REGULAR_ICON,
                    width = ButtonSizes.REGULAR_WIDTH,
                    shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
                ) {
                    navController.navigate("leaderboards_stats")
                },
        // Button to go to points prestige page.
                FeatureItem(
                    iconRes = R.drawable.progress, // Your image resource
                    label = "Points & Progression",
                    color = Color(0xFFFF0000),
                    containerSize = ButtonSizes.REGULAR_CONTAINER,
                    iconSize = ButtonSizes.REGULAR_ICON,
                    width = ButtonSizes.REGULAR_WIDTH,
                    shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
                ) {
                    navController.navigate("points_progression")
                }
            ),
            navController = navController
        )
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