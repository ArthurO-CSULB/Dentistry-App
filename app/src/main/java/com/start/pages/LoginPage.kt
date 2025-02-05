package com.start.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.AuthState
import com.start.AuthViewModel

/*
We have a composable login page which will handle the UI for login integrated with
AuthViewModel. This will be called in the PageNavigation NavHost, passing in the modifier,
NavController, and AuthViewModel.

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
 */
@Composable
fun LoginPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    // We create two variables of email and password and use by remember for the data to persist
    // across recompositions.
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

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
            // When the user is unverified via email, navigate to verification page
            is AuthState.Unverified -> navController.navigate("verification")
            // When the user is authenticated by login, navigate to the home page.
            is AuthState.Authenticated -> navController.navigate("home")
            // When the user inputs incorrectly, we create a Toast message of the error.
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            // Else do nothing.
            else -> Unit
        }
    }

    // Login Page UI Text
    // We create a Column to arrange the UI components
    // ToDo: 2/1/2025 Improve UI of Login Page
    // ToDo: 2/1/2025 Make password hidden when typing
    // ToDo: 2/1/2025 Make initial fields appear again when they are currently empty
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Create the title of the page.
        Text(text = "Welcome to", fontSize = 32.sp)

        // mOral Logo
        Image(
            painter = painterResource(id = R.drawable.moral_logo),
            contentDescription = "Profile Picture",
            modifier = Modifier.size(200.dp)
        )

        // Space
        Spacer(modifier = Modifier.height(16.dp))
        // TextField for user input of email. Email reference updates upon user input.
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text(text = "Email")
            }
        )

        // Space
        Spacer(modifier = Modifier.height(8.dp))
        // TextField for user input of password. Password reference updates upon user input.
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "Password")
            }
        )

        // Space
        Spacer(modifier = Modifier.height(16.dp))
        // Button for creating an account
        Button(onClick = {
            authViewModel.login(email, password)
            if (authState.value == AuthState.Unverified) {
                navController.navigate("verification")
            }
        },
            // Button enabled when the authentication state is not loading.
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Login")
        }

        // Space
        Spacer(modifier = Modifier.height(8.dp))
        // Button to navigate to login.
        TextButton(onClick = {
            navController.navigate("signup")
        }) {
            Text(text = "Don't have an account? Sign up!")
        }
    }
}