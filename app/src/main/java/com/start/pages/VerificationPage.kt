package com.start.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.AuthState
import com.start.AuthViewModel


// Authentication page called after registering for an account
// ToDo: Improve this page's UI
@Composable
fun VerificationPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

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
            // When the user already verified their email, they are sent to the home page.
            is AuthState.Authenticated -> navController.navigate("home")
            // When the user inputs incorrectly, we create a Toast message of the error.
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            // Else do nothing.
            else -> Unit
        }
    }

    // Verification Page
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(16.dp))
        // Text
        Text(
            text = "Please Verify Your Email", fontSize = 32.sp
        )

        // Spacer
        Spacer(modifier=Modifier.height(16.dp))
        // Button to go back to login page
        TextButton(onClick = {
            authViewModel.checkAuthStatus()
            if (authState.value is AuthState.Authenticated) {
                navController.navigate("home")
            } else {
                authViewModel.signout()
                navController.navigate("login")
            }
        }) {
            Text(text = "I am verified already")
        }

        //Space
        Spacer(modifier=Modifier.height(16.dp))

        // Button to send a verification email
        TextButton(onClick = {
            authViewModel.sendVerificationEmail()
        }) {
            Text(text = "Resend verification email")
        }
    }
}