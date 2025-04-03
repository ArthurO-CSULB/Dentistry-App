package com.start.repos

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges

// Repo to connect to the database.
class PointsProgressionRepo(context: Context) {
    // Store references to the Firebase Authentication and Firestore instances.
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Initialize a reference to the user's account.
    private var userAccount: DocumentReference? = null

    // Initialize a listener, that when the user logs in, create a reference and listener to the user's account.
    init {
        auth.addAuthStateListener { firebaseAuth ->
            // If there is a user logged in.
            if (firebaseAuth.currentUser != null) {
                // Create reference to user's account with snapshot listener
                userAccount = getUserAccount()
            }
            // If there is no user logged in, remove account reference.
            else {
                // Set account reference to null
                userAccount = null
            }
        }
    }

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


    // Method to return a reference to the user's account
    private fun getUserAccount(): DocumentReference {
        return db.collection("accounts").document(auth.currentUser?.uid.toString())
    }

}

