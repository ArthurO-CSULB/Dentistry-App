package com.start.viewmodels

import android.content.Context
import android.os.CountDownTimer
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dentalhygiene.R
import com.start.repos.TimerFunFactsRepo
import kotlinx.coroutines.Dispatchers
// import com.start.repos.TimerFunFactsRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import kotlin.collections.ArrayList
import kotlin.coroutines.coroutineContext
import java.util.Timer

/*
We define a TimerViewModel, which is a ViewModel class to manage the state of the Toothbrush Timer
feature.

Author Referenced for overall structure of code: EasyTuto
URL: https://www.youtube.com/watch?v=KOnLpNZ4AFc&t=778s
*/

// The number of milliseconds in two minutes, 10 seconds, and one second, and zero seconds
// for the toothbrush timer.
const val TWO_MINUTES_MILLI : Long = 120000
const val TEN_SECONDS_MILLI : Long = 10000
const val ONE_SECOND_MILLI : Long = 1000
const val ZERO : Long = 0
class TimerViewModel(private val timerFunFactsRepo: TimerFunFactsRepo) : ViewModel() {

    // Author Referenced for state flow: Philipp Lackner
    // URL: https://www.youtube.com/watch?v=6Jc6-INantQ
    // Others: https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/

    // We declare a data holder called "_timerState". This will store data that is mutable from
    // within this ViewModel class. The data that is stored is the timer state of the app,
    // whether the user has started, paused, canceled, etc... the timer.
    private val _timerState = MutableStateFlow<TimerState>(TimerState.Begin)

    // We declare a public value 'timerState' which will be observed by the pages related to the
    // toothbrush timer. When timerState changes state to a particular state the page will navigate
    // to its next specific page assigned in its method.
    val timerState: StateFlow<TimerState> = _timerState

    // We declare a private mutable live data object which will be the countdown timer object for
    // our toothbrush timer. Initialize with two minutes
    private val _toothBrushTimer = MutableStateFlow<Long>(TWO_MINUTES_MILLI)

    // We declare a public state flow int that the UI will observe.
    val toothBrushTimer: StateFlow<Long> = _toothBrushTimer

    // Store the fun facts in a state flow for the UI to read.
    private val _timerFact = MutableStateFlow<StringBuilder>(StringBuilder(timerFunFactsRepo.randomFact()))
    val timerFact = _timerFact

    // Store whether or not the timer model has been toggled.
    private val _timerModelEnabled = MutableStateFlow<Boolean>(false)
    val timerModelEnabled = _timerModelEnabled

    // Store the state of the tooth model.
    private val _timerModelState = MutableStateFlow<TimerModelState>(TimerModelState.Upper)
    val timerModelState = _timerModelState

    // The coroutine that will run the timer concurrently with the main thread.
    private var timerRun: kotlinx.coroutines.Job? = null

    // Method to start the toothbrush timer.
    // Author Referenced/URL for coroutines: https://kotlinlang.org/docs/coroutines-guide.html
    fun startTimer() {
        // If the timer was just un-paused, set the state to resumed.
        if (_timerState.value == TimerState.Pause) _timerState.value = TimerState.Resumed
        // Else it is counting.
        else _timerState.value = TimerState.Counting
        // Asynchronously run the counting down of the timer to avoid blocking the main thread.
        // runs coroutines on a background thread pool via Dispatchers.Default
        timerRun = viewModelScope.launch(Dispatchers.Default) {
            // Loop for counting down for two minutes until state changes.
            while (true) {
                // Wait one second
                delay(ONE_SECOND_MILLI)
                // If timer state is not in a counting state and a resumed state, we can break from this timer to
                // cancel/pause the timer.
                if (_timerState.value != TimerState.Counting && _timerState.value != TimerState.Resumed) break
                // Make sure that the timer does not go less than 0.
                if (_toothBrushTimer.value <= 0) {
                    // Set timer to 0 if timer is done.
                    _toothBrushTimer.value = 0
                    // Timer is in a finished state
                    _timerState.value = TimerState.Finished
                    // Break from the loop since you are at 0.
                    break
                }
                // If the timer passes 10 seconds, change the fun fact to be displayed.
                // Don't change the fun fact if the timer is at 2 minutes or at zero.
                // Also change the state of the tooth model.
                if (_toothBrushTimer.value.toInt() % (ONE_SECOND_MILLI.toInt() * 10) == 0 &&
                    _toothBrushTimer.value != TWO_MINUTES_MILLI &&
                    _toothBrushTimer.value.toInt() != 0) {
                    // Call method to change fact.
                    changeFact()
                    // Call method to change the teeth.
                    changeTeeth()
                }
                // If the timer has 10 seconds left, we change the state of the tooth model to tongue.
                if (_toothBrushTimer.value <= TEN_SECONDS_MILLI) changeTongue()
                // If you are not at 0, update timer by one second less.
                _toothBrushTimer.update {it - ONE_SECOND_MILLI}
            }

        }
    }
    // Method to pause the toothbrush timer.
    fun pauseTimer() {
        // Update the timer state to pause.
        _timerState.value = TimerState.Pause
        // Cancel the timer coroutine.
        timerRun?.cancel()
    }

