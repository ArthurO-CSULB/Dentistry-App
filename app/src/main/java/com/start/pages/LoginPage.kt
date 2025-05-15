package com.start.pages

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import com.start.viewmodels.AuthState
import com.start.viewmodels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate

/*
We have a composable login page which will handle the UI for login integrated with
AuthViewModel. This will be called in the PageNavigation NavHost, passing in the modifier,
NavController, and AuthViewModel.

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
 */
@RequiresApi(Build.VERSION_CODES.O)
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
        when (authState.value) {
            is AuthState.Authenticated -> {
                // Add this block
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                userId?.let { uid ->
                    FirebaseFirestore.getInstance()
                        .collection("accounts")
                        .document(uid)
                        .collection("streaks")
                        .document("currentStreak")
                        .get()
                        .addOnSuccessListener { doc ->
                            val today = LocalDate.now().toString()
                            if (!doc.exists()) {
                                // Initialize streak if new user
                                doc.reference.set(
                                    mapOf(
                                        "lastLoginDate" to today,
                                        "currentCount" to 1,
                                        "longestCount" to 1
                                    )
                                )
                            } else {
                                // Update existing streak
                                val lastDate = LocalDate.parse(doc.getString("lastLoginDate")!!)
                                val current = doc.getLong("currentCount") ?: 0
                                val longest = doc.getLong("longestCount") ?: 0

                                val newCount =
                                    if (lastDate.isBefore(LocalDate.now().minusDays(1))) {
                                        1 // Reset if broken
                                    } else {
                                        current + 1
                                    }

                                doc.reference.update(
                                    mapOf(
                                        "lastLoginDate" to today,
                                        "currentCount" to newCount,
                                        "longestCount" to maxOf(longest, newCount)
                                    )
                                )
                            }
                        }
                }

                navController.navigate("home") // Keep this last
            }

            is AuthState.Error -> TODO()
            AuthState.Loading -> TODO()
            AuthState.UnAuthenticated -> TODO()
            AuthState.Unverified -> TODO()
            null -> TODO()
        }
    }
    // Login Page UI Text
    // We create a Column to arrange the UI components
    // ToDo: 2/1/2025 Improve UI of Login Page
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
            },
            // Shows a keyboard when the text box is typed,
            // keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),

            // makes password hidden
            // TODO: 2/6/2025 design and implement a function to toggle password visibility
            visualTransformation = PasswordVisualTransformation()

        )

        // Space
        Spacer(modifier = Modifier.height(16.dp))
        // Button for logging in
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