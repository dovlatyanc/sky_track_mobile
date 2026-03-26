package com.example.sky_track_mobile.data.repository

import com.example.sky_track_mobile.data.database.AppDatabase
import com.example.sky_track_mobile.data.database.FavoriteFlightEntity
import com.example.sky_track_mobile.data.database.ViewedFlightEntity
import com.example.sky_track_mobile.data.models.Flight
import com.example.sky_track_mobile.data.networks.AviationApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FlightRepository(
    private val api: AviationApi,
    private val apiKey: String,
    private val database: AppDatabase
) {
    suspend fun getFlights(
        limit: Int = 50,
        offset: Int = 0
    ): Result<List<Flight>> {
        return try {
            val response = api.getFlights(
                apiKey = apiKey,
                limit = limit,
                offset = offset
            )
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Просмотренные рейсы
    fun getViewedFlightsFlow(): Flow<List<ViewedFlightEntity>> {
        return database.viewedFlightDao().getAllViewedFlights()
    }

    suspend fun addToViewed(flight: Flight) {
        val flightKey = "${flight.flight?.iata}_${flight.flightDate}"
        val entity = ViewedFlightEntity(
            flightKey = flightKey,
            flightIata = flight.flight?.iata ?: "N/A",
            flightDate = flight.flightDate,
            airlineName = flight.airline?.name ?: "Unknown",
            departureCode = flight.departure?.iata ?: "N/A",
            arrivalCode = flight.arrival?.iata ?: "N/A",
            timestamp = System.currentTimeMillis()
        )
        database.viewedFlightDao().insertOrUpdate(entity)
    }

    suspend fun isViewed(flight: Flight): Boolean {
        val flightKey = "${flight.flight?.iata}_${flight.flightDate}"
        return database.viewedFlightDao().isViewed(flightKey)
    }

    // Избранные рейсы
    fun getFavoritesFlow(): Flow<List<FavoriteFlightEntity>> {
        return database.favoriteFlightDao().getAllFavorites()
    }

    suspend fun addToFavorites(flight: Flight) {
        val flightKey = "${flight.flight?.iata}_${flight.flightDate}"
        val entity = FavoriteFlightEntity(
            flightKey = flightKey,
            flightIata = flight.flight?.iata ?: "N/A",
            flightDate = flight.flightDate,
            airlineName = flight.airline?.name ?: "Unknown",
            departureCode = flight.departure?.iata ?: "N/A",
            departureAirport = flight.departure?.airport ?: "Unknown",
            departureTime = flight.departure?.scheduled?.let { parseTime(it) },
            arrivalCode = flight.arrival?.iata ?: "N/A",
            arrivalAirport = flight.arrival?.airport ?: "Unknown",
            arrivalTime = flight.arrival?.scheduled?.let { parseTime(it) },
            flightStatus = flight.flightStatus ?: "unknown",
            timestamp = System.currentTimeMillis()
        )
        database.favoriteFlightDao().addToFavorites(entity)
    }

    suspend fun removeFromFavorites(flight: Flight) {
        val flightKey = "${flight.flight?.iata}_${flight.flightDate}"
        database.favoriteFlightDao().removeByKey(flightKey)
    }

    suspend fun isFavorite(flight: Flight): Boolean {
        val flightKey = "${flight.flight?.iata}_${flight.flightDate}"
        return database.favoriteFlightDao().isFavorite(flightKey)
    }

    private fun parseTime(isoTime: String?): String {
        if (isoTime.isNullOrBlank()) return "--:--"
        return try {
            val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val timeFormat = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
            val date = format.parse(isoTime)
            if (date != null) timeFormat.format(date) else isoTime.takeLast(5)
        } catch (e: Exception) {
            isoTime.takeLast(5)
        }
    }
}