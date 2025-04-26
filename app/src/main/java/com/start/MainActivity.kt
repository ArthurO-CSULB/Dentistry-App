package com.start

import android.os.Build
import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.start.ui.theme.DentalHygieneTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.MutableCreationExtras
import com.start.repos.TimerFunFactsRepo
//import com.start.repos.TimerFunFactsRepo
import com.start.viewmodels.AuthViewModel
import com.start.viewmodels.TimerViewModel
import com.start.viewmodels.ClinicDetailsViewModel
import com.example.dentalhygiene.BuildConfig.MAPS_API_KEY
import com.google.android.libraries.places.api.Places
import com.start.repos.HygieneTriviaRepo
import com.start.viewmodels.HygieneTriviaViewModel
import com.start.viewmodels.RatingViewModel
import com.start.viewmodels.ToothbrushTrackerViewModel

/*
We have our main and sole activity where the app will navigate through various composable screens
and manage shared data. Managing shared data is easier when you have a single activity and
central ViewModels scoped to that activity that will handle the data and UI-related states.
Our pages/views will display the UI and react to changes in the state provided by the ViewModel.
In addition, it allows better resource usage, avoids redundancy, and brings consistent navigation.
We essentially follow MVVM architecture (Model-View-ViewModel).

Author Referenced: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
 */
class MainActivity : ComponentActivity(){
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable the app content to extend fully to the edges of the screen.
        enableEdgeToEdge()

        //Initializes the Places API with the API key
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, MAPS_API_KEY)
        }
        // We declare and initialize the ViewModel 'AuthViewModel', delegating its initialization

        // Enable to see errors in the logs when trying to load the GLB model.
        WebView.setWebContentsDebuggingEnabled(true)

        // Gabriel Villanueva
        // We create our repositories, passing in the Context for app resources.
        val funFactsRepo = TimerFunFactsRepo(applicationContext)
        val hygieneTriviaRepo = HygieneTriviaRepo(applicationContext)

        // We declare and initialize the view-models, delegating their initialization
        // and lifecycle management to Jetpack's viewModels function.
        val authViewModel : AuthViewModel by viewModels()
        // Gabriel Villanueva
        val timerViewModel: TimerViewModel by viewModels() {
            // Use factory to create view model to pass in the fun facts repository
            TimerViewModel.TimerViewModelFactory(funFactsRepo)
        }
        val clinicDetailsViewModel : ClinicDetailsViewModel by viewModels()
        val ratingViewModel : RatingViewModel by viewModels()
        val hygieneTriviaViewModel: HygieneTriviaViewModel by viewModels() {
            HygieneTriviaViewModel.HygieneTriviaViewModelFactory(hygieneTriviaRepo)
        }

        // Create a toothbrushtrackerviewmodel for app
        val toothbrushTrackerViewModel: ToothbrushTrackerViewModel by viewModels()

        // We set the content of our activity to the PageNavigation to begin page navigation flow.
        setContent {
            DentalHygieneTheme {
                // Scaffold for a structured layout that occupies the entire screen
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // The page navigation is the main content of the scaffold. Modifier assigned
                    // inner padding to ensure page navigation respects the reserved space, as well
                    // as passing in the ViewModels.
                    PageNavigation(modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel, timerViewModel = timerViewModel, hygieneTriviaViewModel = hygieneTriviaViewModel,
                        clinicDetailsViewModel = clinicDetailsViewModel, ratingViewModel = ratingViewModel, toothbrushTrackerViewModel = toothbrushTrackerViewModel)
                }
            }
        }
    }
}