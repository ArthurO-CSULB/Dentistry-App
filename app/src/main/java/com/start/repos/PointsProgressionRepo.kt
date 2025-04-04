package com.start.repos

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

// Repo to connect to the database.
class PointsProgressionRepo(context: Context) {
    // Store references to the Firebase Authentication and Firestore instances.
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Initialize a reference to the user's account.
    var userAccount: DocumentReference? = null
    // Initialize a listener to the user's account.
    var userAccountListener: ListenerRegistration? = null

    /*
    // Initialize a listener, that when the user logs in, create a reference and listener to the user's account.
    init {
        auth.addAuthStateListener { firebaseAuth ->
            // If there is a user logged in.
            if (firebaseAuth.currentUser != null) {
                // Create reference to user's account with snapshot listener
                userAccount = getUserAccount()
                // Add snapshot listener to users account to react to changes in the points.
                userAccount!!.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
                    // If there is an error getting the snapshot...
                    if (e != null) {
                        // Log the error.
                        Log.e(TAG, "User account listener failed", e)
                        return@addSnapshotListener
                    }
                    // If there is a snapshot...
                    if (snapshot != null && snapshot.exists()) {
                        Log.i(TAG, "User account snapshot exists: ${snapshot.data}")

                    }
                }
            }
            // If there is no user logged in, remove account reference.
            else {
                // Set account reference to null
                userAccount = null
            }
        }
    }
    */

    // ***************************************************************************************
    // callbackFlow is a Kotlin tool that lets you convert a callback-based API into a
    // coroutine-based Flow, so you can collect it cleanly in a coroutine.

    /*

    // Get point to the account. https://firebase.google.com/docs/firestore/query-data/listen#kotlin
    // Listen for metadata changes to the account.
    private val accountRef = getUserAccount().addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
        // If there is an error, log the error.
        if (e != null) {
            Log.w(TAG, "Listen failed.", e)
            return@addSnapshotListener
        }

        if (snapshot != null && snapshot.exists()) {
            Log.d(TAG, "Current data: ${snapshot.data}")
        }
        else {
            Log.d(TAG, "Current data: null")
        }

    }
    */

    // Aided with ChatGPT and
    // https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/callback-flow.html
    // https://firebase.google.com/docs/reference/kotlin/com/google/firebase/auth/FirebaseAuth.AuthStateListener

    // Method to emit a boolean variable that will indicate if the user is logged in.
    // PointsProgressionViewModel will observe this variable. Upon true, view model will observe a
    // flow that is emitted from the repo to update the UI based on changes in the user account.
    // Will be called in the view model upon initialization
    fun loggedInFlow(): Flow<Boolean> = callbackFlow {
        // We declare a listener to listen for the changes in the authentication state of the app.
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            // We check if the user is logged in or not.
            val isLoggedIn = firebaseAuth.currentUser != null
            // Initialize the reference to the user account.
            if (isLoggedIn) {
                userAccount = db.collection("accounts").document(auth.currentUser?.uid.toString())
            }
            else {
                userAccount = null
            }
            Log.d(TAG, "Emitting logged in status: $isLoggedIn")
            // Emit that the user is logged in or not.
            trySend(isLoggedIn)
        }

        // Add the listener to the auth instance.
        auth.addAuthStateListener(authStateListener)
        // We remove the listener when the flow is cancelled.
        awaitClose {
            // Remove the listener
            auth.removeAuthStateListener(authStateListener)
            Log.d(TAG, "Auth state listener removed")
        }
        // Only changes are emitted to the flow.
    }.distinctUntilChanged()

    // Aided with ChatGPT and
    // https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/callback-flow.html
    // https://firebase.google.com/docs/reference/kotlin/com/google/firebase/firestore/ListenerRegistration
    // Method to emit the user points
    fun userLevelFlow(): Flow<PointsPrestige> = callbackFlow {

        // Store the ListenerRegistration in userAccountListener.
        userAccountListener = userAccount!!.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
            // If there is an error, log the error.
            if (e != null) {
                // Log the error.
                Log.e(TAG, "User account listener failed", e)
                return@addSnapshotListener
            }
            // If there is a snapshot...
            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "User account snapshot exists: ${snapshot.data}")
            }
            // Get the experience and prestige form the snapshot.
            val experience = if (snapshot!!.contains("experience")) {
                snapshot.getLong("experience") ?: 0L
            }
            else {
                0L
            }
            val prestige = if (snapshot.contains("prestige")) {
                snapshot.getLong("prestige") ?: 0L
            }
            else {
                0L
            }
            // Emit the points and experience.
            trySend(PointsPrestige(experience, prestige))
        }

        // We remove the listener when the flow is cancelled.
        awaitClose {
            // Remove the listener
            userAccountListener?.remove()
            userAccountListener = null
            Log.d(TAG, "user account listener removed")
        }
    // Only changes are emitted to the flow.
    }.distinctUntilChanged()


    // Method to detach a listener to the user's account for points and prestige.
    fun detachPointsPrestigeListener() {
        // Remove listener and set to null.
        userAccountListener?.remove()
        userAccountListener = null
    }

    fun test() {
        // Get the id of the current user.
        Log.d("Current User ID", auth.currentUser?.uid.toString())
    }

    // Method to Add points to the user's account
    fun addPoints(points: Long) {
        // Get the reference to the user's account using the current user's ID.
        //val userAccount = getUserAccount()
        // Increment the user's experience by the points passed in to addPoints.
        userAccount?.update("experience", FieldValue.increment(points))
            // Log the success.
            ?.addOnSuccessListener {
                Log.d("Adding Points", "Points added successfully")
            }
            // Log the failure
            ?.addOnFailureListener { e ->
                Log.w("Adding Points", "Error adding points", e)
            }
    }

    // Method to prestige the user's account
    fun prestige() {
        // Get the user's
        //val userAccount = getUserAccount()
        // Increment the user's prestige by 1.
        userAccount?.update("prestige", FieldValue.increment(1))
    }

    // Method to get the prestige of the user.
    fun getPrestige(onResult: (Long) -> Unit) {
        // Get the user's account.
        // Get the prestige of the user.
        // Query the document in the database.
        userAccount?.get()
            ?.addOnSuccessListener { account ->
                // If the account contains prestige get prestige
                val prestige = if (account.contains("prestige")) {
                    account.getLong("prestige") ?: 0L
                }
                else {
                    userAccount?.update("prestige", 0)
                    0L
                }
                // Call the callback with the prestige value
                onResult(prestige)
            }
            ?.addOnFailureListener {
                Log.d("Getting Prestige", "Error getting prestige. Account doesn't exist.")
                onResult(0L)
            }
    }

    /*
    // Method to return a reference to the user's account
    private fun getUserAccount(): DocumentReference? {
        return db.collection("accounts").document(auth.currentUser?.uid.toString())
    }
     */

}

data class PointsPrestige(val userExperience: Long, val userPrestige: Long) {
    val experience = userExperience
    val prestige = userPrestige
}

