package com.start.model

import com.google.gson.annotations.SerializedName

// A data class file for handling Places data attributes
data class PlacesResponse(
    val results: List<PlaceResult>
)

data class PlaceResult(
    @SerializedName("name") val name: String,
    @SerializedName("geometry") val geometry: Geometry,
    @SerializedName("formatted_address") val address: String?, // "vicinity" for nearby search, "formatted_address" for details
    @SerializedName("place_id") val placeId: String?
)

data class Geometry(
    val location: LocationLatLng
)

data class LocationLatLng(
    val lat: Double,
    val lng: Double
)