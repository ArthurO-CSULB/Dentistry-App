package com.start.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.start.repos.HygieneTriviaRepo
import com.start.repos.TimerFunFactsRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.lang.IllegalArgumentException

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
    // Store the answers to the randomly given questions.
    private val _answers: List<String> = questions.value.map { it.answer }
    val answers = _answers
    // Store the indexes of the user's answers.
    private val _userAnswersIndex = mutableStateListOf<Int>()
    val userAnswersIndex = _userAnswersIndex

    // Method to begin the trivia.
    fun beginTrivia() {
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
        _hygieneTriviaState.value = HygieneTriviaState.Finished
    }
    // Reset the trivia to beginning state, reset the index, reset the answers list, and get a new
    // set of questions.
    fun resetTrivia() {
        _hygieneTriviaState.value = HygieneTriviaState.Begin
        resetTriviaIndex()
        resetResultsIndex()
        _userAnswersIndex.clear()
        _questions.update {hygieneTriviaRepo.randomQuestions()}
    }

    // Reset the index to zero.
    fun resetTriviaIndex() {
        _triviaIndex.value = 0
    }

    fun resetResultsIndex() {
        _resultsIndex.value = 0
    }

    // Function to store the user's index answers.
    fun storeAnswer(index: Int) {
        _userAnswersIndex.add(index)
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
}