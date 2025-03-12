package com.start.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.start.model.PlaceDetails
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
import kotlin.Long

// literal strings to use for document references
// avoids logical mistakes
val CLINICS = "clinics"
val CLINIC_RATINGS = "clinicRatings"
val ACCOUNTS = "accounts"
val USER_RATINGS = "userRatings"

// class that handles ratings and updates
class RatingViewModel: ViewModel() {

    // initialize authentication instance and database instance that viewmodel will utilize
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // handling fields for clinic ratings
    private val _clinicRatingsList = MutableStateFlow<List<ClinicReview>>(emptyList())
    val clinicRatingsList: StateFlow<List<ClinicReview>> get() = _clinicRatingsList

    // handling fields for clinic rating average
    private val _clinicRatingAverage = MutableStateFlow<Int?>(null)
    val clinicRatingAverage: StateFlow<Int?> get() = _clinicRatingAverage


    // handling fields for rating states
    private val _ratingState = MutableStateFlow<RatingState>(RatingState.Idle)
    val ratingState: StateFlow<RatingState> get() = _ratingState


    // function call when creating a rating
    @RequiresApi(Build.VERSION_CODES.O)
    fun createRating(rating: Int, review: String, clinicID: String, clinicName: String) {

        // declare state that rating is being updated
        _ratingState.value = RatingState.UpdatingEntries

        // declare parameters that will be used for creating the data hashmaps
        val userID = auth.currentUser?.uid.toString()
        val ratingID = UUID.randomUUID().toString()
        val createdAt = LocalDateTime.now()

        // rating details that will be put in the ratings class
        /*
        val ratingDetails = hashMapOf(
            "creatorID" to userID,
            "clinicID" to clinicID,
            "clinicName" to clinicName,
            "createdAt" to createdAt,
            "ratingScore" to rating,
            "review" to review,
        )
         */

        // rating details to be appended to the user
        val userRatingRecord = hashMapOf(
            "ratingID" to ratingID,
            "clinicName" to clinicName,
            "ratingScore" to rating,
            "review" to review,
        )

        // rating details to be appended to the clinic
        val clinicRatingRecord = hashMapOf(
            "ratingID" to ratingID,
            "review" to review,
            "ratingScore" to rating,
            "createdAt" to createdAt
        )

        val userRatingRef = db.collection(ACCOUNTS).document(userID).collection(USER_RATINGS).document(clinicID)
        val clinicRatingRef = db.collection(CLINICS).document(clinicID).collection(CLINIC_RATINGS).document(userID)

        //handle all of the updates concurrently via the use of a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                awaitAll(
                    // create a rating on the ratings collection
                    // async { db.collection("ratings").document(ratingID).set(ratingDetails).await() },

                    // create a rating in the users collection
                    // user can only rate once per clinic, will replace old rating if done again
                    async { userRatingRef.set(userRatingRecord).await() },

                    // create a rating in the clinics collection
                    // a clinic can only have one rating per user
                    // avoids tanking rating score
                    async { clinicRatingRef.set(clinicRatingRecord).await() }
                )

                // if rating is created successfully, display a toast message
                _ratingState.value = RatingState.Success("Rating Successfully Created!")

            }
            // if an exception occurs, state why and output to log
            catch (e: Exception) {
                Log.e("Rating creation", e.message.toString())
                _ratingState.value = RatingState.Error(e.message.toString())
            }
        }
    }

    // function that handles getting all ratings on a clinic
    @RequiresApi(Build.VERSION_CODES.O)
    fun getClinicRatings(clinicID: String) {

        val clinicDocRef = db.collection("clinics").document(clinicID)
            .collection("clinicRatings")

        //get all ratings on a clinic
        clinicDocRef.get()

            // if successful...
            .addOnSuccessListener { ratings ->

                // create a handler for a ClinicReview data class to be used for showing reviews
                val processedRatings = mutableListOf<ClinicReview>()

                // for each rating in the clinic
                for (rating in ratings) {

                    // get the data
                    val data = rating.data
                    Log.d("Clinic Ratings Fetching", "Processing rating: $data")

                    // then get the relevant data: the rating, the review, and the date it was made
                    val ratingScore = (data["ratingScore"] as? Long)?.toInt() ?: 0
                    val review = data["review"] as? String ?: ""

                    // since Firestore time is being stored as a map, explicitly cast it to a map
                    // then use helper function to make it inton a LocalDateTime value being used
                    // by this app
                    val createdAtMap = data["createdAt"] as? Map<String, Any>
                    val createdAt = createdAtMap?.let { mapToLocalDateTime(it) } ?: LocalDateTime.now()

                    // store them into a ClinicRating data class then append it to the handler
                    val processedRating = ClinicReview(ratingScore, review, createdAt)
                    processedRatings.add(processedRating)
                }

                // once its finished... replace the value of _clinicList into the contents of the handler
                _clinicRatingsList.value = processedRatings
                Log.d("Clinic Ratings Fetching", "Clinic Ratings successfully fetched")
            }

            // if it fails...
            .addOnFailureListener { exception ->
                // log the reason why it failed
                Log.e("Clinic Ratings Fetching", exception.message.toString())
            }
    }


    // helper function for changing Firestore time format into a LocalDateTime format
    @RequiresApi(Build.VERSION_CODES.O)
    fun mapToLocalDateTime(dateMap: Map<String, Any>): LocalDateTime {
        // Since Firestore stores numbers as Long, we make every value being stored in the DateMap
        // into an Int by casting it into a Long, then converting it into an Int.
        // If it's not available, turn it to zero
        val year = (dateMap["year"] as? Long)?.toInt() ?: 0
        val monthValue = (dateMap["monthValue"] as?Long)?.toInt() ?: 0
        val dayOfMonth = (dateMap["dayOfMonth"] as? Long)?.toInt() ?: 0
        val hour = (dateMap["hour"] as? Long)?.toInt() ?: 0
        val minute = (dateMap["minute"] as? Long)?.toInt() ?: 0
        val second = (dateMap["second"] as? Long)?.toInt() ?: 0
        val nano = (dateMap["nano"] as? Long)?.toInt() ?: 0

        // then create a LocalDateTime object with the extracted data
        return LocalDateTime.of(year, monthValue, dayOfMonth, hour, minute, second, nano)
    }

    // function for adding a clinic into the clinic database
    // maybe replaced in the future as Kevin is also making one
    // Made a temporary one just for testing, update later
    fun addClinicToDB(clinicDetails: PlaceDetails?)
    {
        var clinicData= hashMapOf(
            "name" to (clinicDetails?.name.toString()),
            "address" to clinicDetails?.address.toString(),
            "phone" to clinicDetails?.phoneNumber.toString()
        )
        db.collection("clinics").document(clinicDetails?.placeId.toString()).set(clinicData)
            .addOnSuccessListener {
                Log.d("Adding clinic to db", "Clinic successfully added to database")
            }
    }

    // Outputs the current rating average of a clinic
    fun calculateRatingScoreAverage() {
    }

    // Outputs the current number of reviews a clinic has
    fun getClinicRatingsCount() {
    }

    // Outputs the current number of ratings a user has made
    fun getUserRatingsCount() {
    }

    // Delete a rating a user has made
    // Should only delete ratings the user has made, not other reviews
    fun deleteReview() {
    }

    // Sort ratings based on the specification user choose
    fun sortReviews() {

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

// Data class for storing clinic ratings
data class ClinicReview(
    val rating: Int,
    val review: String,
    val createdAt: LocalDateTime
)

// sealed class used for handling state changes in this viewmodel
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