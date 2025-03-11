package com.start.pages

data class BookmarkInformation(
    // Storage-relevant information
    val bookmarkID: String = "",
    val userID: String = "",

    // Clinic information
    val title: String = "",
    val address: String = "",

    // User-related information
    val rating: String = "",
    val review: String = ""
)
