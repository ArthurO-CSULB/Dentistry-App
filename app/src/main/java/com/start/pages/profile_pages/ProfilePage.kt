package com.start.pages.profile_pages

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.dentalhygiene.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.start.pages.ButtonSizes
import com.start.viewmodels.PointsProgressionViewModel
import com.start.viewmodels.ThemeViewModel

/*
We have a composable profile page which will handle the UI for profile options.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ProfilePage(modifier: Modifier = Modifier, navController: NavController,
                pointsProgressionViewModel: PointsProgressionViewModel,
                themeViewModel: ThemeViewModel) {
    // From the passed in AuthViewModel, we get the authState of the authentication and use
    // observeAsState() to subscribe to the live data and track its changes.
    //val authState = authViewModel.authState.observeAsState()

    // Grab user information whose profile will be displayed
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    val streak = 1
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
        Spacer(modifier = Modifier.height(12.dp))

        // Title of Profile Page
        Text(
            text = "THE Profile Page", fontSize = 32.sp
        )

        // Space between the title text and user information

        //Spacer(modifier = Modifier.height(4.dp))

        // If the user doesn't have an emblem equipped, shows the default one instead
        if (emblem == "" || emblem == null) {
            Image(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = "Profile Picture",
                modifier = modifier.size(160.dp)
            )
        } else {
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
        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = { themeViewModel.toggleTheme() },
//                modifier = Modifier.padding(8.dp)
            ) {
                Text(if (themeViewModel.isDarkMode) "Light Mode" else "Dark Mode")
            }
            Button(
                onClick = { navController.navigate("changeUserDetails") }) {
                Text(text = "Edit Profile")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // User information
        // Name
        Text(
            text = "Name: ",
            color = MaterialTheme.colorScheme.onBackground
        )
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            Text(
                text = "$firstName $lastName",
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Email
        Text(
            text = "Email: ",
            color = MaterialTheme.colorScheme.onBackground
        )
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            Text(
                text = email,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
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
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = experience,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Experience
        // Consistent size values
        val smallButtonWidth = 110.dp
        val largeButtonWidth = 180.dp
        val buttonHeight = 120.dp
        val iconSize = 50.dp
        val cornerRadius = ButtonSizes.CORNER_RADIUS

// First Row - Leaderboard, Achievements, Points
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Leaderboard Button
            Box(
                modifier = Modifier
                    .width(smallButtonWidth)
                    .height(buttonHeight)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .clickable { navController.navigate("leaderboards_stats") }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.stats),
                        contentDescription = "Leaderboard",
                        tint = Color(0xFFFFFF00),
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Leaderboard",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }

            // Achievements Button
            Box(
                modifier = Modifier
                    .width(smallButtonWidth)
                    .height(buttonHeight)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .clickable { navController.navigate("achievements") }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.trophy),
                        contentDescription = "Achievements",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Achievements",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }

            // Points Button
            Box(
                modifier = Modifier
                    .width(smallButtonWidth)
                    .height(buttonHeight)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .clickable { navController.navigate("points_progression") }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.progress),
                        contentDescription = "Points",
                        tint = Color(0xFFFF0000),
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Points",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

// Second Row - Bookmarks, Streaks, and Ratings
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Bookmarks Button
            Box(
                modifier = Modifier
                    .width(smallButtonWidth)
                    .height(buttonHeight)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .clickable { navController.navigate("bookmark") }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.bookmark),
                        contentDescription = "Bookmarks",
                        tint = Color(0xFFA020F0),
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bookmarks",
                        fontSize = 14.sp,  // Changed from 16.sp to match first row
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }

            // Streak Display
            Box(
                modifier = Modifier
                    .width(smallButtonWidth)
                    .height(buttonHeight)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.flame),
                        contentDescription = "Streak",
                        tint = if (streak > 0) Color.Red else Color.Gray,
                        modifier = Modifier.size(iconSize)  // Changed to use iconSize
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${streak} days",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }

            // Ratings Button
            Box(
                modifier = Modifier
                    .width(smallButtonWidth)
                    .height(buttonHeight)
                    .background(
                        color = Color.Black.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(cornerRadius)
                    )
                    .clickable { navController.navigate("userRatings") }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_ratings),
                        contentDescription = "Ratings",
                        tint = Color(0xFFFFFF00),
                        modifier = Modifier.size(iconSize)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ratings",
                        fontSize = 14.sp,  // Changed from 16.sp to match first row
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
            }
        }

}
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home", fontSize = 32.sp)
        }
    }
}