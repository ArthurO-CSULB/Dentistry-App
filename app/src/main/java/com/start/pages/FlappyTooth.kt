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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlin.random.Random
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.FirebaseFirestore
import com.start.viewmodels.AchievementViewModel
import com.start.viewmodels.PointsProgressionViewModel

@Composable
fun FlappyTooth(modifier: Modifier = Modifier, navController: NavController,
                pointsProgressionViewModel: PointsProgressionViewModel, achievementViewModel: AchievementViewModel) {
    // Game dimensions
    val screenWidth = 1000f
    val screenHeight = 1600f
    val centerX = screenWidth / 2

    // Game state
    var birdY by remember { mutableStateOf(screenHeight / 2) }
    var birdVelocity by remember { mutableStateOf(0f) }
    var isGameRunning by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var pipes by remember { mutableStateOf(emptyList<Pipe>()) }
    var showDebug by remember { mutableStateOf(false) }
    var pointsAdded by remember { mutableStateOf<Int?>(null) }

    // Game constants
    val gravity = 0.5f
    val flapStrength = -12f
    val birdSize = 40.dp
    val pipeWidth = 80.dp
    val pipeGap = 260.dp
    val pipeSpeed = 6f
    val pipeSpacing = 575f
    val groundHeight = 100f
    val ceilingHeight = 50f

    // Convert Dp to pixels
    val density = LocalDensity.current
    val birdSizePx = with(density) { birdSize.toPx() }
    val pipeWidthPx = with(density) { pipeWidth.toPx() }
    val pipeGapPx = with(density) { pipeGap.toPx() }

    // Game loop with sub-frame collision checking
    LaunchedEffect(isGameRunning) {
        while (isGameRunning) {
            val frameTime = 16L // ms per frame
            val steps = 3 // Check 3 positions per frame
            var collisionOccurred = false

            // Using a label and explicit loop control
            stepLoop@ for (i in 0 until steps) {
                // Update physics in smaller steps
                birdVelocity += gravity / steps
                val newBirdY = birdY + birdVelocity / steps

                // Check for collisions along the movement path
                val collision = checkCollision(
                    birdY = newBirdY,
                    birdSizePx = birdSizePx,
                    pipes = pipes,
                    pipeWidthPx = pipeWidthPx,
                    pipeGapPx = pipeGapPx,
                    groundHeight = groundHeight,
                    ceilingHeight = ceilingHeight,
                    centerX = centerX,
                    screenHeight = screenHeight
                )

                if (collision) {
                    isGameRunning = false
                    collisionOccurred = true
                    if (score > 4)
                        achievementViewModel.incrementAchievement("flappy_tappy")
                    break@stepLoop
                } else {
                    birdY = newBirdY.coerceIn(
                        minimumValue = birdSizePx/2 + ceilingHeight,
                        maximumValue = screenHeight - groundHeight - birdSizePx/2
                    )
                }

                delay(frameTime / steps)
            }

            if (collisionOccurred) {
                // Add the score to database if collision.
                pointsProgressionViewModel.addFlappyToothPoints(score)
                continue
            }

            // Update pipes
            pipes = pipes.map { it.copy(x = it.x - pipeSpeed) }
                .filter { it.x + pipeWidthPx > 0 }

            // Add new pipes with proper spacing
            if (pipes.isEmpty() || pipes.last().x < screenWidth - pipeSpacing) {
                pipes = pipes + Pipe(
                    x = screenWidth,
                    gapY = Random.nextFloat() * (screenHeight - pipeGapPx - 200f) + 75f,
                    passed = false
                )
            }

            // Score increment
            pipes.firstOrNull { pipe ->
                pipe.x + pipeWidthPx < centerX && !pipe.passed
            }?.let { passedPipe ->
                score++
                pipes = pipes.map { pipe ->
                    if (pipe == passedPipe) pipe.copy(passed = true) else pipe
                }
            }
        }
        if (!isGameRunning) {
            updateUserPoints(score) { added ->
                pointsAdded = added
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF87CEEB)) // Sky blue
            .pointerInput(Unit) {
                detectTapGestures {
                    if (!isGameRunning) {
                        // Reset game
                        birdY = screenHeight / 2
                        birdVelocity = 0f
                        pipes = emptyList()
                        score = 0
                        isGameRunning = true
                    } else {
                        birdVelocity = flapStrength
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
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = groundHeight.dp)
        ) {
            // Draw ground
            drawRect(
                color = Color(0xFF8B4513), // Brown
                topLeft = Offset(0f, size.height - groundHeight),
                size = androidx.compose.ui.geometry.Size(size.width, groundHeight)
            )

            // Draw ceiling
            drawRect(
                color = Color(0xFF8B4513),
                topLeft = Offset(0f, 0f),
                size = androidx.compose.ui.geometry.Size(size.width, ceilingHeight)
            )

            // Draw bird
            drawCircle(
                color = Color.White,
                center = Offset(centerX, birdY),
                radius = birdSizePx / 2
            )

            // Draw pipes
            for (pipe in pipes) {
                // Top pipe
                drawRect(
                    color = Color(0xFF00AA00), // Dark green
                    topLeft = Offset(pipe.x, ceilingHeight),
                    size = androidx.compose.ui.geometry.Size(pipeWidthPx, pipe.gapY - ceilingHeight)
                )

                // Bottom pipe
                drawRect(
                    color = Color(0xFF00AA00),
                    topLeft = Offset(pipe.x, pipe.gapY + pipeGapPx),
                    size = androidx.compose.ui.geometry.Size(pipeWidthPx, size.height)
                )

                // Debug collision boxes
                if (showDebug) {
                    // Bird
                    drawRect(
                        color = Color.Red.copy(alpha = 0.3f),
                        topLeft = Offset(
                            centerX - birdSizePx * 0.6f/2,
                            birdY - birdSizePx * 0.6f/2
                        ),
                        size = androidx.compose.ui.geometry.Size(
                            birdSizePx * 0.6f,
                            birdSizePx * 0.6f
                        )
                    )

                    // Pipes
                    drawRect(
                        color = Color.Red.copy(alpha = 0.3f),
                        topLeft = Offset(pipe.x, ceilingHeight),
                        size = androidx.compose.ui.geometry.Size(pipeWidthPx, pipe.gapY - ceilingHeight)
                    )
                    drawRect(
                        color = Color.Red.copy(alpha = 0.3f),
                        topLeft = Offset(pipe.x, pipe.gapY + pipeGapPx),
                        size = androidx.compose.ui.geometry.Size(pipeWidthPx, size.height)
                    )
                }
            }
        }
    }
}

// Helper function for collision detection
fun checkCollision(
    birdY: Float,
    birdSizePx: Float,
    pipes: List<Pipe>,
    pipeWidthPx: Float,
    pipeGapPx: Float,
    groundHeight: Float,
    ceilingHeight: Float,
    centerX: Float,
    screenHeight: Float
): Boolean {
    val birdBox = Rect(
        x = centerX - birdSizePx * 0.6f/2,
        y = birdY - birdSizePx * 0.6f/2,
        width = birdSizePx * 0.6f,
        height = birdSizePx * 0.6f
    )

    // Ground collision
    if (birdY + birdSizePx/2 > screenHeight - groundHeight) {
        return true
    }

    // Ceiling collision
    if (birdY - birdSizePx/2 < ceilingHeight) {
        return true
    }

    // Pipe collisions
    pipes.forEach { pipe ->
        val topPipe = Rect(
            x = pipe.x,
            y = ceilingHeight,
            width = pipeWidthPx,
            height = pipe.gapY - ceilingHeight
        )
        val bottomPipe = Rect(
            x = pipe.x,
            y = pipe.gapY + pipeGapPx,
            width = pipeWidthPx,
            height = screenHeight
        )
        if (birdBox.overlaps(topPipe) || birdBox.overlaps(bottomPipe)) {
            return true
        }
    }
    return false
}
fun updateUserPoints(score: Int, onComplete: (Int) -> Unit) {
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




// Data classes
data class Pipe(
    val x: Float,
    val gapY: Float,
    var passed: Boolean = false
)

data class Rect(
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float
) {
    fun overlaps(other: Rect): Boolean {
        return x < other.x + other.width &&
                x + width > other.x &&
                y < other.y + other.height &&
                y + height > other.y
    }
}