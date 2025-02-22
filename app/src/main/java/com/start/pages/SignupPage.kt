package com.start.pages

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.AuthViewModel
import com.start.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/*
We have a composable sign up page which will handle the UI for signing in integrated with
AuthViewModel. This will be called in the PageNavigation NavHost, passing in the modifier,
NavController, and AuthViewModel.

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
 */
@Composable
fun SignUpPage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel) {

    // We create four variables of email, password, first name, and last name and use by remember
    // for the data to persist across recompositions.
    var email by remember {
        mutableStateOf("")
    }
    var password by remember {
        mutableStateOf("")
    }

    var firstName by remember {
        mutableStateOf("")
    }

    var lastName by remember {
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
            // When the user is authenticated by signing up, navigate to the home page.
            is AuthState.Authenticated -> navController.navigate("home")
            // When the user inputs incorrectly, we create a Toast message of the error.
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            // Else do nothing.
            else -> Unit
        }
    }

    // Signup Page UI
    // We create a Column to arrange the UI components
    // ToDo: 2/1/2025 Improve UI of Registration Page
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Create the title of the page.
        Text(text = "Prototype Signup Page", fontSize = 32.sp)

        // Space
        Spacer(modifier = Modifier.height(16.dp))
        // TextField for user input of email. Email reference updates upon user input.
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = { Text(text = "Email")
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
            },
            // Shows a keyboard when the text box is typed,
            // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

            // makes password hidden
            // TODO: 2/6/2025 design and implement a function to toggle password visibility
            visualTransformation = PasswordVisualTransformation()
        )

        // Space
        Spacer(modifier = Modifier.height(8.dp))
        // First Name
        OutlinedTextField(
            value = firstName,
            onValueChange = {
                firstName = it
            },
            label = {
                Text(text = "First Name")
            }
        )

        // Last Name
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lastName,
            onValueChange = {
                lastName = it
            },
            label = {
                Text(text = "Last Name")
            }
        )
        // Space
        Spacer(modifier = Modifier.height(16.dp))

        // Button for creating an account
        Button(onClick = {
            // Upon click call the signup method of authViewModel. Pass in data.
            // If successful, send user to login page
            (CoroutineScope(Main)).launch{
                try {
                    val result = withContext(Main) {authViewModel.signup(email, password, firstName, lastName)}
                    if (result) {
                        navController.navigate("login")
                    }
                }
                catch(e: Exception) {
                    Log.e("Signup", e.message.toString())
                }
            }

        },
            // Button enabled when the authentication state is not loading.
            enabled = authState.value != AuthState.Loading
        ) {
            Text(text = "Create Account")
        }

        // Space
        Spacer(modifier = Modifier.height(8.dp))

        // Button navigate to login
        TextButton(onClick = {
            navController.navigate("login")
        }) {
            Text(text = "Already have an account? Login!")
        }
    }
}