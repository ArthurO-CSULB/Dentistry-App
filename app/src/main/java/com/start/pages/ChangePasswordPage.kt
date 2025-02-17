package com.start.pages

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
fun ChangePasswordPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    // We create variable password and use by remember for the data to persist
    // across recompositions.
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
            // When the user inputs incorrectly, we create a Toast message of the error.
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            // Else do nothing.
            else -> Unit
        }
    }

    // Change Password UI Text
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Create the title of the page.
        Text(text = "Change Password", fontSize = 32.sp)


        // Space
        Spacer(modifier = Modifier.height(16.dp))
        // TextField for user input of password. Password reference updates upon user input.
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text(text = "New Password")
            }
        )

        // Space
        Spacer(modifier = Modifier.height(16.dp))
        // Button for changing password
        Button(onClick = {
            authViewModel.changePassword(password)
            authViewModel.checkAuthStatus()
            navController.navigate("profile")
        },
            // Button enabled when the authentication state is not loading.
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Change Password")
        }
    }
}