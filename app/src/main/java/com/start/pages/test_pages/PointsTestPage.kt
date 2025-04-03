package com.start.pages.test_pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import com.start.viewmodels.PointsProgressionViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun PointsTestPage(modifier: Modifier, navController: NavController, pointsProgressionViewModel: PointsProgressionViewModel) {

    var currPrestige by remember { mutableStateOf("")}
    
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Points Test!", fontWeight = FontWeight.Bold, fontSize = 16.sp,
            lineHeight = 1.5.em)
        Text("Prestige: $currPrestige", fontSize = 16.sp, lineHeight = 1.5.em)
        Button(onClick = {pointsProgressionViewModel.addTriviaPoints(1)}) {
            Text("Add 1 Point")
        }
        Button(onClick = {pointsProgressionViewModel.prestige()}) {
            Text("Prestige")
        }
        Button(onClick = {
            // Get prestige
            pointsProgressionViewModel.getPrestige {
                prestige -> currPrestige = prestige.toString()
            }
        }) {
            Text("Get Prestige")
        }

    }
}
