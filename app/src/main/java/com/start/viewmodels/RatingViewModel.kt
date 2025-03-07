package com.start.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.util.UUID

// class that handles ratings and updates
class RatingViewModel: ViewModel() {

    // initialize an authentication instance
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Creates a rating based on the given parameters and stores it on the database
    @RequiresApi(Build.VERSION_CODES.O)
    fun createRating(rating: Int, review: String, clinicID: String, clinicName: String) {
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
            "clinicID" to clinicID,
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
                        .collection("userRatings").document(ratingID)
                        .set(userRatingRecord).await()
                }

                // Third Firebase Task: Add rating to clinic database
                val addRatingToClinics = async {
                    db.collection("clinics").document(clinicID).
                    collection("clinicRatings").document(ratingID).
                    set(clinicRatingRecord).await()}

                // Execute all tasks concurrently
                awaitAll(addRatingtoRatings, addRatingtoUsers, addRatingToClinics)
            }
            catch(e: Exception) {
                Log.e("Rating creation", e.message.toString())
            }
        }

    }

    // Outputs the current rating average of a clinic
    fun calculateRatingScoreAverage() {
    }
}