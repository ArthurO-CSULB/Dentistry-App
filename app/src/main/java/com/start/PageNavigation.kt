package com.start

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.start.pages.LoginPage
import com.start.pages.SignUpPage
import com.start.pages.HomePage
import com.start.pages.TimerPage
import com.start.pages.CalendarPage
import com.start.pages.GamesPage
import com.start.pages.GlossaryPage
import com.start.pages.ClinicSearchPage
import com.start.pages.ProfilePage
import com.start.viewmodels.AuthViewModel
import com.start.viewmodels.TimerViewModel
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.start.pages.AddEventPage
import com.start.pages.ClinicDetailsPage
import com.start.pages.EditEventPage
import com.start.pages.ErrorPage
import com.start.pages.SettingsPage
import com.start.pages.VerificationPage
import com.start.pages.WeeklyCalendarPage

/*
We define a PageNavigation using Jetpack Compose's Navigation component to manage the app's
navigation flow between different composable screens. We pass in a modifier for UI, as well as the
'authViewModel' for backend user authentication functionality.

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
 */

@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun PageNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, timerViewModel:
TimerViewModel) {
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
            // If the current build is at least API level 33...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Timer Page is implemented.
                TimerPage(modifier, navController, timerViewModel)
            }
            else {
                // Else the timer will display an error page.
                ErrorPage(modifier, navController)
            }
        }

        // Monthly Calendar screen.
        composable("calendar"){
            CalendarPage(modifier, navController)
        }

        // Weekly Calendar screen.
        composable("weeklyCalendar/{startDate}/{endDate}") { backStackEntry ->
            val startDate = backStackEntry.arguments?.getString("startDate") ?: ""
            val endDate = backStackEntry.arguments?.getString("endDate") ?: ""
            WeeklyCalendarPage(navController = navController, startDate = startDate, endDate = endDate)
        }

        // Add Event screen.
        composable("addEvent/{date}") { backStackEntry ->
            val date = backStackEntry.arguments?.getString("date") ?: ""
            AddEventPage(navController = navController, date = date)
        }

        // Edit Event screen.
        composable("editEvent/{eventId}") { backStackEntry ->
            val eventID = backStackEntry.arguments?.getString("eventID") ?: ""
            EditEventPage(navController = navController, eventID = eventID)
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
            ClinicSearchPage(modifier, navController = navController)
        }

        // Detail screen
        composable("clinicDetails/{placeId}") {backStackEntry ->
            ClinicDetailsPage(
                placeId = backStackEntry.arguments?.getString("placeId"),
                navController = navController)
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

        // Settings Page
        composable("settings"){
            SettingsPage(modifier, navController)
        }
    })
}