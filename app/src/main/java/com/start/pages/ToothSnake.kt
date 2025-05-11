package com.start.pages

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

//class SnakeGameActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            SnakeGame(navController = navController)
//        }
//    }
//}

enum class Direction { UP, DOWN, LEFT, RIGHT }

@Composable
fun ToothSnake(modifier: Modifier, navController: NavController) {
    val gridSize = 20
    val blockSize = 32.dp
    val initialSnake = listOf(Pair(10, 10))

    var snake by remember { mutableStateOf(initialSnake) }
    var direction by remember { mutableStateOf(Direction.RIGHT) }
    var food by remember { mutableStateOf(Pair(5, 5)) }
    var isGameOver by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (isActive) {
                withFrameNanos { }
                kotlinx.coroutines.delay(500)
                if (!isGameOver) {
                    moveSnake(
                        snake,
                        direction,
                        gridSize,
                        food,
                        onGameOver = { isGameOver = true },
                        onEatFood = { updatedSnake ->
                            snake = updatedSnake
                            // Ensure food is generated in a valid position
                            food = generateFood(gridSize, updatedSnake).also {
                                if (it.first == -1 && it.second == -1) {
                                    // Handle case where no valid food position exists
                                    isGameOver = true
                                }
                            }
                        },
                        onUpdate = { snake = it }
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size((gridSize * blockSize.value).dp)
                .background(Color(0xFF199746), shape = RoundedCornerShape(12.dp))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Draw food
                translate(food.first * blockSize.toPx(), food.second * blockSize.toPx()) {
                    drawRect(Color.Red, size = size.copy(width = blockSize.toPx(), height = blockSize.toPx()))
                }
            }

            snake.forEach { (x, y) ->
                Image(
                    painter = painterResource(id = R.drawable.tooth_snake_body),
                    contentDescription = "Snake Body",
                    modifier = Modifier
                        .offset((x * blockSize.value).dp, (y * blockSize.value).dp)
                        .size(blockSize)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        DirectionControls(
            onDirectionChange = { newDirection ->
                if (!isOpposite(direction, newDirection)) {
                    direction = newDirection
                }
            }
        )

//if (isGameOver) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                snake = initialSnake
                direction = Direction.RIGHT
                food = generateFood(gridSize, snake)
                isGameOver = false
            }) {
                Text("Restart")
            }
        //}
    }
}

fun moveSnake(
    snake: List<Pair<Int, Int>>,
    direction: Direction,
    gridSize: Int,
    food: Pair<Int, Int>,
    onGameOver: () -> Unit,
    onEatFood: (List<Pair<Int, Int>>) -> Unit,
    onUpdate: (List<Pair<Int, Int>>) -> Unit
) {
    val head = snake.first()
    val newHead = when (direction) {
        Direction.UP -> Pair(head.first, head.second - 1)
        Direction.DOWN -> Pair(head.first, head.second + 1)
        Direction.LEFT -> Pair(head.first - 1, head.second)
        Direction.RIGHT -> Pair(head.first + 1, head.second)
    }

    if (newHead.first !in 0 until gridSize || newHead.second !in 0 until gridSize || snake.contains(newHead)) {
        onGameOver()
        return
    }

    val newSnake = mutableListOf(newHead)
    newSnake.addAll(snake)

    if (newHead == food) { // Ate food
        onEatFood(newSnake)
    } else {
        newSnake.removeAt(newSnake.size - 1)
        onUpdate(newSnake)
    }
}

fun generateFood(gridSize: Int, snake: List<Pair<Int, Int>>): Pair<Int, Int> {
    val emptyCells = mutableListOf<Pair<Int, Int>>()

    // Find all empty cells
    for (x in 0 until gridSize) {
        for (y in 0 until gridSize) {
            if (!snake.contains(Pair(x, y))) {
                emptyCells.add(Pair(x, y))
            }
        }
    }

    return if (emptyCells.isNotEmpty()) {
        emptyCells.random() // Pick a random empty cell
    } else {
        Pair(-1, -1) // Return invalid position if no space left (game won)
    }
}

fun isOpposite(current: Direction, new: Direction): Boolean {
    return (current == Direction.UP && new == Direction.DOWN) ||
            (current == Direction.DOWN && new == Direction.UP) ||
            (current == Direction.LEFT && new == Direction.RIGHT) ||
            (current == Direction.RIGHT && new == Direction.LEFT)
}

@Composable
fun DirectionControls(onDirectionChange: (Direction) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(onClick = { onDirectionChange(Direction.UP) }) { Text("Up") }
        Row {
            Button(onClick = { onDirectionChange(Direction.LEFT) }) { Text("Left") }
            Spacer(modifier = Modifier.width(20.dp))
            Button(onClick = { onDirectionChange(Direction.RIGHT) }) { Text("Right") }
        }
        Button(onClick = { onDirectionChange(Direction.DOWN) }) { Text("Down") }
    }
}
