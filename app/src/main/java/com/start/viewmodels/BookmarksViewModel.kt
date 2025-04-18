package com.start.viewmodels

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.start.pages.BookmarkInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime

class BookmarksViewModel : ViewModel() {
    // We establish our authentication by getting an instance of the Firebase Authentication.
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _bookmarks = MutableLiveData<List<BookmarkInformation>>()
    val bookmarks: LiveData<List<BookmarkInformation>> = _bookmarks

    // Function call when adding a bookmark
    @RequiresApi(Build.VERSION_CODES.O)
    fun addBookmark(clinicID: String, clinicName: String, rating: Float?) {
        // variables related to bookmark
        val userID = auth.currentUser?.uid.toString()
        val bookmarkRef = db.collection("bookmarks").document(userID).collection("clinicID")
        val bookmarkedDate = LocalDateTime.now().toString()

        // details in the database for bookmark
        val bookmarkDetails = hashMapOf(
            "clinicID" to clinicID,
            "clinicName" to clinicName,
            "ratingScore" to rating,
        )

        // Adding the bookmark to Firebase
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Use await() inside a coroutine
                bookmarkRef.document(clinicID).set(bookmarkDetails).await()
                Log.d("Bookmark", "Bookmark successfully added")
            } catch (e: Exception) {
                Log.e("Bookmark", "Failed to add bookmark: ${e.message}")
            }
        }
    }

    // Load bookmark(s).
    fun loadBookmarks() {
        val userID = auth.currentUser?.uid ?: return
        db.collection("bookmarks").document(userID).collection("clinicID")
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val bookmarkList = snapshot.documents.mapNotNull { it.toObject(
                        BookmarkInformation::class.java) }
                    _bookmarks.value = bookmarkList
                }
            }
    }

    // Delete bookmark(s)
    fun deleteBookmark(clinicID: String) {
        val userID = auth.currentUser?.uid ?: return
        db.collection("bookmarks").document(userID).collection("clinicID")
            .document(clinicID).delete()
    }
}
