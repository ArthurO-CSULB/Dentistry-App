package com.start

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.start.ui.theme.DentalHygieneTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import com.example.dentalhygiene.BuildConfig.MAPS_API_KEY
import com.google.android.libraries.places.api.Places

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

        //Initializes the Places API with the API key
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, MAPS_API_KEY)
        }
        // We declare and initialize the ViewModel 'AuthViewModel', delegating its initialization
        // and lifecycle management to Jetpack's viewModels function.
        val authViewModel : AuthViewModel by viewModels()
        // We set the content of our activity to the PageNavigation to begin page navigation flow.
        setContent {
            DentalHygieneTheme {
                // Scaffold for a structured layout that occupies the entire screen
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // The page navigation is the main content of the scaffold. Modifier assigned
                    // inner padding to ensure page navigation respects the reserved space, as well
                    // as passing in the ViewModels.
                    PageNavigation(modifier = Modifier.padding(innerPadding), authViewModel = authViewModel)
                }
            }
        }
    }
}