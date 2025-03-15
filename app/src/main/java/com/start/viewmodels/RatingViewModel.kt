package com.start.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateField
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FieldValue
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
const val CLINICS = "clinics"
const val CLINIC_RATINGS = "clinicRatings"
const val ACCOUNTS = "accounts"
const val USER_RATINGS = "userRatings"

// class that handles ratings and updates
class RatingViewModel: ViewModel() {

    // initialize authentication instance and database instance that viewmodel will utilize
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // handling fields for the list of ratings
    private val _ratingsList = MutableStateFlow<List<ClinicReview>>(emptyList())
    val ratingsList: StateFlow<List<ClinicReview>> get() = _ratingsList

    // handling fields for the number of ratings present
    private val _ratingsCount = MutableStateFlow<Int>(0)
    val ratingsCount: StateFlow<Int> get() = _ratingsCount

    // handling ratingID
    private var _ratingID: String? = null
    val ratingID: String? get() = _ratingID

    private val _clinicLikesAverage = MutableStateFlow<Float>(0f)
    val clinicLikesAverage: StateFlow<Float> get() = _clinicLikesAverage

    // handling fields for clinic rating average
    private val _clinicRatingAverage = MutableStateFlow<Float>(0f)
    val clinicRatingAverage: StateFlow<Float> get() = _clinicRatingAverage



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
        _ratingID = UUID.randomUUID().toString()
        val createdAt = LocalDateTime.now()

