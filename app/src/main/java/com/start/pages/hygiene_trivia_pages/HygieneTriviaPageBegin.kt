package com.start.pages.hygiene_trivia_pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.start.viewmodels.HygieneTriviaState
import com.start.viewmodels.HygieneTriviaViewModel

@Composable
fun HygieneTriviaPageBegin(modifier: Modifier, navController: NavController, hygieneTriviaViewModel: HygieneTriviaViewModel) {

    // Use BackHandler to intercept the system back button and navigate to the home screen.
    BackHandler {
        // Navigate back to the home screen when the system back button is pressed
        navController.popBackStack("home", inclusive = false)
    }

    // Collect the state of the hygiene trivia.
    val hygieneTriviaState = hygieneTriviaViewModel.hygieneTriviaState.collectAsState()

    // When the state of the trivia is changed to trivia, navigate to the trivia page.
    LaunchedEffect(hygieneTriviaState.value) {
        when (hygieneTriviaState.value) {
            is HygieneTriviaState.Trivia -> (navController.navigate("trivia_trivia"))
            else -> Unit
        }
    }

    // Column for the trivia page.
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Dental Trivia!", fontWeight = FontWeight.Bold, fontSize = 64.sp,
            lineHeight = 1.5.em)
        Text("Press the Button to Begin!", fontStyle = FontStyle.Italic, fontSize = 24.sp,
            lineHeight = 1.5.em)

        Button(onClick = {hygieneTriviaViewModel.beginTrivia()}) {
            Text("Begin!")
        }
        TextButton(onClick = {navController.popBackStack("home", inclusive = false)}) {
            Text("Go Home")
        }


    }
}