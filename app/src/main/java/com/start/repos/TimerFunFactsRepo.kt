package com.start.repos


import android.content.Context
import androidx.compose.runtime.ProvidableCompositionLocal
import kotlin.collections.List
import kotlin.collections.arrayListOf
import kotlin.random.Random

// A repository for timer fun facts that will be passed into the TimerViewModel.
class TimerFunFactsRepo(private val context: Context) {
    // Variable that will store the array list of fun facts.
    private val funFacts = loadFunFacts()

    // Method to load the fun facts from the raw resource text file into an array list.
    private fun loadFunFacts(): List<String> {
        // Handle exceptions.
        try {
            // URL Reference:
            // * https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.io/
            // * Chat GPT for clean up.
            // * https://omahadentists.net/blog/interesting-teeth-and-dentistry-facts/ - original facts.
            // From the app resources, we will open a data stream for reading a raw resource
            // which contains the text file that stores all the fun facts for the tooth brush timer.
            return context.resources.openRawResource(com.example.dentalhygiene.R.raw.dental_fun_facts)
                // We will read the text file using a buffered reader
                .bufferedReader()
                // We take each line and put them in a list.
                // Closes file automatically
                .useLines {lines ->
                    // Filter out lines that are not too big for the UI
                    lines.filter { it.length <= 210 }
                        // Store it in a list.
                        .toList()
                }
        } catch(_: Exception) {}
        // ArrayList that contains fail
        return arrayListOf("Fail")
    }

    // Method to get a random fact from the list of facts.
    fun randomFact(): String {
        // Get a random index of the list.
        val randomIndex = Random.nextInt(0, funFacts.size)
        // Return the random fact.
        return funFacts[randomIndex]
    }
}