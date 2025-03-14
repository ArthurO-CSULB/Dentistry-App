package com.start.pages

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Event(
    val eventID: String = "",
    val userID: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val time: String = ""
)
