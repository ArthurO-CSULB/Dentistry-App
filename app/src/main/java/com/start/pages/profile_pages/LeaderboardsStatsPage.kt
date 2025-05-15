package com.start.pages.profile_pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.start.viewmodels.LeaderboardsStatsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardsStatsPage(modifier: Modifier, navController: NavController,
                          leaderBoardStatsViewModel: LeaderboardsStatsViewModel) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar to go back to the profile page
        TopAppBar(
            title =
            {
                Text(
                    text = "Back to Profile Page",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            navigationIcon = {
                IconButton(onClick = {navController.popBackStack()}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back Button",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults. topAppBarColors(MaterialTheme.colorScheme.secondaryContainer)
        )

        // Title of Page
        Box(
            modifier = modifier
                //.border(1.dp, Color.Black)
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Leaderboards and Stats",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 1.5.em,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }

        // List of stats
        Column(
            modifier = modifier
                .border(
                    1.dp,
                    Color.Black,
                    shape = RoundedCornerShape(16.dp)
                )
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            // TODO: use the viewModel to get the status, then display some here Add more in the future.
            Text(
                "User with the most points:\n______",
                textAlign = TextAlign.Center,
                lineHeight = 2.em,
                color = MaterialTheme.colorScheme.secondaryContainer,
            );
            Text(
                "The most consistent tooth brusher:\n______", textAlign =
                TextAlign.Center,
                lineHeight = 2.em,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            Text(
                "The most liked dentist:\n______",
                textAlign = TextAlign.Center,
                lineHeight = 2.em,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            Text(
                "Highest score on flappy tooth:\n______",
                textAlign = TextAlign.Center,
                lineHeight = 2.em,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
            Text(
                "The smartest dental scholar (deals with trivia):\n______",
                textAlign = TextAlign.Center,
                lineHeight = 2.em,
                color = MaterialTheme.colorScheme.secondaryContainer
            )
        }


    }

}