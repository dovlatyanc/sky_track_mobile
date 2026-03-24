package com.example.sky_track_mobile.data.models

import com.google.gson.annotations.SerializedName

data class FlightResponse(
    val pagination: Pagination,
    val data: List<Flight>
)

data class Pagination(
    val limit: Int,
    val offset: Int,
    val count: Int,
    val total: Int
)

data class Flight(
    @SerializedName("flight_date") val flightDate: String,
    @SerializedName("flight_status") val flightStatus: String,
    val departure: AirportInfo,
    val arrival: AirportInfo,
    val airline: Airline,
    val flight: FlightInfo,
    val aircraft: Aircraft?,
    val live: LiveInfo?
)

data class AirportInfo(
    val airport: String,
    val timezone: String,
    val iata: String?,
    val icao: String?,
    val terminal: String?,
    val gate: String?,
    val delay: Int?,
    val scheduled: String,
    val estimated: String?,
    val actual: String?
)

data class Airline(
    val name: String,
    val iata: String?,
    val icao: String?
)

data class FlightInfo(
    val number: String,
    val iata: String,
    val icao: String
)

data class Aircraft(
    val registration: String?,
    val iata: String?,
    val icao: String?,
    val icao24: String?
)

data class LiveInfo(
    val updated: String,
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?,
    val direction: Double?,
    val speed_horizontal: Double?,
    val speed_vertical: Double?,
    val is_ground: Boolean
)