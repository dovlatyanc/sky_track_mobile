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
    val departure: Airport?,
    val arrival: Airport?,
    val airline: Airline?,
    val flight: FlightInfo?,
    val aircraft: Aircraft?,
    val live: LiveInfo?
)

data class Airport(
    val airport: String?,
    val timezone: String?,
    val iata: String?,
    val icao: String?,
    val terminal: String?,
    val gate: String?,
    val delay: Int?,
    val scheduled: String?,
    val estimated: String?,
    val actual: String?,
    @SerializedName("estimated_runway") val estimatedRunway: String?,
    @SerializedName("actual_runway") val actualRunway: String?,
    val baggage: String?
) {

    constructor(
        iata: String?,
        scheduled: String?,
        airport: String?
    ) : this(
        airport = airport,
        timezone = null,
        iata = iata,
        icao = null,
        terminal = null,
        gate = null,
        delay = null,
        scheduled = scheduled,
        estimated = null,
        actual = null,
        estimatedRunway = null,
        actualRunway = null,
        baggage = null
    )
}

data class Airline(
    val name: String?,
    val iata: String?,
    val icao: String?
) {

    constructor(name: String) : this(
        name = name,
        iata = null,
        icao = null
    )
}

data class FlightInfo(
    val number: String?,
    val iata: String?,
    val icao: String?,
    val codeshared: Codeshared?
) {

    constructor(iata: String) : this(
        number = null,
        iata = iata,
        icao = null,
        codeshared = null
    )
}

data class Codeshared(
    @SerializedName("airline_name") val airlineName: String?,
    @SerializedName("airline_iata") val airlineIata: String?,
    @SerializedName("airline_icao") val airlineIcao: String?,
    @SerializedName("flight_number") val flightNumber: String?,
    @SerializedName("flight_iata") val flightIata: String?,
    @SerializedName("flight_icao") val flightIcao: String?
)

data class Aircraft(
    val registration: String?,
    val iata: String?,
    val icao: String?,
    val icao24: String?
)

data class LiveInfo(
    val updated: String?,
    val latitude: Double?,
    val longitude: Double?,
    val altitude: Double?,
    val direction: Double?,
    @SerializedName("speed_horizontal") val speed_horizontal: Double?,
    @SerializedName("speed_vertical") val speed_vertical: Double?,
    @SerializedName("is_ground") val is_ground: Boolean
)