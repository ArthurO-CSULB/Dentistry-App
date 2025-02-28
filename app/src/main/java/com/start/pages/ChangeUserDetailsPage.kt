package com.start.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.oAuthProvider
import com.start.AuthViewModel

/*
We have a composable settings page which will handle the UI for settings
where the user can change many of their app preferences.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */

// A page in settings that shows which user details can be changed by the user
@Composable
fun ChangeUserDetailsPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    // We create two variables of email and password and use by remember for the data to persist
    // across recompositions.
    var firstName by remember {
        mutableStateOf("")
    }
    var lastName by remember {
        mutableStateOf("")
    }

    // Change User Details Page UI
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(64.dp))
        // Title of User Details Page
        Text(
            text = "Change User Details", fontSize = 32.sp
        )

        Spacer(modifier=Modifier.height(16.dp))

        // TextField for user input of email. Email reference updates upon user input.
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
            },
            label = {
                Text(text = "First Name")
            }
        )

        // TextField for user input of email. Email reference updates upon user input.
        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
            },
            label = {
                Text(text = "Last Name")
            }
        )

        TextButton(onClick = {
            authViewModel.changeUserDetails(firstName, lastName)
            navController.navigate("profile")
        }) {
            Text(text = "Change User Details")
        }

        // Button to go back to settings.
        TextButton(onClick = {
            navController.navigate("settings")
        }) {
            Text(text = "Go back to settings")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ShowPage() {
    ChangeUserDetailsPage(
        navController = rememberNavController(),
        authViewModel = TODO()
    )
}