package com.start.pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.start.viewmodels.AchievementViewModel
import com.start.viewmodels.PointsProgressionViewModel

@Composable
fun TeethTapper(
    modifier: Modifier = Modifier,
    navController: NavController,
    pointsProgressionViewModel: PointsProgressionViewModel,
    achievementViewModel: AchievementViewModel
) {
    // Game dimensions
    val screenWidth = 1000f
    val screenHeight = 1600f
    val teethHeight = 80f

    // Game state
    var score by remember { mutableIntStateOf(0) }
    var gameTime by remember { mutableLongStateOf(0L) }
    var isGameRunning by remember { mutableStateOf(false) }
    var sugarCubes by remember { mutableStateOf(emptyList<SugarCube>()) }
    var pointsAdded by remember { mutableStateOf<Int?>(null) }

    // Game difficulty parameters
    val baseSpeed = 6f
    val baseSpawnRate = 2000L // milliseconds
    var currentSpeed by remember { mutableFloatStateOf(baseSpeed) }
    var currentSpawnRate by remember { mutableLongStateOf(baseSpawnRate) }

    // Sugar cube properties
    val cubeSize = 50.dp
    val density = LocalDensity.current
    val cubeSizePx = with(density) { cubeSize.toPx() }
    val cubeColors = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFFFF69B4), // Pink
        Color(0xFFADD8E6), // Light blue
        Color(0xFF98FB98)  // Pale green
    )

    // Game loop
    LaunchedEffect(isGameRunning) {
        var lastSpawnTime = 0L

        while (isGameRunning) {
            val frameTime = System.currentTimeMillis()

            // Spawn new sugar cubes
            if (frameTime - lastSpawnTime > currentSpawnRate) {
                lastSpawnTime = frameTime
                sugarCubes = sugarCubes + SugarCube(
                    x = Random.nextFloat() * (screenWidth - cubeSizePx),
                    y = 0f,
                    color = cubeColors[Random.nextInt(cubeColors.size)],
                )
            }

            // Update existing sugar cubes
            sugarCubes = sugarCubes.map { cube ->
                cube.copy(y = cube.y + currentSpeed)
            }.filter { cube ->
                cube.y < screenHeight - teethHeight
            }

            // Check for missed cubes (reached the teeth)
            val missedCubes = sugarCubes.count { cube ->
                cube.y + cubeSizePx >= screenHeight - teethHeight
            }

            if (missedCubes > 0) {
                // End game if any cubes reach the teeth
                isGameRunning = false
//                if (score > 9) {
//                    achievementViewModel.incrementAchievement("sugar_rush")
//                }
                //pointsProgressionViewModel.updateSugarCubePoints(score)
            }

            // Increase difficulty over time
            gameTime += 16L
            currentSpeed = baseSpeed + (gameTime / 15000f) * 0.5f  // Slower difficulty ramp
            currentSpawnRate = (baseSpawnRate - (gameTime / 2000f).coerceAtLeast(0f) * 10f)
                .toLong()
                .coerceAtLeast(700L)

            delay(16L) // ~60 FPS
        }

        if (!isGameRunning && score > 0) {
            updateSugarCubePoints(score) { added ->
                pointsAdded = added
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE6E6FA))
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    if (isGameRunning) {
                        // Filter out clicked cubes immediately
                        sugarCubes = sugarCubes.mapNotNull { cube ->
                            if (tapOffset.x in cube.x..(cube.x + cubeSizePx) &&
                                tapOffset.y in cube.y..(cube.y + cubeSizePx)) {
                                score++
                                null // remove this cube
                            } else {
                                cube
                            }
                        }

                    }
                }
            }
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Score: $score",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(28.dp)
                    .weight(1f)
            )

            if (!isGameRunning && score >= 1) {
                Text(
                    text = "Points Got: $score",
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(28.dp)
                        .weight(1f)
                )
            } else {
                Spacer(Modifier.weight(1f))
            }

            TextButton(
                onClick = { navController.navigate("home") },
                modifier = Modifier.padding(12.dp)
            ) {
                Text("Home", color = Color.White)
            }
        }

        // Game Canvas
        Box(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                // Only draw non-clicked cubes (filtered in tap handler)
                sugarCubes.forEach { cube ->
                    drawRect(
                        color = cube.color,
                        topLeft = Offset(cube.x, cube.y),
                        size = androidx.compose.ui.geometry.Size(cubeSizePx, cubeSizePx)
                    )
                }

                // Draw teeth at bottom
                val toothWidth = size.width / 8f
                val teethTopPosition = size.height - teethHeight // Higher position
                for (i in 0..7) {
                    drawArc(
                        color = Color.White,
                        startAngle = 180f, // Flipped to face upwards
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(i * toothWidth, teethTopPosition - teethHeight), // Adjusted position
                        size = androidx.compose.ui.geometry.Size(toothWidth, teethHeight * 2)
                    )
                }
            }
            val missedCubes = sugarCubes.count { cube ->
                cube.y + cubeSizePx >= screenHeight - teethHeight // Collision at teeth line
            }
        }

        // Game controls
        if (!isGameRunning) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (score > 0) {
                    Text(
                        text = "Game Over! Score: $score",
                        fontSize = 24.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(16.dp)
                    )

                    if (pointsAdded != null) {
                        Text(
                            text = "Points added: $pointsAdded",
                            fontSize = 20.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                TextButton(
                    onClick = {
                        // Reset game state
                        score = 0
                        gameTime = 0L
                        currentSpeed = baseSpeed
                        currentSpawnRate = baseSpawnRate
                        sugarCubes = emptyList()
                        isGameRunning = true
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Start Game", fontSize = 20.sp)
                }
            }
        }
    }
}

// Data classes
data class SugarCube(
    val x: Float,
    val y: Float,
    val color: Color,
    )

fun updateSugarCubePoints(score: Int, onComplete: (Int) -> Unit) {
    val auth = Firebase.auth
    val db = Firebase.firestore

    val userId = auth.currentUser?.uid
    if (userId != null) {
        val userRef = db.collection("users").document(userId)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(userRef)
            val currentPoints = snapshot.getLong("points") ?: 0L
            val newPoints = currentPoints + score
            transaction.update(userRef, "points", newPoints)
            score // return score to pass to onComplete
        }.addOnSuccessListener { addedScore ->
            println("Successfully updated points")
            onComplete(addedScore)
        }.addOnFailureListener { e ->
            println("Error updating points: ${e.message}")
            onComplete(0) // fallback if failed
        }
    } else {
        onComplete(0)
    }
}