package com.start.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.start.Event
import java.util.UUID

class EventViewModel : ViewModel() {

    // We establish our authentication by getting an instance of the Firebase Authentication.
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> = _events

    // Load events.
    fun loadEvents() {
        val userID = auth.currentUser?.uid ?: return
        db.collection("accounts").document(userID).collection("events")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val eventList = snapshot.documents.mapNotNull { it.toObject(Event::class.java) }
                    _events.value = eventList
                }
            }
    }

    // Add event(s).
    fun addEvent(title: String, description: String, date: String, time: String) {
        val userID = auth.currentUser?.uid ?: return
        val eventID = UUID.randomUUID().toString()
        val combinedDate = "$date $time"
        val event = Event(eventID, title, description, combinedDate, time)

        db.collection("accounts").document(userID).collection("events")
            .document(eventID).set(event)
    }


    // Update an existing event.
    fun updateEvent(eventID: String, title: String, description: String, date: String, time: String) {
        val userID = auth.currentUser?.uid ?: return
        val event = Event(eventID, title, description, date, time)

        db.collection("accounts").document(userID).collection("events")
            .document(eventID).set(event)
    }

    // Delete an event
    fun deleteEvent(eventID: String) {
        val userID = auth.currentUser?.uid ?: return
        db.collection("accounts").document(userID).collection("events")
            .document(eventID).delete()
    }
}