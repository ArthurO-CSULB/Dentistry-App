package com.start.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.viewmodels.AuthState
import com.start.viewmodels.AuthViewModel

/*
We have a composable settings page which will handle the UI for settings
where the user can change many of their app preferences.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */

@Composable
fun SettingsPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    // From the passed in AuthViewModel, we get the authState of the authentication and use
    // observeAsState() to subscribe to the live data and track its changes.
    val authState = authViewModel.authState.observeAsState()
    // Get the context of this composable. Will be used for Toast messages.
    val context = LocalContext.current

    // We create a launched effect that passes in the value of the authentication state. Upon
    // the value changing when calling authViewModel methods, the block of code will execute.
    LaunchedEffect(authState.value) {
        // Whenever the authState is a certain authentication state...
        when (authState.value){
            is AuthState.UnAuthenticated -> navController.navigate("login")
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            // Else do nothing.
            else -> Unit
        }
    }

    // Settings Page UI
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(16.dp))
        // Title of Settings Page
        Text(
            text = "Settings Page", fontSize = 32.sp
        )

        Spacer(modifier=Modifier.height(16.dp))

        // Reauthentication before accessing user details page
        TextButton(onClick = {
            navController.navigate("changeUserDetails")
        }) {
            Text(text = "Change User Details")
        }

        // Change Password
        TextButton(onClick = {
            navController.navigate("reauthenticationPasswordChange")
        }) {
            Text(text = "Change Password")
        }

        // Delete Account
        TextButton(onClick = {
            try{
                authViewModel.deleteAccount()
            }
            catch (e: Exception) {
                Log.e("Account Deletion",e.toString())
                navController.navigate("reauthenticationAccountDeletion")
            }

        }) {
            Text(text = "Delete Account")
        }

        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home")
        }

    }
}