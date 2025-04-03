package com.start.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.start.repos.PointsProgressionRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException


/*
    Rules for Points and Prestiging/Leveling Up:

    1. Points will be added to the user's account through various features such as the timer,
    trivia, games, and more.
    2. Users will have a maximum number of points that can be earned for their current prestige level.
    For instance a level 0 prestige account can at once possess only a maximum of 1000 points, nothing more.
    3. Users can spend points as defined in use case 15 (User Profile Management) on customizable
    items/rewards/badges/emblems/etc, only if they have enough points.
    4. Users can only increase in prestige level if they currently possess the maximum number points
    for their current prestige level.
    5. The higher the prestige level, the higher the number of maximum points that can be earned.
    For instance, a level 0 prestige can only possess 1000 points maximum, whereas a level 1
    prestige can only possess 2500 points maximum.
    6. Prestiging will be harder after every prestige as a result of the increasing maximum points.
    7. There will be prestiges 0 - 11 where max points for that prestige will be as follows:
    - Prestige 0: 1000 points
    - Prestige 1: 2500 points
    - Prestige 2: 5000 points
    - Prestige 3: 10000 points
    - Prestige 4: 25000 points
    - Prestige 5: 50000 points
    - Prestige 6: 100000 points
    - Maximum Prestige: Users can have a maximum of 1000000 points, but they have a 10x multiplier
    applied to point earnings. They can buy anything with enough points.

 */

class PointsProgressionViewModel(private val pointsProgressionRepo: PointsProgressionRepo): ViewModel() {

    // TODO: When adding points, implement the functionality that limits the amount of points for each prestige


    // Pass in the number of questions that were answered correctly by the user. Each
    // question answered correctly, multiplied by 10 will be the number of points awarded.
    fun addTriviaPoints(numCorrect: Long) {
        // Add points to Firestore database asynchronously
        viewModelScope.launch {
            // Add points to the user's account. Convert 10 to a long.
            pointsProgressionRepo.addPoints(numCorrect * 10L)
        }
    }

    // Add 100 points to the user's account when they have successfully completed the timer.
    fun addTimerPoints() {
        // Add points to the Firestore database asynchronously
        viewModelScope.launch {
            pointsProgressionRepo.addPoints(100L)
        }
    }


    fun prestige() {
        viewModelScope.launch {
            pointsProgressionRepo.prestige()
        }
    }

    fun getPrestige(onResult: (Long) -> Unit) {
        pointsProgressionRepo.getPrestige { prestige ->
            onResult(prestige)
        }
    }

    // We create this view model with a factory so that it can pass in arguments to specific repos.
    // The trivia view model will pass in a repository with the trivia.
    class PointsViewModelFactory(private val pointsProgressionRepo: PointsProgressionRepo) : ViewModelProvider.Factory {
        // Override method to create viewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Check if the model class is the same as or a superclass of PointsViewModel.
            // If it is we create an instance of PointsViewModel.
            if (modelClass.isAssignableFrom((PointsProgressionViewModel::class.java))) {
                @Suppress("UNCHECKED_CAST")
                // Create and return an instance of the HygieneTriviaViewModel with the argument of the
                // repository
                return PointsProgressionViewModel(pointsProgressionRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

// TODO: Not confirmed of how its going to be structured.
// Prestige Objects that define types of ranks
sealed class Prestige {
    data object Prestige0: Prestige() {
        override fun toString(): String {
            return "Prestige 0 Name"
        }
    }
    data object Prestige1: Prestige() {
        override fun toString(): String {
            return "Prestige 1 Name"
        }
    }
    data object Prestige2: Prestige() {
        override fun toString(): String {
            return "Prestige 2 Name"
        }
    }
    data object Prestige3: Prestige() {
        override fun toString(): String {
            return "Prestige 3 Name"
        }
    }
    data object Prestige4: Prestige() {
        override fun toString(): String {
            return "Prestige 4 Name"
        }
    }
    data object Prestige5: Prestige() {
        override fun toString(): String {
            return "Prestige 5 Name"
        }
    }
    data object Prestige6: Prestige() {
        override fun toString(): String {
            return "Prestige 6 Name"
        }
    }
    data object MaxPrestige: Prestige() {
        override fun toString(): String {
            return "Max Prestige Name"
        }

    }
}