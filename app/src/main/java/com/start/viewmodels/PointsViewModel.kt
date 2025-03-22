package com.start.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.start.repos.PointsRepo
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class PointsViewModel(private val pointsRepo: PointsRepo): ViewModel() {

    // Pass in the number of questions that were answered correctly by the user. Each
    // question answered correctly, multiplied by 10 will be the number of points awarded.
    fun addTriviaPoints(numCorrect: Long) {
        // Add points to Firestore database asynchronously
        viewModelScope.launch {
            // Add points to the user's account. Convert 10 to a long.
            pointsRepo.addPoints(numCorrect * 10L)
        }
    }

    // We create this view model with a factory so that it can pass in arguments to specific repos.
    // The trivia view model will pass in a repository with the trivia.
    class PointsViewModelFactory(private val pointsRepo: PointsRepo) : ViewModelProvider.Factory {
        // Override method to create viewModel
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            // Check if the model class is the same as or a superclass of PointsViewModel.
            // If it is we create an instance of PointsViewModel.
            if (modelClass.isAssignableFrom((PointsViewModel::class.java))) {
                @Suppress("UNCHECKED_CAST")
                // Create and return an instance of the HygieneTriviaViewModel with the argument of the
                // repository
                return PointsViewModel(pointsRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}