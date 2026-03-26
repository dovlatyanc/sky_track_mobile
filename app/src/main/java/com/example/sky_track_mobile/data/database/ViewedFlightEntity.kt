package com.example.sky_track_mobile.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "viewed_flights")
data class ViewedFlightEntity(
    @PrimaryKey
    val flightKey: String, // Уникальный ключ: iata + дата
    val flightIata: String,
    val flightDate: String,
    val airlineName: String,
    val departureCode: String,
    val arrivalCode: String,
    val timestamp: Long = System.currentTimeMillis()
)