package com.start.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R

/*
We have a composable clinic search page which will handle the UI for clinic search.
This will be called in the PageNavigation NavHost, passing in the modifier,
NavController.
 */

@Composable
fun ClinicSearchPage(modifier: Modifier = Modifier, navController: NavController) {

    // Calendar Page UI
    // We create a Column to arrange the UI components
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier=Modifier.height(16.dp))
        // Title of Clinic Search Page
        Text(
            text = "Prototype Clinic Search Page", fontSize = 28.sp
        )
        // Button to go back home.
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home")
        }
        Box {

            val map = painterResource(R.drawable.map)
            Image(
                painter = map,
                modifier = Modifier
                    .sizeIn(450.dp, 500.dp),
                contentDescription = null
            )
            Text(
                text = "Placeholder Map",
                modifier = Modifier
                    .align(Alignment.Center),
                fontSize = 45.sp,
                color = Color.Blue
            )
        }
    }
    Column(
        // We fill the column to the entire screen
        modifier = modifier.fillMaxSize(),
        // We center the components of the column.
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(modifier=Modifier.height(320.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(5.dp))
        HorizontalDivider(
            thickness = 2.dp,
            color = Color.Black
        )
        Text(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(8.dp, 0.dp),
            text = "Locate: ",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            modifier = Modifier
                .align(Alignment.Start)
                .padding(8.dp, 0.dp),
            text = "placeholder address text",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal
        )
    }
}