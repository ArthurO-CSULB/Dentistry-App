package com.start.repos

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

// Repo to connect to the database.
class PointsProgressionRepo(context: Context) {
    // Store references to the Firebase Authentication and Firestore instances.
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun test() {
        // Get the id of the current user.
        Log.d("Current User ID", auth.currentUser?.uid.toString())
    }

    // Method to Add points to the user's account
    fun addPoints(points: Long) {

        // Get the reference to the user's account using the current user's ID.
        val userAccount = getUserAccount()
        // Increment the user's experience by the points passed in to addPoints.
        userAccount.update("experience", FieldValue.increment(points))
            // Log the success.
            .addOnSuccessListener {
                Log.d("Adding Points", "Points added successfully")
            }
            // Log the failure
            .addOnFailureListener { e ->
                Log.w("Adding Points", "Error adding points", e)
            }
    }

    // Method to prestige the user's account
    fun prestige() {
        // Get the user's
        val userAccount = getUserAccount()
        // Increment the user's prestige by 1.
        userAccount.update("prestige", FieldValue.increment(1))
    }

    // Method to get the prestige of the user.
    fun getPrestige(onResult: (Long) -> Unit) {
        // Get the user's account.
        val userAccount = getUserAccount()
        // Get the prestige of the user.
        // Query the database.
        userAccount.get()
            .addOnSuccessListener { account ->
                // If the account contains prestige get prestige
                val prestige = if (account.contains("prestige")) {
                    account.getLong("prestige") ?: 0L
                }
                else {
                    userAccount.update("prestige", 0)
                    0L
                }
                // Call the callback with the prestige value
                onResult(prestige)
            }
            .addOnFailureListener {
                Log.d("Getting Prestige", "Error getting prestige. Account doesn't exist.")
                onResult(0L)
            }
    }


    // Method to return a reference to the user's account
    fun getUserAccount(): DocumentReference {
        return db.collection("accounts").document(auth.currentUser?.uid.toString())
    }

}

