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

@Composable
fun GamesPage(modifier: Modifier = Modifier, navController: NavController) {
    // Game state
    var birdY by remember { mutableFloatStateOf(0f) }
    var birdVelocity by remember { mutableFloatStateOf(0f) }
    var isGameRunning by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var pipes by remember { mutableStateOf(listOf<Pipe>()) }

    // Game constants
    val gravity = 1.5f
    val flapStrength = -20f
    val birdSize = 30.dp
    val pipeWidth = 100.dp
    val pipeGap = 200.dp
    val pipeSpeed = 5f

    // Convert Dp to pixels
    val density = LocalDensity.current
    val birdSizePx = with(density) { birdSize.toPx() }
    val pipeWidthPx = with(density) { pipeWidth.toPx() }
    val pipeGapPx = with(density) { pipeGap.toPx() }

    // Game loop
    LaunchedEffect(isGameRunning) {
        while (isGameRunning) {
            // Update bird position
            birdVelocity += gravity
            birdY += birdVelocity

            // Update pipes
            pipes = pipes.map { pipe ->
                pipe.copy(x = pipe.x - pipeSpeed)
            }

            // Add new pipes
            if (pipes.isEmpty() || pipes.last().x < 0) {
                pipes = pipes + Pipe(
                    x = 1000f,
                    gapY = Random.nextFloat() * 400 + 200
                )
            }

            // Check for collisions
            val birdRect = Rect(500f, birdY, birdSizePx, birdSizePx)
            for (pipe in pipes) {
                val topPipeRect = Rect(pipe.x, 0f, pipeWidthPx, pipe.gapY)
                val bottomPipeRect = Rect(pipe.x, pipe.gapY + pipeGapPx, pipeWidthPx, 1000f)
                if (birdRect.overlaps(topPipeRect) || birdRect.overlaps(bottomPipeRect)) {
                    isGameRunning = false
                }
                if (pipe.x + pipeWidthPx < 500f && !pipe.passed) {
                    score++
                    pipe.passed = true
                }
            }

            // Check if bird is out of bounds
            if (birdY > 1000 || birdY < 0) {
                isGameRunning = false
            }

            delay(16) // ~60 FPS
        }
    }

    // Column for the UI
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(Unit) {
                detectTapGestures {
                    if (!isGameRunning) {
                        // Reset game state
                        birdY = 500f
                        birdVelocity = 0f
                        pipes = emptyList()
                        score = 0
                        isGameRunning = true
                    }
                    birdVelocity = flapStrength // Flap on tap
                }
            },
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Flappy Tooth", fontSize = 32.sp, color = Color.White
        )
        Text(
            text = "Score: $score", fontSize = 24.sp, color = Color.White
        )
        TextButton(onClick = {
            navController.navigate("home")
        }) {
            Text(text = "Home", color = Color.White)
        }

        // Game Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Draw the bird
            drawCircle(
                color = Color.White,
                center = Offset(size.width / 2, birdY),
                radius = birdSizePx / 2
            )

            // Draw pipes
            for (pipe in pipes) {
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(pipe.x, 0f),
                    size = androidx.compose.ui.geometry.Size(pipeWidthPx, pipe.gapY)
                )
                drawRect(
                    color = Color.Green,
                    topLeft = Offset(pipe.x, pipe.gapY + pipeGapPx),
                    size = androidx.compose.ui.geometry.Size(pipeWidthPx, size.height)
                )
            }
        }
    }
}

// Data class for pipes
data class Pipe(
    val x: Float,
    val gapY: Float,
    var passed: Boolean = false
)

// Rect class for collision detection
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