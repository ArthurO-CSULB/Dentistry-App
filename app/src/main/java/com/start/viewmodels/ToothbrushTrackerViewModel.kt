package com.start.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking

// Viewmodel that checks how long have they been using their toothbrush
class ToothbrushTrackerViewModel(): ViewModel() {

    // constants for faster querying
    val accounts = "accounts"

    // state holders for the toothbrush acquisition date
    private val _toothbrushGetDate = MutableStateFlow<Long?>(null)
    val toothbrushGetDate: StateFlow<Long?> get() = _toothbrushGetDate

    // Firebase auth and database session declarations
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Stores the date the user has replaced their toothbrush
    fun setToothbrushGetDate(getDate: Long?, replacementDate: Long?) {
        val userID = auth.currentUser?.uid.toString()
        runBlocking {
            if (getDate == null) db.collection(accounts).document(userID).update("getDate", getDate, "replacementDate", null)
            else db.collection(accounts).document(userID).update("getDate", getDate, "replacementDate", replacementDate)
        }
        _toothbrushGetDate.value = getDate
    }

    // get the toothbrush date stored in the datrabase
    fun getToothbrushGetDate() {
        val userId = auth.currentUser?.uid.toString()

        db.collection(accounts).document(userId).get().addOnSuccessListener { snapshot ->
            val time = snapshot.get("getDate") as Long?
            if (time != null) _toothbrushGetDate.value = time
        }
    }
}