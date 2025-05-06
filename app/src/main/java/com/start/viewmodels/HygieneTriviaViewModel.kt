package com.start.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.start.repos.HygieneTriviaRepo
import com.start.repos.TimerFunFactsRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

const val THIRTY_SECONDS_MILLI : Long = 30000

// View model to control the state of the hygiene trivia.
class HygieneTriviaViewModel(private val hygieneTriviaRepo: HygieneTriviaRepo) : ViewModel() {
    // Store the state of the hygiene trivia. Initialize it to the beginning state.
    private val _hygieneTriviaState = MutableStateFlow<HygieneTriviaState>(HygieneTriviaState.Begin)
    val hygieneTriviaState = _hygieneTriviaState
    // Store the index of where the user is in the trivia.
    private val _triviaIndex = MutableStateFlow<Int>(0)
    val triviaIndex = _triviaIndex
    // Store the index of where the user is in the results.
    private val _resultsIndex = MutableStateFlow<Int>(0)
    val resultsIndex = _resultsIndex
    // Store the questions that were randomly given by the repository, five random questions. List<HygieneTriviaRepo.DentalTriviaQnA>
    private var _questions = MutableStateFlow<List<HygieneTriviaRepo.DentalTriviaQnA>>(hygieneTriviaRepo.randomQuestions())
    var questions = _questions

    // Store the indexes of the user's answers.
    private val _userAnswersIndex = mutableStateListOf<Int>()
    val userAnswersIndex = _userAnswersIndex
    // Store the timer that the UI will observe.
    private val _hygieneTriviaTimer = MutableStateFlow<Long>(THIRTY_SECONDS_MILLI)
    val hygieneTriviaTimer: StateFlow<Long> = _hygieneTriviaTimer

    // The coroutine that will run the timer concurrently with the main thread.
    private var timerRun: kotlinx.coroutines.Job? = null


    // Method to begin the trivia.
    fun beginTrivia() {
        startTriviaTimer()
        _hygieneTriviaState.value = HygieneTriviaState.Trivia
    }
    // Increment the index by one for the next QnA.
    fun nextQuestion() {
        _triviaIndex.value++
    }
    fun nextResult() {
        _resultsIndex.value++
    }
    // Method to finish the trivia.
    fun finishTrivia() {
        // Cancel the timer first.
        cancelTriviaTimer()
        _hygieneTriviaState.value = HygieneTriviaState.Finished
    }
    // Method to fail the trivia.
    private fun failTrivia() {
        _hygieneTriviaState.value = HygieneTriviaState.Failed
    }
    // Method to go to the points screen.
    fun goToPoints() {
        _hygieneTriviaState.value = HygieneTriviaState.Points
    }

    // Reset the trivia to beginning state, reset the index, reset the answers list, reset the timer,
    // and get a new set of questions.
    fun resetTrivia() {
        _hygieneTriviaState.value = HygieneTriviaState.Begin
        resetTriviaIndex()
        resetResultsIndex()
        resetTriviaTimer()
        _userAnswersIndex.clear()
        _questions.update {hygieneTriviaRepo.randomQuestions()}
    }

    // Reset the index to zero.
    private fun resetTriviaIndex() {
        _triviaIndex.value = 0
    }

    private fun resetResultsIndex() {
        _resultsIndex.value = 0
    }

    // Method to calculate the number of correct answers that the user got.
    fun numCorrect(): Int {
        // Number of correctly answered questions
        var numCorrect = 0
        if (hygieneTriviaState.value == HygieneTriviaState.Points) {

            // answers to the randomly given questions.
            val answers: List<String> = questions.value.map { it.answer }
            // choices to the randomly given questions.
            val choices: List<List<String>> = questions.value.map { it.choices }
            // Loop through the number of questions.
            for(i in 0 until questions.value.size) {
                // If the user got the question correct, increment numCorrect.
                if (answers[i] == choices[i][userAnswersIndex[i]]) {
                    numCorrect++
                }
            }
        }
        return numCorrect
    }

    // Function to store the user's index answers.
    fun storeAnswer(index: Int) {
        _userAnswersIndex.add(index)
    }

    // Function to start the hygiene trivia timer.
    private fun startTriviaTimer() {
        // Launch the coroutine to run the timer.
        timerRun = viewModelScope.launch(Dispatchers.Default) {
            // While the timer is not at zero...
            while(_hygieneTriviaTimer.value != ZERO) {
                // Wait one second.
                delay(ONE_SECOND_MILLI)
                // Update the timer by subtracting one second.
                _hygieneTriviaTimer.update {it - ONE_SECOND_MILLI}
                // Check if the timer is at zero.
                if (_hygieneTriviaTimer.value <= ZERO) {
                    // Update the state to failed and cancel the coroutine.
                    failTrivia()
                    cancelTriviaTimer()
                }
            }
        }
    }

    // Function to cancel the hygiene trivia timer.
    private fun cancelTriviaTimer() {
        timerRun?.cancel()
    }

    // Function to reset the timer to 30 seconds.
    private fun resetTriviaTimer() {
        _hygieneTriviaTimer.value = THIRTY_SECONDS_MILLI
    }

    // Demo when the trivia is failed. Cancel trivia and fail user.
    fun demoFailed() {
        cancelTriviaTimer()
        failTrivia()
    }

    // We create this view model with a factory so that it can pass in arguments to specific repos.
    // The trivia view model will pass in a repository with the trivia.
    class HygieneTriviaViewModelFactory(private val hygieneTriviaRepo: HygieneTriviaRepo) : ViewModelProvider.Factory {
        // Override method to create viewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Check if the model class is the same as or a superclass of TimerViewModel.
            // If it is we create an instance of HygieneTriviaViewModel.
            if (modelClass.isAssignableFrom((HygieneTriviaViewModel::class.java))) {
                @Suppress("UNCHECKED_CAST")
                // Create and return an instance of the HygieneTriviaViewModel with the argument of the
                // repository
                return HygieneTriviaViewModel(hygieneTriviaRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}

// Sealed class to represent the state of the hygiene trivia.
sealed class HygieneTriviaState {
    // State when the trivia is just beginning.
    data object Begin: HygieneTriviaState()
    // State for when the user is doing trivia.
    data object Trivia: HygieneTriviaState()
    // State for when the user is finished with the trivia.
    data object Finished: HygieneTriviaState()
    // State for when the user failed the trivia because they ran out of time.
    data object Failed: HygieneTriviaState()
    // State for when the user goes to the points screen.
    data object Points: HygieneTriviaState()
}