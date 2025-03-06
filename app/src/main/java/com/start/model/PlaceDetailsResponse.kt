package com.start.model

// Data class file that is needed to parse the response from the Google Places API when fetching the
//clinic details
import com.google.gson.annotations.SerializedName

data class PlaceDetailsResponse(
    @SerializedName("result")val result: PlaceDetails?,
    @SerializedName("status") val status: String
)

// Data class for detailed clinic information
data class PlaceDetails(
    @SerializedName("place_id") val placeId: String,
    @SerializedName("name") val name: String,
    @SerializedName("formatted_address") val address: String?,
    @SerializedName("formatted_phone_number") val phoneNumber: String?,
    @SerializedName("rating") val rating: Float?,
    @SerializedName("user_ratings_total") val userRatingsTotal: Int?,
    @SerializedName("opening_hours") val openingHours: OpeningHours?,
    @SerializedName("photos") val photos: List<Photo>?
)

// Data class for opening hours
data class OpeningHours(
    @SerializedName("weekday_text") val weekdayText: List<String>?
)

// Data class for photo references (used to fetch images of the clinic)
data class Photo(
    @SerializedName("photo_reference") val photoReference: String
)
