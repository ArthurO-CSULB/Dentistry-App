package com.start

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable the app content to extend fully to the edges of the screen.
        enableEdgeToEdge()

        // Enable to see errors in the logs when trying to load the GLB model.
        WebView.setWebContentsDebuggingEnabled(true)

        // Gabriel Villanueva
        // We create our repositories, passing in the Context for app resources.
        val funFactsRepo = TimerFunFactsRepo(applicationContext)

        // We declare and initialize the view-models, delegating their initialization
        // and lifecycle management to Jetpack's viewModels function.
        val authViewModel : AuthViewModel by viewModels()
        // Gabriel Villanueva
        val timerViewModel: TimerViewModel by viewModels() {
            // Use factory to create view model to pass in the fun facts repository
            TimerViewModel.TimerViewModelFactory(funFactsRepo)
        }
        // We set the content of our activity to the PageNavigation to begin page navigation flow.
        setContent {
            DentalHygieneTheme {
                // Scaffold for a structured layout that occupies the entire screen
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // The page navigation is the main content of the scaffold. Modifier assigned
                    // inner padding to ensure page navigation respects the reserved space, as well
                    // as passing in the ViewModels.
                    PageNavigation(modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel, timerViewModel = timerViewModel)
                }
            }
        }
    }
}