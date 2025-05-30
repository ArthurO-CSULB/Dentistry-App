package com.start.viewmodels

import android.content.ContentValues.TAG
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dentalhygiene.R
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

    // Store the experience and prestige of the user.
    private val _experience = MutableStateFlow(0L)
    val experience: StateFlow<Long> = _experience
    private val _prestige = MutableStateFlow(0L)
    val prestige: StateFlow<Long> = _prestige

    // Stores the list of all emblems owned by the user
    private val _emblemList = mutableStateOf<List<Emblem>>(emptyList())
    val emblemList : MutableState<List<Emblem>> = _emblemList

    // Stores the user's currently equipped emblem
    var equippedEmblem = mutableStateOf<String?>("")

    // A list of prestiges that will be used to access certain info on the prestige.
    // Prestige number will be used to index the prestiges list. Makes sure to convert the prestige
    // to an int before trying to access the prestige info. For instance, using the prestige flow
    // above, prestigeName: String = prestiges[prestige.value.toInt()]
    val prestiges: List<Prestige> = listOf(
        Prestige.Prestige0,
        Prestige.Prestige1,
        Prestige.Prestige2,
        Prestige.Prestige3,
        Prestige.Prestige4,
        Prestige.Prestige5,
        Prestige.Prestige6,
        Prestige.MaxPrestige
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
        // Initialize the points to be added.
        val points = numCorrect * 10L
        // If the number of points that will be added to the user's account make them exceed
        // their point limit, set their points to the point limit for their current prestige.
        if (_experience.value.toInt() + points.toInt() >= prestiges[_prestige.value.toInt()].maxExp) {
            // Set points to the Firestore database asynchronously
            viewModelScope.launch {
                pointsProgressionRepo.setPoints(prestiges[prestige.value.toInt()].maxExp)
            }
        }
        // Else, we just add the the standard amount of points.
        else {
            // Add points to the Firestore database asynchronously
            viewModelScope.launch {
                // Add points to the user's account. Convert 10 to a long.
                pointsProgressionRepo.addPoints(numCorrect * 10L)
            }
        }
    }

    // Add 100 points to the user's account when they have successfully completed the timer.
    fun addTimerPoints() {
        val points = 100L
        // If the number of points that will be added to the user's account make them exceed
        // their point limit, set their points to the point limit for their current prestige.
        if (_experience.value.toInt() + points.toInt() >= prestiges[_prestige.value.toInt()].maxExp) {
            // Set points to the Firestore database asynchronously
            viewModelScope.launch {
                pointsProgressionRepo.setPoints(prestiges[prestige.value.toInt()].maxExp)
            }
        }
        // Else, we just add the the standard amount of points.
        else {
            // Add points to the Firestore database asynchronously
            viewModelScope.launch {
                pointsProgressionRepo.addPoints(100L)
            }
        }

    }

    // Add the score of flappy tooth to the user's points.
    fun addFlappyToothPoints(score: Int) {
        viewModelScope.launch {
            pointsProgressionRepo.addPoints(score.toLong())
        }
    }

    // Set the points of the user.
    fun setPoints(points: Long) {
        viewModelScope.launch {
            pointsProgressionRepo.setPoints(points)
        }
    }

    // Method to spend points when buying an emblem from the shop
    fun buyEmblem(emblem: Emblem) {
        // If the user has enough points to buy the emblem
        if (_experience.value >= emblem.price) {
            viewModelScope.launch {
                pointsProgressionRepo.emblemOwned(emblem.name){ owned ->
                    // If the user doesn't already own the emblem,
                    // adds the emblem to the list of owned emblems
                    // and subtracts the emblem points from the user's experience
                    if (!owned)
                    {
                        pointsProgressionRepo.buyEmblem(emblem.name)
                        pointsProgressionRepo.subtractPoints(emblem.price)
                    }
                }
            }
        }
    }

    // Function that shows  whether the inputted emblem is owned by the user or not
    fun isOwned(emblemName: String, onResult: (Boolean) -> Unit)
    {
        pointsProgressionRepo.emblemOwned(emblemName){ owned ->
            if (owned)
            {
                onResult(true)
            }
            else
            {
                onResult(false)
            }
        }
    }

    // Method to increase the prestige value.
    fun prestige() {
        // If prestige is already at max prestige, user can't prestige.
        if (_prestige.value == prestiges.last().maxExp) {
            return
        }
        // Add points to the user's account.
        viewModelScope.launch {
            pointsProgressionRepo.prestige()
            // TODO: pointsProgressionRepo.rewardBadge()
        }
    }

    // Method to reset the user's experience to zero.
    fun resetPoints() {
        viewModelScope.launch {
            pointsProgressionRepo.resetPoints()
        }
    }

    //Method to load all emblems according to the user's prestige
    fun getEmblems(prestigeLevel: Long){
        pointsProgressionRepo.getEmblemsForPrestige(prestigeLevel) { emblems ->
            _emblemList.value = emblems
        }
        Log.d("Firebase", "Getting Emblems")
    }

    // Function to load the user's equipped emblem when the page loads
    fun loadEquipped(){
        pointsProgressionRepo.loadEquippedEmblem { equippedEmblem.value = it }
    }

    // Function to equip the given emblem and add it to the equipped emblem state
    fun equipEmblem(emblem: Emblem){
        pointsProgressionRepo.equipEmblem(emblem.imageUrl)
        equippedEmblem.value = emblem.imageUrl
    }

    // Function to equip the given emblem and add it to the equipped emblem state
    fun unequipEmblem(){
        pointsProgressionRepo.equipEmblem("")
        equippedEmblem.value = ""
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

// Prestige Objects that define types of tiers, each tier is color coded and has its own badge
sealed class Prestige {
    abstract val maxExp: Long
    abstract val prestigeLevel: Long
    abstract val badge: String
    abstract val color: Color

    data object Prestige0: Prestige() {
        override val maxExp = 1000L
        override val prestigeLevel = 0L
        override fun toString() = "White Tier"
        override val badge = "The Root"
        override val color = Color(0xFFFAF9E8)
    }
    data object Prestige1: Prestige() {
        override val maxExp = 2500L
        override val prestigeLevel = 1L
        override fun toString() = "Green Tier"
        override val badge = "Cavity Avoider"
        override val color = Color(0xFF24E966)
    }
    data object Prestige2: Prestige() {
        override val maxExp = 5000L
        override val prestigeLevel = 2L
        override fun toString() = "Yellow Tier"
        override val badge = "Tooth Brusher"
        override val color = Color(0xFFFFEE06)

    }
    data object Prestige3: Prestige() {
        override val maxExp = 10000L
        override val prestigeLevel = 3L
        override fun toString() = "Orange Tier"
        override val badge = "Calculus Destroyer"
        override val color = Color(0xFFFD8C0A)
    }
    data object Prestige4: Prestige() {
        override val maxExp = 25000L
        override val prestigeLevel = 4L
        override fun toString() = "Red Tier"
        override val badge = "Deal Sealer"
        override val color = Color(0xFFFA335B)
    }
    data object Prestige5: Prestige() {
        override val maxExp = 50000L
        override val prestigeLevel = 5L
        override fun toString() = "Blue Tier"
        override val badge = "Floss Veteran"
        override val color = Color(0xFF398FFF)
    }
    data object Prestige6: Prestige() {
        override val maxExp = 100000L
        override val prestigeLevel = 6L
        override fun toString() = "Purple Tier"
        override val badge = "Crown Bearer"
        override val color = Color(0xFFAB62FF)
    }
    data object MaxPrestige: Prestige() {
        override val maxExp = 1000000L
        override val prestigeLevel = 7L
        override fun toString() = "Gold Tier"
        override val badge = "The Apex"
        override val color = Color(0xFFFFCE06)
    }

}

//Emblem object that defines what prestige it unlocks at, price, name, and image URL
data class Emblem (
    val imageUrl: String = "",
    val name: String = "",
    var prestige: Long = 0L,
    val price: Long = 0L,
    )

//Badge objects that define what prestige they unlock at, will just be a tagline string
data class Badge (
    val prestige: Long,
    val badge: String
)


// Previously used to store emblems locally, opted for Firebase instead
//Repository to store all emblems for each prestige
//object EmblemRepo {
//    private val emblems: Map<Int, List<Emblem>> = mapOf(
//        0 to listOf(
//            Emblem(0L, "Shrek", 100L, R.drawable.shrek_emblem),
//            Emblem(0L, "Toothpick", 500L, R.drawable.toothpick_emblem)
//        )
//    )
//    fun getEmblemsForCurrentPrestige(prestige: Int): List<Emblem> {
//        return emblems[prestige] ?: emptyList()
//    }
//}