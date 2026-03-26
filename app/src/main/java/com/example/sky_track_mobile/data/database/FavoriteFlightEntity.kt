package com.example.sky_track_mobile.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_flights")
data class FavoriteFlightEntity(
    @PrimaryKey
    val flightKey: String,
    val flightIata: String,
    val flightDate: String,
    val airlineName: String,
    val departureCode: String,
    val departureAirport: String,
    val departureTime: String?,
    val arrivalCode: String,
    val arrivalAirport: String,
    val arrivalTime: String?,
    val flightStatus: String,
    val timestamp: Long = System.currentTimeMillis()
)