        // rating details that will be put in the ratings class
        // currently deprecated since it will be too much reading and writing for the time being
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
            "ratingID" to _ratingID,
            "clinicName" to clinicName,
            "ratingScore" to rating,
            "review" to review,
            "likeCount" to 0,
            "dislikeCount" to 0,
            "LikeDislike" to "neutral"
        )

        // rating details to be appended to the clinic
        val clinicRatingRecord = hashMapOf(
            "ratingID" to _ratingID,
            "review" to review,
            "ratingScore" to rating,
            "createdAt" to createdAt,
            "likeCount" to 0,
            "dislikeCount" to 0,
            "LikeDislike" to "neutral"
        )

        val userRatingRef = db.collection(ACCOUNTS).document(userID).collection(USER_RATINGS).document(clinicID)
        val clinicRatingRef = db.collection(CLINICS).document(clinicID).collection(CLINIC_RATINGS).document(userID)

        //handle all of the updates concurrently via the use of a coroutine
        CoroutineScope(Dispatchers.IO).launch {
            try {
                awaitAll(
                    // create a rating on the ratings collection
                    // currently deprecated since it will be too much reading and writing for the time being
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

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val ratings = clinicDocRef.get().await() // using await to fetch synchroncusly

                // create a handler for a ClinicReview data class to be used for showing reviews
                val processedRatings = mutableListOf<ClinicReview>()

                // for each rating in the clinic
                for (rating in ratings) {

                    // get the data
                    val data = rating.data

                    // then get the relevant data: the rating, the review, the date it was made,
                    // and the like/dislikes
                    val ratingScore = (data["ratingScore"] as? Long)?.toInt() ?: 0
                    val review = data["review"] as? String ?: ""
                    val ratingID = data["ratingID"] as? String ?: ""

                    val likes = (data["likeCount"] as? Long)?.toInt() ?: 0
                    val dislikes = (data["dislikeCount"] as? Long)?.toInt() ?: 0
                    val netLikes = data["LikeDislike"] as? String ?: "neutral"

                    // since Firestore time is being stored as a map, explicitly cast it to a map
                    // then use helper function to make it inton a LocalDateTime value being used
                    // by this app
                    val createdAtMap = data["createdAt"] as? Map<String, Any>
                    val createdAt =
                        createdAtMap?.let { mapToLocalDateTime(it) } ?: LocalDateTime.now()


                    // store them into a ClinicRating data class then append it to the handler
                    val processedRating = ClinicReview(
                        ratingScore, review, createdAt, netLikes,
                        likes, dislikes, ratingID
                    )
                    processedRatings.add(processedRating)

                }

                // once its finished... replace the value of _clinicList into the contents of the handler\
                _ratingsList.value = processedRatings
                _ratingsCount.value = processedRatings.size
                sortReviews("Most Recent")
                Log.d("Clinic Ratings Fetching", "Clinic Ratings successfully fetched")
            }

                // if it fails...
            catch (e: Exception) {
                    // log the reason why it failed
                    Log.e("Clinic Ratings Fetching", e.message.toString())
            }
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
    // currently not being used since Kevin is implementing a better one
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
    fun calculateClinicRatingAverage(clinicID: String?) {

        // holder values to make function work properly
        var ratingAverage: Float = 0F // holds ratingAverage that will be appended to rating holder soon
        val docRef = db.collection(CLINICS).document(clinicID.toString()).collection(CLINIC_RATINGS) // collection reference for the query
        val query = docRef.aggregate(AggregateField.average("ratingScore")) // aggregation query for getting the rating average of the clinic

        // Use a coroutine to do the process asynchronously
        // Scope main process in a try-catch block to handle errors (may not always work)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                async {
                    // do the Firebase query in a seperate thread called task
                    query.get(AggregateSource.SERVER).addOnCompleteListener { task ->
                        // if the task is successful...
                        if (task.isSuccessful) {
                            // process the results and check if there is a floating point output
                            val snapshot = task.result
                            val processedSnapshot =
                                snapshot.get(AggregateField.average("ratingScore"))?.toFloat()

                            // if the result is a float...
                            if (processedSnapshot is Float) {
                                // put the result in the container and
                                // declare that the result is a success then log the details
                                ratingAverage = processedSnapshot
                                _ratingState.value = RatingState.Success("Clinic Average successfully calculated")
                                Log.d("Getting Rating Average", "Clinic Average successfully calculated. Clinic Average = $processedSnapshot")

                            }
                            // if it's not...
                            else {
                                // set the float handler to zero then declare the results
                                Log.d("Getting Rating Average", "Clinic Average cannot be calculated. Clinic Average = $processedSnapshot")
                                ratingAverage = 0f
                                _ratingState.value = RatingState.Error("Clinic Ratings cannot be calculated")
                            }
                            // in both cases, set the clinicRatingAverage field into the handler's value
                            _clinicRatingAverage.value = ratingAverage
                        }
                        // if the task fails
                        else {
                            // output the error message then set the clinic average to zero
                            Log.e("Getting Rating Average", task.exception?.message.toString())
                            _ratingState.value =
                                RatingState.Error(task.exception?.message.toString())
                            _clinicRatingAverage.value = 0f
                        }
                    }.await()
                }
            }
            // if an exception occurs, display and log the error message
            catch(e: Exception) {
                _ratingState.value = RatingState.Error(e.message.toString())
                Log.e("Getting Rating Average", e.message.toString())
            }
        }
    }

    //get the clinicRatingAverage
    fun getClinicRatingAverage(): Float {
        return _clinicRatingAverage.value
    }

    // Update likes or dislikes on an individual rating
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateLikeDislike(clinicID: String, ratingID: String, userDoesLikeDislike: String) {
        // Gather necessary variables and access to database
        val userID = auth.currentUser?.uid.toString()
        val clinicRatingRef = db.collection("clinics").document(clinicID).collection("clinicRatings").document(ratingID)  // Create document for each user
        val userLikeDislikeRef = clinicRatingRef.collection("userLikesDislikes").document(userID)

        // Coroutine to handle asynchronously
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // ensure that the documents for clinicRatingRef exists, if not, create them
                val clinicRatingDoc = clinicRatingRef.get().await()
                if (!clinicRatingDoc.exists()) {
                    // Create the review document with default values
                    clinicRatingRef.set(
                        hashMapOf(
                            "ratingID" to ratingID,
                            "likeCount" to 0,
                            "dislikeCount" to 0,
                            "LikeDislike" to "neutral"
                        )
                    ).await()
                    }

                // Proceed with likes and dislikes
                val currentReviewDoc = clinicRatingRef.get().await()
                val currentLikeDislike = currentReviewDoc.getString("LikeDislike") ?: ""

                // Check if the user is not repeatedly clicking the like/dislike button on a rating
                // If they aren't, then the data will be updated to whatever they picked
                if (currentLikeDislike != userDoesLikeDislike) {
                    val updateData = hashMapOf<String, Any>(
                        "LikeDislike" to userDoesLikeDislike
                    )

                    // Adjust likes and dislikes based on what the user does
                    if (userDoesLikeDislike == "like") {
                        if (currentLikeDislike == "dislike") {
                            // Update dislike count to negate previous dislike, then add a like
                            updateData["dislikeCount"] = FieldValue.increment(-1)
                            updateData["likeCount"] = FieldValue.increment(1)
                        } else {
                            // First time liking, so add a like
                            updateData["likeCount"] = FieldValue.increment(1)
                        }
                    } else if (userDoesLikeDislike == "dislike") {
                        // Same logic as above but reversed
                        if (currentLikeDislike == "like") {
                            updateData["likeCount"] = FieldValue.increment(-1)
                            updateData["dislikeCount"] = FieldValue.increment(1)
                        } else {
                            updateData["dislikeCount"] = FieldValue.increment(1)
                        }
                    }

                    // Update like/dislike state
                    userLikeDislikeRef.set(mapOf("LikeDislike" to userDoesLikeDislike)).await()

                    // Perform the database update for clinic rating
                     clinicRatingRef.update(updateData).await()


                    // Update the ClinicReview object in _ratingsList
                    val updatedRatings = _ratingsList.value.map { review ->
                        if (review.ratingID == ratingID) {
                            review.copy(
                                likeCount = (updateData["likeCount"] as? Long)?.toInt() ?: review.likeCount,
                                dislikeCount = (updateData["dislikeCount"] as? Long)?.toInt() ?: review.dislikeCount,
                                likeDislike = updateData["LikeDislike"] as? String ?: review.likeDislike
                            )
                        } else {
                            review
                        }
                    }

                    _ratingsList.value = updatedRatings
                    // Fetch to ensure UI changes
                    getClinicRatings(clinicID)
                    _ratingState.value = RatingState.Success("Like/Dislike Updated Successfully!")
                }
            } catch (e: Exception) {
                Log.e("LikeDislike update", e.message.toString())
                _ratingState.value = RatingState.Error(e.message.toString())
            }
        }
    }


    // Outputs the likes average of individual ratings
    fun calculateRatingLikesAverage(clinicID: String?) {
        // holder values to make function work properly
        var likesAverage: Float = 0F // holds likesAverage that will be appended to rating holder soon
        val docRef = db.collection(CLINICS).document(clinicID.toString()).collection(CLINIC_RATINGS) // collection reference for the query
        val query = docRef.aggregate(AggregateField.average("likesScore")) // aggregation query for getting the likes average of the clinic

        // Use a coroutine to do the process asynchronously
        // Scope main process in a try-catch block to handle errors (may not always work)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                async {
                    // do the Firebase query in a separate thread called task
                    query.get(AggregateSource.SERVER).addOnCompleteListener { task ->
                        // if the task is successful...
                        if (task.isSuccessful) {
                            // process the results and check if there is a floating point output
                            val snapshot = task.result
                            val processedSnapshot =
                                snapshot.get(AggregateField.average("likesScore"))?.toFloat()

                            // if the result is a float...
                            if (processedSnapshot is Float) {
                                // put the result in the container and
                                // declare that the result is a success then log the details
                                likesAverage = processedSnapshot
                                _ratingState.value = RatingState.Success("Rating's Like Average successfully calculated")
                                Log.d("Getting Rating Average", "Rating's Like Average successfully calculated. Like Average = $processedSnapshot")

                            }
                            // if it's not...
                            else {
                                // set the float handler to zero then declare the results
                                Log.d("Getting Rating's Net Likes", "Rating's Net Likes cannot be calculated. Like Average = $processedSnapshot")
                                likesAverage = 0f
                                _ratingState.value = RatingState.Error("Rating's Net Likes cannot be calculated")
                            }
                            // in both cases, set the clinicLikesAverage field into the handler's value
                            _clinicLikesAverage.value = likesAverage
                        }
                        // if the task fails
                        else {
                            // output the error message then set the clinic average to zero
                            Log.e("Getting Rating Net Likes", task.exception?.message.toString())
                            _ratingState.value =
                                RatingState.Error(task.exception?.message.toString())
                            _clinicLikesAverage.value = 0f
                        }
                    }.await()
                }
            }
            // if an exception occurs, display and log the error message
            catch(e: Exception) {
                _ratingState.value = RatingState.Error(e.message.toString())
                Log.e("Getting Net Likes", e.message.toString())
            }
        }
    }
    // get the clinicLikesAverage
    fun getClinicLikesAverage(): Float {
        return _clinicLikesAverage.value
    }

    // Delete a rating a user has made
    // Should only delete ratings the user has made, not other reviews
    fun deleteReview() {
    }

    // Sort ratings based on the specification user choose
    fun sortReviews(sortedBy: String) {

        //sort the list by the most recent ratings
        if (sortedBy == "Most Recent") {
            _ratingsList.value = _ratingsList.value.sortedWith(
                compareByDescending<ClinicReview> { it.createdAt})
        }

        // sort the list by the most helpful ratings (likes > dislikes, if amount is the same, display the most recent one)
        //TODO: Change this function to compare like amounts instead of rating when Kelson finishes likes and dislike implementation
        if (sortedBy == "Most Helpful") {
            _ratingsList.value = _ratingsList.value.sortedWith(
                compareByDescending<ClinicReview> { it.rating }.thenByDescending { it.createdAt }
            )
        }
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
// TODO: may need to be modified based on Kelson's likes and dislikes implementation
data class ClinicReview(
    val rating: Int,
    val review: String,
    val createdAt: LocalDateTime,
    var likeDislike: String, // for when a user likes/dislikes a rating, initialized as String to avoid confusion
    var likeCount: Int = 0, // Count of likes on an individual rating
    var dislikeCount: Int = 0, // Count of dislikes on an individual rating
    val ratingID: String
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