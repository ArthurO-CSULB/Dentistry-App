package com.start

import com.google.gson.Gson
import com.start.model.PlacesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

//File for handling the nearby search function for the clinic search page
object PlacesApiService {
    suspend fun searchNearbyClinics(
        lat: Double,
        lng: Double,
        radius : Int,
        apiKey: String
    ): List<com.start.model.PlaceResult> {
        val urlString =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$lat,$lng&radius=$radius&type=dentist&key=$apiKey"

        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val response = Gson().fromJson(responseText, PlacesResponse::class.java)

                response.results
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // Return an empty list in case of failure
            }
        }
    }
}