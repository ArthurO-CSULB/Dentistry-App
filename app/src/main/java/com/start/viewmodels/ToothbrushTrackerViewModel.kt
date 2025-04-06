package com.start.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

const val ACCOUNTS = "accounts"
const val THREE_MONTHS_IN_MILLS = 7889238000

// Viewmodel that checks how long have they been using their toothbrush
class ToothbrushTrackerViewModel(): ViewModel() {

    private val _toothbrushGetDate = MutableStateFlow<Long>(0)
    val toothbrushGetDate: StateFlow<Long> get() = _toothbrushGetDate

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Stores the date the user has replaced their toothbrush
    fun setToothbrushGetDate(time: Long) {
        val userID = auth.currentUser?.uid.toString()
        db.collection(ACCOUNTS).document(userID).update("toothbrushDate", time)
        _toothbrushGetDate.value = time
    }

    fun getToothbrushGetDate() {
        val userId = auth.currentUser?.uid.toString()

        db.collection(ACCOUNTS).document(userId).get().addOnSuccessListener { snapshot ->
            val time = snapshot.get("toothbrushDate") as Long
            _toothbrushGetDate.value = time
        }
    }
}