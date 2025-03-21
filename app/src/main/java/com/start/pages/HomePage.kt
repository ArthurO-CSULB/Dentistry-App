package com.start.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.viewmodels.AuthState
import com.start.viewmodels.AuthViewModel
import com.start.viewmodels.HygieneTriviaState
import com.start.viewmodels.HygieneTriviaViewModel
import com.start.viewmodels.TimerState
import com.start.viewmodels.TimerViewModel

/*
We have a composable home page which will handle the UI for choosing different features of the app.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController, and AuthViewModel.

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
 */
@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController,
             authViewModel: AuthViewModel, timerViewModel: TimerViewModel,
             hygieneTriviaViewModel: HygieneTriviaViewModel) {

    // From the passed in AuthViewModel, we get the authState of the authentication and use
    // observeAsState() to subscribe to the live data and track its changes.
    val authState = authViewModel.authState.observeAsState()
    // We get the timer state.
    val timerState = timerViewModel.timerState.collectAsState()
    // We get the state of the trivia.
    val hygieneTriviaState = hygieneTriviaViewModel.hygieneTriviaState.collectAsState()

    // We create a launched effect that passes in the value of the authentication state. Upon
    // the value changing when calling authViewModel methods, the block of code will execute.
    LaunchedEffect(authState.value) {
        when (authState.value){
            // When the user is unauthenticated by singing out, navigate to the login screen.
            is AuthState.UnAuthenticated -> navController.navigate("login")
            // When the user is unverified, navigate to verification screen.
            is AuthState.Unverified -> navController.navigate("verification")
            // Else nothing.
            else -> Unit
        }
    }

    // Home Page UI
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Space between top of screen and title text
        Spacer(modifier=Modifier.height(32.dp))

        // Title of Home Page
        Text(
            text = "Prototype Home Page", fontSize = 32.sp
        )

        // Space
        Spacer(modifier=Modifier.height(16.dp))
        // Button to the timer page. Navigate depending to certain page depending on specific state.
        Button(onClick={
            when(timerState.value) {
                is TimerState.Begin -> navController.navigate("timer_begin")
                is TimerState.Counting -> navController.navigate("timer_counting")
                is TimerState.Cancel -> navController.navigate("timer_cancel")
                is TimerState.Finished -> navController.navigate("timer_finish")
                else -> Unit
            }
            navController.navigate("timer_begin")
        }) {
            Text(text = "Prototype Toothbrush Timer")
        }
        // Space
        Spacer(modifier=Modifier.height(8.dp))

        Button(onClick={
            when(hygieneTriviaState.value) {
                is HygieneTriviaState.Begin -> navController.navigate("trivia_begin")
                is HygieneTriviaState.Trivia -> navController.navigate("trivia_trivia")
                is HygieneTriviaState.Finished -> navController.navigate("trivia_finish")
                else -> Unit
            }
        }) {
            Text(text = "Dental Hygiene Trivia")
        }

        Spacer(modifier=Modifier.height(8.dp))

        // Button to the Calendar page.
        Button(onClick={navController.navigate("calendar")}) {
            Text(text = "Prototype Calendar")
        }
        // Space
        Spacer(modifier=Modifier.height(8.dp))
        // Button to the Games page.
        Button(onClick={navController.navigate("games")}) {
            Text(text = "Prototype Games")
        }
        // Space
        Spacer(modifier=Modifier.height(8.dp))
        // Button to the Glossary page.
        Button(onClick={navController.navigate("glossary")}) {
            Text(text = "Prototype Glossary")
        }

        // Space
        Spacer(modifier=Modifier.height(8.dp))
        // Button to the Clinic Search page.
        Button(onClick={navController.navigate("search")}) {
            Text(text = "Prototype Clinic Search")
        }

        // Space
        Spacer(modifier=Modifier.height(8.dp))
        // Button to the Profile page.
        Button(onClick={navController.navigate("profile")}) {
            Text(text = "Prototype Profile")
        }

        // Space
        Spacer(modifier=Modifier.height(8.dp))
        // Button to the Profile page.
        Button(onClick={navController.navigate("verification")}) {
            Text(text = "Prototype Verification")
        }

        // Space
        Spacer(modifier=Modifier.height(8.dp))
        // Button to the Profile page.
        Button(onClick={navController.navigate("settings")}) {
            Text(text = "Prototype Settings")
        }
    }

    //Arrange another column only for Sign Out button
    //Always stays at bottom of screen
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        // Text Button to sign out.
        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = "Sign Out", fontSize = 32.sp)
        }
    }
}