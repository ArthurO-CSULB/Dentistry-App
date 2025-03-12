package com.start

import android.util.Log
import com.google.gson.Gson
import com.start.model.PlaceDetailsResponse
import com.start.model.PlacesResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

// File for handling the nearby search function for the clinic search page
// and getting place details in the clinic details page
object PlacesApiService {
    suspend fun searchNearbyClinics(
        lat: Double,
        lng: Double,
        radius : Int,
        apiKey: String
    ): List<com.start.model.PlaceResult> {
        val urlString =
            "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=$lat,$lng&radius=$radius&type=dentist&&key=$apiKey"

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
    // Function to get details of a specific place using placeId
    suspend fun getPlaceDetails(
        placeId: String,
        apiKey: String
    ): com.start.model.PlaceDetails? {
        val urlString =
            "https://maps.googleapis.com/maps/api/place/details/json?place_id=$placeId&key=$apiKey"

        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val response = Gson().fromJson(responseText, PlaceDetailsResponse::class.java)

                response.result // Extract and return the PlaceDetails object
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error fetching place details: ${e.message}")
                null // Return null in case of failure
            }
        }
    }
}