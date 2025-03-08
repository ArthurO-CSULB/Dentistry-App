package com.start.viewmodels

import android.media.Rating
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.UUID

// class that handles ratings and updates
class RatingViewModel: ViewModel() {

    // initialize authentication instance and database instance that viewmodel will utilize
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // private value that holds the state of the viewmodel
    private val _ratingState = MutableLiveData<RatingState>(RatingState.Idle)

    // public viewmodel state holder that is utilized by the pages
    val ratingState: LiveData<RatingState> = _ratingState

    // Creates a rating based on the given parameters and stores it on the database
    @RequiresApi(Build.VERSION_CODES.O)
    fun createRating(rating: Int, review: String, clinicID: String, clinicName: String) {

        // set viewmodel state to creating a rating
        _ratingState.value = RatingState.UpdatingEntries

        var userID = auth.currentUser?.uid.toString()
        var ratingID = UUID.randomUUID().toString()
        var createdAt = LocalDateTime.now()

        // Create rating details that will be appended to ratings collection
        val ratingDetails = hashMapOf(
            "creatorID" to userID,
            "clinicID" to clinicID,
            "clinicName" to clinicName,
            "createdAt" to createdAt,
            "ratingScore" to rating,
            "review" to review,
        )

        // Create rating record for user
        val userRatingRecord = hashMapOf(
            "ratingID" to ratingID,
            "clinicName" to clinicName,
            "ratingScore" to rating,
            "review" to review,
        )

        // Create rating record for clinic
        val clinicRatingRecord = hashMapOf(
            "ratingID" to ratingID,
            "review" to review,
            "ratingScore" to rating,
            "createdAt" to createdAt
        )

        //make use of coroutines to do three tasks at once
        CoroutineScope(Dispatchers.IO).launch {

            try {
                // First Firebase operation: Add rating to ratings database
                val addRatingtoRatings = async {
                    db.collection("ratings").document(ratingID).set(ratingDetails).await()
                }

                // Second Firebase operation: Add rating to user ratings
                val addRatingtoUsers = async {
                    db.collection("accounts").document(userID)
                        .collection("userRatings").document(clinicID)
                        .set(userRatingRecord).await()
                }

                // Third Firebase Task: Add rating to clinic database
                val addRatingToClinics = async {
                    db.collection("clinics").document(clinicID).
                    collection("clinicRatings").document(userID).
                    set(clinicRatingRecord).await()}

                // Execute all tasks concurrently
                awaitAll(addRatingtoRatings, addRatingtoUsers, addRatingToClinics)
            }
            catch(e: Exception) {
                Log.e("Rating creation", e.message.toString())
                _ratingState.value = RatingState.Error(e.message.toString())
            }
        }

        // Change the viewmodel state when the function is successful
        _ratingState.value = RatingState.Success("Rating Successfully Created!")
        _ratingState.value = RatingState.Idle

    }

    // Outputs the current rating average of a clinic
    fun calculateRatingScoreAverage() {
    }

    // Function that changes the state when user starts creating a rating
    fun ratingCreationEnter() {
        _ratingState.value = RatingState.CreatingARating
    }

    // Function that changes the state when user exits rating creation
    fun ratingCreationExit() {
        _ratingState.value = RatingState.Idle
    }
}

sealed class RatingState {
    // state when a user is currently creating a rating
    data object CreatingARating : RatingState()

    // state when viewModel is modifying the backend
    data object UpdatingEntries: RatingState()

    // state when viewModel is doing nothing
    data object Idle: RatingState()

    // state when an error occurs in the viewmodel
    data class Error(val message: String): RatingState()

    // state to display messages in the viewmodel
    data class Success(val message: String): RatingState()
}