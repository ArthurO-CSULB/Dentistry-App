package com.start.pages.profile_pages

// Create a new file in your pages package

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.model.Achievement
import com.start.viewmodels.AchievementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsPage(
    navController: NavController,
    achievementViewModel: AchievementViewModel
) {
    val achievements = achievementViewModel.achievements.collectAsState().value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Your Achievements") },
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back Button",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(achievements) { achievement ->
                AchievementItem(achievement = achievement)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun AchievementItem(achievement: Achievement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.earned) Color(0xFF4CAF50) else Color.LightGray
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = achievement.title,
                style = MaterialTheme.typography.titleLarge,
                color = if (achievement.earned) Color.White else Color.Black
            )
            Text(
                text = achievement.description,
                style = MaterialTheme.typography.bodyMedium,
                color = if (achievement.earned) Color.White else Color.Black
            )
            if (!achievement.earned && achievement.requiredProgress > 1) {
                Text(
                    text = "Progress: ${achievement.progress}/${achievement.requiredProgress}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (achievement.earned) Color.White else Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = achievement.progress.toFloat() / achievement.requiredProgress.toFloat(),
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2196F3)
                )
            }
        }
    }
}