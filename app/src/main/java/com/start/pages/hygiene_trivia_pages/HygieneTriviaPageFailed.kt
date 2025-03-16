package com.start.pages.hygiene_trivia_pages

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.viewmodels.HygieneTriviaState
import com.start.viewmodels.HygieneTriviaViewModel


@Composable
fun HygieneTriviaPageFailed(modifier: Modifier, navController: NavController, hygieneTriviaViewModel: HygieneTriviaViewModel) {

    // Use BackHandler to intercept the system back button and navigate to the home screen.
    BackHandler {
        // Navigate back to the home screen when the system back button is pressed
        navController.popBackStack("home", inclusive = false)
    }

    val hygieneTriviaState = hygieneTriviaViewModel.hygieneTriviaState.collectAsState()

    LaunchedEffect(hygieneTriviaState.value) {
        when(hygieneTriviaState.value) {
            is HygieneTriviaState.Begin -> navController.navigate("home")
            else -> Unit
        }
    }

    Column(
        modifier= modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier=modifier
                .weight(2f)
        ) {
            Image(
                // Using the painter Resource API we display the sad face image.
                painter = painterResource(id = R.drawable.sadface_timer),
                contentDescription = stringResource(id = R.string.sad_face),
                // We crop the image to our liking.
                contentScale = ContentScale.Crop,
                modifier = modifier
                    .size(200.dp)
                    .clip(CircleShape)
            )
        }
        Row(
            modifier=modifier
                .weight(1f)
        ) {
            Text("You have failed the hygiene trivia :(", fontWeight = FontWeight.Bold,
                fontSize = 32.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 1.5.em)
        }
        Row(
            modifier=modifier
                .weight(1f)
        ) {
            Text("Try again next time!", fontWeight = FontWeight.Bold, fontSize = 32.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 1.5.em)
        }

        Row(
            modifier=modifier
                .weight(1f)
        ) {
            Button(onClick = {hygieneTriviaViewModel.resetTrivia()}) {
                Text("Go Home")
            }
        }
    }
}