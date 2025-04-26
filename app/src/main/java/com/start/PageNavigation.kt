package com.start

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.start.pages.AddEventPage
import com.start.pages.BookmarkPage
import com.start.pages.CalendarPage
import com.start.pages.ChangePasswordPage
import com.start.pages.ChangeUserDetailsPage
import com.start.pages.ClinicDetailsPage
import com.start.pages.ClinicRatingsPage
import com.start.pages.ClinicSearchPage
import com.start.pages.CreateRatingPage
import com.start.pages.EditEventPage
import com.start.pages.ErrorPage
import com.start.pages.GamesPage
import com.start.pages.GlossaryPage
import com.start.pages.HomePage
import com.start.pages.LoginPage
import com.start.pages.ReauthenticationPage
import com.start.pages.SettingsPage
import com.start.pages.SignUpPage
import com.start.pages.ToothbrushReplacementPage
import com.start.pages.UserRatingsPage
import com.start.pages.VerificationPage
import com.start.pages.WeeklyCalendarPage
import com.start.pages.hygiene_trivia_pages.HygieneTriviaPageBegin
import com.start.pages.hygiene_trivia_pages.HygieneTriviaPageFailed
import com.start.pages.hygiene_trivia_pages.HygieneTriviaPageFinished
import com.start.pages.hygiene_trivia_pages.HygieneTriviaPagePoints
import com.start.pages.hygiene_trivia_pages.HygieneTriviaPageTrivia
import com.start.pages.productrecs.FlossRecs
import com.start.pages.productrecs.MouthwashRecs
import com.start.pages.productrecs.ProductRecommendations
import com.start.pages.productrecs.ToothbrushRecs
import com.start.pages.productrecs.ToothpasteRecs
import com.start.pages.profile_pages.PointsProgressionPage
import com.start.pages.profile_pages.ProfilePage
import com.start.pages.test_pages.PointsTestPage
import com.start.pages.timer_pages.TimerPage
import com.start.pages.timer_pages.TimerPageBegin
import com.start.pages.timer_pages.TimerPageCancel
import com.start.pages.timer_pages.TimerPageCounting
import com.start.pages.timer_pages.TimerPageCountingModel
import com.start.pages.timer_pages.TimerPageFinish
import com.start.viewmodels.AuthViewModel
import com.start.viewmodels.BookmarksViewModel
import com.start.viewmodels.ClinicDetailsViewModel
import com.start.viewmodels.HygieneTriviaViewModel
import com.start.viewmodels.PointsProgressionViewModel
import com.start.viewmodels.RatingViewModel
import com.start.viewmodels.TimerViewModel
import com.start.viewmodels.ToothbrushTrackerViewModel

