package com.example.sky_track_mobile.data.networks

import com.example.sky_track_mobile.data.models.FlightResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AviationApi {
    @GET("v1/flights")
    suspend fun getFlights(
        @Query("access_key") apiKey: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0,
        @Query("arr_iata") arrivalIata: String? = null,
        @Query("dep_iata") departureIata: String? = null
    ): FlightResponse
}