    // Method to cancel the toothbrush timer.
    fun cancelTimer() {
        // Update the timer state to cancel.
        _timerState.value = TimerState.Cancel
        timerRun?.cancel()
    }

    // Method to reset the timer.
    fun resetTimer() {
        // Cancel the coroutine
        timerRun?.cancel()
        // Initialize the states to their beginning states.
        _timerState.value = TimerState.Begin
        _timerModelState.value = TimerModelState.Upper
        _timerModelEnabled.value = false
        // Set the timer to two minutes.
        _toothBrushTimer.update {TWO_MINUTES_MILLI}
    }

    // Method to change the fact.
    fun changeFact() {
        // Update the
        _timerFact.update {StringBuilder(timerFunFactsRepo.randomFact())}
    }

    // Demo finish.
    fun demoFinish() {
        // Timer set to zero.
        _toothBrushTimer.update {ZERO}
        // Coroutine canceled.
        timerRun?.cancel()
        // Timer state set to finished.
        _timerState.value = TimerState.Finished
    }

    // Method to toggle the tooth model.
    fun toggleTeeth() {
        // Flip the value of the timerModelEnabled
        _timerModelEnabled.value = !_timerModelEnabled.value
    }

    // Method to change the state of the teeth from upper to lower and vice versa.
    private fun changeTeeth() {
        if (timerModelState.value == TimerModelState.Upper) _timerModelState.value = TimerModelState.Lower
        else if (timerModelState.value == TimerModelState.Lower) _timerModelState.value = TimerModelState.Upper
    }

    // Method to change the state of the teeth for the tongue.
    private fun changeTongue() {
        _timerModelState.value = TimerModelState.Tongue
    }

    // Reference:
    // * https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories
    // * Chat GPT for clean up and readability.
    // We create this view model with a factory so that it can pass in arguments to specific repos.
    // The timer will pass in a repository with fun facts.
    class TimerViewModelFactory(private val timerFunFactsRepo: TimerFunFactsRepo) : ViewModelProvider.Factory {
        // Override method to create viewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Check if the model class is the same as or a superclass of TimerViewModel.
            // If it is we create an instance of TimerViewModel.
            if (modelClass.isAssignableFrom((TimerViewModel::class.java))) {
                @Suppress("UNCHECKED_CAST")
                // Create and return an instance of the TimerViewModel with the argument of the
                // repository
                return TimerViewModel(timerFunFactsRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


}

/*
    -Sealed class "TimerState" to represent different timer states.
    -We use a sealed class to ensure that "TimerState" has these only defined states below.
    -Use sealed so that when the TimerState changes, the UI reacts by observing it and processing it
        in a when expression to show what the user should see next.
 */
sealed class TimerState {
    // Begin State
    data object Begin : TimerState()
    // Counting State
    data object Counting : TimerState()
    // Paused State
    data object Pause : TimerState()
    // Resumed State
    data object Resumed : TimerState()
    // Canceled State
    data object Cancel: TimerState()
    // Finished State
    data object Finished: TimerState()

}

/*
    -Sealed class "TimerModelState" to represent different tooth model states.
    -We use a sealed class to ensure that "TimerModelState" has these only defined states below.
    -Use sealed so that when the TimerModelState changes, the UI reacts by observing it and processing it
        in a when expression to show what the user should see next.
 */

sealed class TimerModelState {
    // Upper Teeth State
    data object Upper : TimerModelState()
    // Lower Teeth State
    data object Lower : TimerModelState()
    // Tongue State
    data object Tongue : TimerModelState()
}
