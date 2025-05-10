package com.start.pages.profile_pages

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
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

@Composable
fun LeaderboardsStatsPage(modifier: Modifier, navController: NavController,
                          leaderBoardStatsViewModel: LeaderboardsStatsViewModel) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title of Page
        Box(
            modifier = modifier
                .border(1.dp, Color.Black)
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Leaderboards and Stats",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 1.5.em
            )
        }

        // List of stats
        Column(
            modifier = modifier
                .border(1.dp, Color.Black)
                .weight(2f)
        ) {
            // TODO: use the viewModel to get the status, then display some here Add more in the future.
            Text("User with the most points: ...")
            Text("The most consistent tooth brusher: ...")
            Text("The most liked dentist: ...")
            Text("Highest score on flappy tooth: ...")
            Text("The smartest dental scholar (deals with trivia): ...")
        }


    }

}