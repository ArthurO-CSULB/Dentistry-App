package com.start.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.dentalhygiene.R
import com.start.viewmodels.AuthState
import com.start.viewmodels.AuthViewModel
import com.start.viewmodels.HygieneTriviaState
import com.start.viewmodels.HygieneTriviaViewModel
import com.start.viewmodels.TimerState
import com.start.viewmodels.TimerViewModel
import androidx.compose.runtime.livedata.observeAsState

object ButtonSizes {
    // Regular buttons
    val REGULAR_CONTAINER = 120.dp
    val REGULAR_ICON = 72.dp // 60% of container
    val REGULAR_WIDTH = 150.dp
    val CORNER_RADIUS = 16.dp

    // Profile button
    val PROFILE_CONTAINER = 120.dp
    val PROFILE_ICON = 72.dp // 60% of container

    // Settings button
    val SETTINGS_CONTAINER = 32.dp
    val SETTINGS_ICON = 24.dp // 75% of container

    // Games button (special)
    val GAMES_WIDTH = 150.dp
    val GAMES_HEIGHT = 100.dp
}

@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    timerViewModel: TimerViewModel,
    hygieneTriviaViewModel: HygieneTriviaViewModel
) {
    val authState = authViewModel.authState.observeAsState()
    val timerState = timerViewModel.timerState.collectAsState()
    val hygieneTriviaState = hygieneTriviaViewModel.hygieneTriviaState.collectAsState()

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.UnAuthenticated -> navController.navigate("login")
            is AuthState.Unverified -> navController.navigate("verification")
            else -> Unit
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))
            Text("Prototype Home Page", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            // Profile (Centered Circle)
            ProfileButton(
                iconRes = R.drawable.ic_profile,
                onClick = { navController.navigate("profile") },
                containerSize = ButtonSizes.PROFILE_CONTAINER,
                iconSize = ButtonSizes.PROFILE_ICON,
                backgroundColor = Color(0xFFFFB6C1)
            )
            // Space
            Spacer(modifier=Modifier.height(16.dp))
            // Button to the timer page.
            Button(onClick={navController.navigate("productrecs")}) {
                Text(text = "Product Recommendations")
            }

            Spacer(Modifier.height(40.dp))

            // Feature Rows
            FeatureRows(
                navController = navController,
                timerState = timerState.value,
                hygieneTriviaState = hygieneTriviaState.value
            )
        }

        // Sign Out Button (Bottom Center)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = { authViewModel.signout() }
            ) {
                Text("Sign Out", fontSize = 20.sp)
            }
        }

        // Settings Button (Bottom Right)
        SettingsButton(
            iconRes = R.drawable.ic_settings,
            onClick = { navController.navigate("settings") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
private fun FeatureRows(
    navController: NavController,
    timerState: TimerState,
    hygieneTriviaState: HygieneTriviaState
) {
    // Timer and Calendar
    FeatureRow(
        features = listOf(
            FeatureItem(
                iconRes = R.drawable.ic_timer,
                label = "Toothbrush Timer",
                color = Color(0xFFFFA07A),
                containerSize = ButtonSizes.REGULAR_CONTAINER,
                iconSize = ButtonSizes.REGULAR_ICON,
                width = ButtonSizes.REGULAR_WIDTH,
                shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
            ) {
                when (timerState) {
                    is TimerState.Begin -> navController.navigate("timer_begin")
                    is TimerState.Counting -> navController.navigate("timer_counting")
                    is TimerState.Cancel -> navController.navigate("timer_cancel")
                    is TimerState.Finished -> navController.navigate("timer_finish")
                    else -> navController.navigate("timer_begin")
                }
            },
            FeatureItem(
                iconRes = R.drawable.ic_calendar,
                label = "Calendar",
                color = Color(0xFF87CEFA),
                containerSize = ButtonSizes.REGULAR_CONTAINER,
                iconSize = ButtonSizes.REGULAR_ICON,
                width = ButtonSizes.REGULAR_WIDTH,
                shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
            ) {
                navController.navigate("calendar")
            }
        ),
        navController = navController  // Moved inside the parentheses
    )

    Spacer(Modifier.height(24.dp))

    // Trivia and Glossary
    FeatureRow(
        features = listOf(
            FeatureItem(
                iconRes = R.drawable.ic_trivia,
                label = "Dental Trivia",
                color = Color(0xFF98FB98),
                containerSize = ButtonSizes.REGULAR_CONTAINER,
                iconSize = ButtonSizes.REGULAR_ICON,
                width = ButtonSizes.REGULAR_WIDTH,
                shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
            ) {
                when (hygieneTriviaState) {
                    is HygieneTriviaState.Begin -> navController.navigate("trivia_begin")
                    is HygieneTriviaState.Trivia -> navController.navigate("trivia_trivia")
                    is HygieneTriviaState.Finished -> navController.navigate("trivia_finish")
                    else -> navController.navigate("trivia_begin")
                }
            },
            FeatureItem(
                iconRes = R.drawable.ic_glossary,
                label = "Glossary",
                color = Color(0xFFFFFF99),
                containerSize = ButtonSizes.REGULAR_CONTAINER,
                iconSize = ButtonSizes.REGULAR_ICON,
                width = ButtonSizes.REGULAR_WIDTH,
                shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
            ) {
                navController.navigate("glossary")
            }
        ),
        navController = navController  // Moved inside the parentheses
    )

    Spacer(Modifier.height(24.dp))

    // Search and Games
    FeatureRow(
        features = listOf(
            FeatureItem(
                iconRes = R.drawable.ic_search,
                label = "Clinic Search",
                color = Color(0xFFB2DFDB),
                containerSize = ButtonSizes.REGULAR_CONTAINER,
                iconSize = ButtonSizes.REGULAR_ICON,
                width = ButtonSizes.REGULAR_WIDTH,
                shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
            ) {
                navController.navigate("search")
            },
            FeatureItem(
                iconRes = R.drawable.ic_games,
                label = "Games",
                color = Color(0xFFD8BFD8),
                containerSize = ButtonSizes.REGULAR_CONTAINER,
                iconSize = ButtonSizes.REGULAR_ICON,
                width = ButtonSizes.REGULAR_WIDTH,
                shape = RoundedCornerShape(ButtonSizes.CORNER_RADIUS)
            ) {
                navController.navigate("games")
            }
        ),
        navController = navController  // Moved inside the parentheses
    )
}

@Composable
private fun ProfileButton(
    iconRes: Int,
    onClick: () -> Unit,
    containerSize: Dp,
    iconSize: Dp,
    backgroundColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            shape = CircleShape,
            color = backgroundColor,
            modifier = Modifier.size(containerSize),
            shadowElevation = 8.dp
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = "Profile",
                    modifier = Modifier.size(iconSize)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Profile", fontSize = 18.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun FeatureRow(
    features: List<FeatureItem>,
    navController: NavController  // Add this parameter
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        features.forEach { feature ->
            IconButtonWithLabel(
                iconRes = feature.iconRes,
                label = feature.label,
                backgroundColor = feature.color,
                containerSize = feature.containerSize,
                iconSize = feature.iconSize,
                width = feature.width,
                shape = feature.shape,
                onClick = feature.onClick
            )
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("userRatings") }) {
            Text("User Ratings")
        }
    }
}

@Composable
private fun IconButtonWithLabel(
    iconRes: Int,
    label: String,
    backgroundColor: Color,
    containerSize: Dp,
    iconSize: Dp,
    width: Dp,
    shape: Shape,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(width)
    ) {
        Surface(
            shape = shape,
            color = backgroundColor,
            modifier = Modifier.size(containerSize),
            shadowElevation = 4.dp
        ) {
            IconButton(
                onClick = onClick,
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SettingsButton(
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier.size(ButtonSizes.SETTINGS_CONTAINER),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = Color(0xFFE6E6FA),
            contentColor = Color.Black
        )
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = "Settings",
            modifier = Modifier.size(ButtonSizes.SETTINGS_ICON)
        )
    }
}

data class FeatureItem(
    val iconRes: Int,
    val label: String,
    val color: Color,
    val containerSize: Dp,
    val iconSize: Dp,
    val width: Dp,
    val shape: Shape,
    val onClick: () -> Unit
)