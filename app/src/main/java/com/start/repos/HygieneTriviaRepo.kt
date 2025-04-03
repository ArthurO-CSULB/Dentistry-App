package com.start.repos

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


// Class to store the question, choices, and answer from the loaded JSON containing the QnA.
class HygieneTriviaRepo(private val context: Context) {
    // Load the trivia on creation.
    private val hygieneTrivia: List<DentalTriviaQnA> = loadHygieneTrivia()

    // We define a data class to hold the question, choices, and answer from the loaded JSON containing the QnA.
    data class DentalTriviaQnA(val question: String, val choices: List<String>, val answer: String)

    // Method to load the trivia. Returns a list of DentalTriviaQnA objects.
    private fun loadHygieneTrivia(): List<DentalTriviaQnA> {
        // Open the JSON file from the assets folder and read the text.
        val triviaJson = context.assets.open("dental_trivia.json").bufferedReader().use { it.readText() }
        // Create a TypeToken object which tells Gson how to parse the a list of DentalTriviaQnA
        // objects from the JSON file.
        val type = object : TypeToken<List<DentalTriviaQnA>>() {}.type
        // return the parsed list of DentalTriviaQnA objects.
        return Gson().fromJson(triviaJson, type)
    }

    // Method to return a random set of five QnAs for the user to answer.
    fun randomQuestions(): List<DentalTriviaQnA> {
        // Shuffle the list of QnAs and return the first five.
        return hygieneTrivia.shuffled().take(5)
    }
}