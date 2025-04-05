package com.start.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.start.repos.PointsPrestige
import com.start.repos.PointsProgressionRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
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
    7. There will be prestiges 0 - 7 where max points for that prestige will be as follows:
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

@OptIn(ExperimentalCoroutinesApi::class)
class PointsProgressionViewModel(private val pointsProgressionRepo: PointsProgressionRepo): ViewModel() {

    // TODO: When adding points, implement the functionality that limits the amount of points for each prestige

    // Store the experience and prestige of the user.
    private val _experience = MutableStateFlow(0L)
    val experience: StateFlow<Long> = _experience
    private val _prestige = MutableStateFlow(0L)
    val prestige: StateFlow<Long> = _prestige
    // A list of prestiges that will be used to access certain info on the prestige.
    // Prestige number will be used to index the prestiges list. Makes sure to convert the prestige
    // to an int before trying to access the prestige info. For instance, using the prestige flow
    // above, prestigeName: String = prestiges[prestige.value.toInt()]
    val prestiges: List<Prestige> = listOf(Prestige.Prestige0,
        Prestige.Prestige1,
        Prestige.Prestige2, Prestige.Prestige3,
        Prestige.Prestige4, Prestige.Prestige5, Prestige.Prestige6, Prestige.MaxPrestige
    )

    init {
        // We collect the logged in state from the repo by calling the function that emits
        // the logged in state. FlatMapLatest will be used to dynamically switch between
        // the flow that emits data from the user's account, and the flow that emits data
        // when the user is logged out.
        viewModelScope.launch {
            // Call loggedInFlow to emit the logged in state. Use flatMapLatest to dynamically
            // collect from the latest inner flow. It cancels current flow when 'isLoggedIn' changes
            // and builds new inner flow based off new value of 'isLoggedIn'. Method will also
            // assign the 'userAccount' variable in the repo with the reference to the 'account'
            // document of the user.
            pointsProgressionRepo.loggedInFlow().flatMapLatest { isLoggedIn ->
                // If the user is logged in, we can build the inner flow that will emit experience
                // and prestige data from the database.
                if (isLoggedIn) {
                    // Method to attaches a listener to the user's account and emit changes to
                    // points and prestige.
                    pointsProgressionRepo.userLevelFlow()
                }
                // If the user is not logged in, we can build the inner flow that will detach the
                // listener of the user who signed out and emit a dummy value of 0 points and 0
                // prestige for the UI to display.
                else {
                    flow {
                        // Detach listener from the user's account if no user is logged in.
                        pointsProgressionRepo.detachPointsPrestigeListener()
                        // Emit 0 points and 0 prestige if no user is logged in.
                        emit(PointsPrestige(0L, 0L))
                        // Detach the listener from the user's account if no user is logged in.
                        Log.d(TAG, "user not logged in anymore so detaching listener, emitting values of 0")
                    }

                }
                // Collect from the latest inner flow emitted by flatMapLatest.
            }.collect {pointsPrestige ->
                // Update the experience and prestige state that will be presented to the UI
                _experience.value = pointsPrestige.experience
                _prestige.value = pointsPrestige.prestige
            }
        }
    }


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

    // Method to increase the prestige value.
    fun prestige() {
        viewModelScope.launch {
            pointsProgressionRepo.prestige()
        }
    }

    // Method to reset the user's experience to zero.
    fun resetPoints() {
        viewModelScope.launch {
            pointsProgressionRepo.resetPoints()
        }
    }

    /*
    Not in user since we have a prestige attribute in the view model.
    fun getPrestige(onResult: (Long) -> Unit) {
        pointsProgressionRepo.getPrestige { prestige ->
            onResult(prestige)
        }
    }
     */

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

// Prestige Objects that define types of ranks
sealed class Prestige {
    abstract val maxExp: Long

    data object Prestige0: Prestige() {
        override val maxExp = 1000L
        override fun toString() = "Prestige 0 Name"
    }

    data object Prestige1: Prestige() {
        override val maxExp = 2500L
        override fun toString() = "Prestige 1 Name"
    }
    data object Prestige2: Prestige() {
        override val maxExp = 5000L
        override fun toString() = "Prestige 2 Name"
    }
    data object Prestige3: Prestige() {
        override val maxExp = 10000L
        override fun toString() = "Prestige 3 Name"
    }
    data object Prestige4: Prestige() {
        override val maxExp = 25000L
        override fun toString() = "Prestige 4 Name"
    }
    data object Prestige5: Prestige() {
        override val maxExp = 50000L
        override fun toString() = "Prestige 5 Name"
    }
    data object Prestige6: Prestige() {
        override val maxExp = 100000L
        override fun toString() = "Prestige 6 Name"
    }
    data object MaxPrestige: Prestige() {
        override val maxExp = 1000000L
        override fun toString() = "Max Prestige Name"
    }

}