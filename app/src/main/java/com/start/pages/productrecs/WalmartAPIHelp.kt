package com.start.pages.productrecs

import com.android.volley.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// WalmartApiService.kt
interface WalmartApiService {
    @GET("v3/items")
    suspend fun searchItems(
        @Query("query") query: String,
        @Query("format") format: String = "json",
        @Header("Authorization") apiKey: String
    ): Response<WalmartSearchResponse>
}

// Classes
data class WalmartSearchResponse(
    val items: List<WalmartProduct>
)

data class WalmartProduct(
    val name: String,
    val salePrice: Double,
    val thumbnailImage: String,
    val productUrl: String
)