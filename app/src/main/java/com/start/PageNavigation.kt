package com.start

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.start.pages.VerificationPage
import com.start.pages.LoginPage
import com.start.pages.SignUpPage
import com.start.pages.HomePage
import com.start.pages.TimerPage
import com.start.pages.CalendarPage
import com.start.pages.GamesPage
import com.start.pages.GlossaryPage
import com.start.pages.ClinicSearchPage
import com.start.pages.ProfilePage
import com.start.pages.VerificationPage

/*
We define a PageNavigation using Jetpack Compose's Navigation component to manage the app's
navigation flow between different composable screens. We pass in a modifier for UI, as well as the
'authViewModel' for backend user authentication functionality.

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
 */
@Composable
fun PageNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel) {
    // We create a navController to track the current screen and provide methods to navigate
    // between screens. We use rememberNavController to ensure that the NavController instance
    // is consistent throughout the lifecycle of NavHost. This prevents a NavController being
    // created every time.
    val navController = rememberNavController()

    // We create a NavHost which defines the navigation graph and maps screens to their routes.
    // We initialize the start destination to the login screen. We then define the various
    // navigation destinations in our app.
    NavHost(navController = navController, startDestination = "login", builder = {
        // We have our various composable screens with their specific identifier, such as "login",
        // signup", etc.

        // Log in screen.
        composable("login"){
            LoginPage(modifier, navController, authViewModel)
        }
        // Sign up screen.
        composable("signup")
        {
            SignUpPage(modifier, navController, authViewModel)
        }
        // Home screen.
        composable("home"){
            HomePage(modifier, navController, authViewModel)
        }

        // Timer screen.
        composable("timer"){
            TimerPage(modifier, navController)
        }

        // Calendar screen.
        composable("calendar"){
            CalendarPage(modifier, navController)
        }

        // Games screen.
        composable("games"){
            GamesPage(modifier, navController)
        }

        // Glossary screen.
        composable("glossary"){
            GlossaryPage(modifier, navController)
        }

        // Search screen.
        composable("search"){
            ClinicSearchPage(modifier, navController)
        }

        // Profile screen.
        composable("profile"){
            ProfilePage(modifier, navController)
        }

        // Verification Screen
        // Cannot be accessed when user is already verified
        composable("verification"){
            VerificationPage(modifier, navController, authViewModel)
        }
    })
}