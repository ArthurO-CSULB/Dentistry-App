package com.start.pages

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class BookmarksViewModel : ViewModel() {
    // We establish our authentication by getting an instance of the Firebase Authentication.
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _bookmarks = MutableLiveData<List<BookmarkInformation>>()
    val bookmarks: LiveData<List<BookmarkInformation>> = _bookmarks

    // For the purposes of demo-ing
    // Load placeholder bookmarks
    init {
        loadSampleBookmarks()
    }

    // Load placeholder bookmarks
    private fun loadSampleBookmarks() {
        val sampleBookmarks = listOf(
            BookmarkInformation("1", "1gIEhMTuCVSQBen3iufV9zT6P6e2", "Bright Smile Dental", "123 Main St, Long Beach", "4.5", "Great service!"),
            BookmarkInformation("2", "1gIEhMTuCVSQBen3iufV9zT6P6e2", "Happy Teeth Clinic", "456 Elm St, Long Beach", "4.8", "Friendly staff and clean office."),
            BookmarkInformation("3", "1gIEhMTuCVSQBen3iufV9zT6P6e2", "Pearl Dental Care", "789 Oak Ave, Long Beach", "4.2", "Affordable and efficient."),
        )
        _bookmarks.value = sampleBookmarks
    }


    // Load bookmark(s).
    fun loadBookmarks() {
        val userID = auth.currentUser?.uid ?: return
        db.collection("accounts").document(userID).collection("bookmarks")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val bookmarkList = snapshot.documents.mapNotNull { it.toObject(BookmarkInformation::class.java) }
                    _bookmarks.value = bookmarkList
                }
            }
    }
}
