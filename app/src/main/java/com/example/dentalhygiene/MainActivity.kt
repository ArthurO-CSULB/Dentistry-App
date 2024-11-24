package com.example.dentalhygiene

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.dentalhygiene.ui.theme.DentalHygieneTheme

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

// testing 123
class MainActivity : ComponentActivity() {
    // Initialize database connection.
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DentalHygieneTheme {
                // Greeting composable for the UI
                Greeting("Android")
            }
        }

        // Write to database
        writeData()

        // Read from database
        readData()

    }

    // Method to write data
    private fun writeData() {
        val user = hashMapOf(
            "name" to "Gabriel Villanueva",
            "age" to 22
        )

        db.collection("testcollection").document("testdocument3")
            .set(user)
            .addOnSuccessListener {
                Log.d("Firestore", "Document successfully written!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error writing document", e)
            }
    }

    private fun readData() {
        db.collection("testcollection").document("testdocument")
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("Firestore", "Document data: ${document.data}")
                } else {
                    Log.d("Firestore", "No such document")
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error reading document", e)
            }
    }
}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
            text = "Hello $name!",
            modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DentalHygieneTheme {
        Greeting("Android")
    }
}
