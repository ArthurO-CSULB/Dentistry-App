package com.start.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


// Implementation of Dental Routine System to be used by the Dental Routine Page
class DentalRoutineViewModel: ViewModel() {

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    private val _morningTime = MutableStateFlow<Int?>(null)
    val morningTime: StateFlow<Int?> get() = _morningTime

    private val _afternoonTime = MutableStateFlow<Int?>(null)
    val afternoonTime: StateFlow<Int?> get() = _afternoonTime

    private val _eveningTime = MutableStateFlow<Int?>(null)
    val eveningTime: StateFlow<Int?> get() = _eveningTime

    fun setBrushingTimes(morningTime: Int, afternoonTime: Int, eveningTime: Int) {

    }
}