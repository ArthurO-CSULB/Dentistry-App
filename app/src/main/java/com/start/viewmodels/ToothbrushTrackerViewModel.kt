package com.start.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking

const val ACCOUNTS = "accounts"

// Viewmodel that checks how long have they been using their toothbrush
class ToothbrushTrackerViewModel(): ViewModel() {

    private val _toothbrushGetDate = MutableStateFlow<Long?>(null)
    val toothbrushGetDate: StateFlow<Long?> get() = _toothbrushGetDate

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Stores the date the user has replaced their toothbrush
    fun setToothbrushGetDate(getDate: Long?, replacementDate: Long?) {
        val userID = auth.currentUser?.uid.toString()
        runBlocking {
            db.collection(ACCOUNTS).document(userID).update("getDate", getDate, "replacementDate", replacementDate)
        }
        _toothbrushGetDate.value = getDate
    }

    fun getToothbrushGetDate() {
        val userId = auth.currentUser?.uid.toString()

        db.collection(ACCOUNTS).document(userId).get().addOnSuccessListener { snapshot ->
            val time = snapshot.get("getDate") as Long?
            if (time != null) _toothbrushGetDate.value = time
        }
    }
}