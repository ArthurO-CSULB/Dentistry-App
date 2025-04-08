package com.start.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import android.util.Log
import com.start.model.PlaceDetails

class ClinicDetailsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    fun addClinicToDb(clinic: PlaceDetails) {
        viewModelScope.launch {
            val clinicData = hashMapOf(
                "name" to clinic.name,
                "address" to clinic.address,
                "phoneNumber" to clinic.phoneNumber,
            )

            db.collection("clinics").document(clinic.placeId).set(clinicData)
                .addOnSuccessListener {
                    Log.d("Firebase", "Clinic added successfully: ${clinic.name}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase", "Error adding clinic", e)
                }
        }
    }
}