/*
We define a PageNavigation using Jetpack Compose's Navigation component to manage the app's
navigation flow between different composable screens. We pass in a modifier for UI, as well as the
'authViewModel' for backend user authentication functionality.

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
 */

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun PageNavigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel, timerViewModel:
TimerViewModel, hygieneTriviaViewModel: HygieneTriviaViewModel, pointsProgressionViewModel: PointsProgressionViewModel,
                   clinicDetailsViewModel: ClinicDetailsViewModel, ratingViewModel: RatingViewModel,
                   toothbrushTrackerViewModel: ToothbrushTrackerViewModel
) {
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
            HomePage(modifier, navController, authViewModel, timerViewModel, hygieneTriviaViewModel)
        }

        // Timer screen. Not in use
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

        composable("timer_begin") {
            TimerPageBegin(modifier, navController, timerViewModel)
        }

        composable(
            route = "timer_counting",
        ) {
            TimerPageCounting(modifier, navController, timerViewModel)
        }

        composable(
            route = "timer_counting_model",
            enterTransition = {
                fadeIn(animationSpec=tween(1500, 750))
            }
        ) {
            TimerPageCountingModel(modifier, navController, timerViewModel)
        }

        composable("timer_cancel") {
            TimerPageCancel(modifier, navController, timerViewModel)
        }

        composable("timer_finish") {
            TimerPageFinish(modifier, navController, timerViewModel, pointsProgressionViewModel)
        }

        composable("trivia_begin") {
            HygieneTriviaPageBegin(modifier, navController, hygieneTriviaViewModel)
        }

        composable("trivia_trivia") {
            HygieneTriviaPageTrivia(modifier, navController, hygieneTriviaViewModel)
        }

        composable(
            route ="trivia_finish",
            enterTransition = {
                fadeIn(animationSpec=tween(800, 750))
            }
        ) {
            HygieneTriviaPageFinished(modifier, navController, hygieneTriviaViewModel)
        }

        composable("trivia_points") {
            HygieneTriviaPagePoints(modifier, navController, hygieneTriviaViewModel, pointsProgressionViewModel)
        }

        composable(
            route = "trivia_fail",
            enterTransition = {
                fadeIn(animationSpec=tween(800, 750))
            }
        ) {
            HygieneTriviaPageFailed(modifier, navController, hygieneTriviaViewModel)
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
        composable("editEvent/{eventId}/{date}") { backStackEntry ->
            val eventID = backStackEntry.arguments?.getString("eventId") ?: ""
            val date = backStackEntry.arguments?.getString("date") ?: ""
            EditEventPage(navController = navController, date = date, eventID = eventID)
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

        // Detail screen
        composable("clinicDetails/{placeId}") {backStackEntry ->
            val bookmarksViewModel: BookmarksViewModel = viewModel()
            ClinicDetailsPage(
                placeId = backStackEntry.arguments?.getString("placeId"),
                navController = navController,
                clinicDetailsViewModel = clinicDetailsViewModel,
                ratingViewModel = ratingViewModel,
                bookmarksViewModel = bookmarksViewModel)
        }

        // clinic ratings page navigation
        composable("clinicRatings/{placeID},{clinicName}") { backStackEntry ->
            ClinicRatingsPage(
                navController = navController,
                ratingViewModel = ratingViewModel,
                clinicID = backStackEntry.arguments?.getString("placeID").toString(),
                clinicName = backStackEntry.arguments?.getString("clinicName").toString()
            )
        }

        // rating creation page navigation
        composable("createRating/{placeID},{clinicName}") { backStackEntry ->
            CreateRatingPage(
                navController = navController,
                ratingViewModel = ratingViewModel,
                clinicID = backStackEntry.arguments?.getString("placeID").toString(),
                clinicName = backStackEntry.arguments?.getString("clinicName").toString()
            )
        }

        // Profile screen.
        composable("profile"){
            ProfilePage(modifier, navController)
        }

        // Bookmark screen.
        composable("bookmark"){
            BookmarkPage(modifier, navController)
        }

        composable("points_progression") {
            PointsProgressionPage(modifier, navController, pointsProgressionViewModel)
        }

        // Product recommendations screen
        composable("product_recs") {
            ProductRecommendations(modifier, navController)
        }

        // Individual product recommendation screen
        composable("toothbrushes") {
            ToothbrushRecs(modifier, navController)
        }

        composable("toothpastes") {
            ToothpasteRecs(modifier, navController)
        }

        composable("floss") {
            FlossRecs(modifier, navController)
        }

        composable("mouthwash") {
            MouthwashRecs(modifier, navController)
        }

        // Verification Screen
        // Cannot be accessed when user is already verified
        composable("verification"){
            VerificationPage(modifier, navController, authViewModel)
        }

        // Settings Page
        composable("settings"){
            SettingsPage(modifier, navController, authViewModel)
        }

        // Change Password Page
        composable("changePassword"){
            ChangePasswordPage(modifier, navController, authViewModel)
        }

        // Reauthentication Page Route for Password Change
        composable("reauthenticationPasswordChange"){
            ReauthenticationPage(modifier, navController, authViewModel, "changePassword")
        }

        // Reauthentication Page Route for Account Deletion
        composable("reauthenticationAccountDeletion"){
            ReauthenticationPage(modifier, navController, authViewModel, "settings")
        }

        // Change User Details Page
        composable("changeUserDetails"){
            ChangeUserDetailsPage(modifier, navController, authViewModel)
        }

        composable("points_test") {
            PointsTestPage(modifier, navController, pointsProgressionViewModel)
        }

        // User Ratings Page
        composable("userRatings") {
            UserRatingsPage(navController, ratingViewModel)
        }

        composable("toothbrushTracker") {
            ToothbrushReplacementPage(navController, toothbrushTrackerViewModel)
        }
    })
}