package com.example.sky_track_mobile.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sky_track_mobile.R
import com.example.sky_track_mobile.data.models.*
import com.example.sky_track_mobile.presentation.theme.Sky_track_mobileTheme
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FlightCard(
    flight: Flight,
    modifier: Modifier = Modifier,
    isViewed: Boolean = false,
    isFavorite: Boolean = false,
    onClick: ((Flight) -> Unit)? = null,
    onShare: ((Flight) -> Unit)? = null
) {

    val cardColor = if (isViewed) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val iconColor = if (isViewed) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.primary
    }

    val textColor = if (isViewed) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val elevation = if (!isViewed) 4.dp else 2.dp

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick?.invoke(flight) },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Airline + Favorite + Share + Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = flight.airline?.name ?: stringResource(R.string.unknown_airline),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.weight(1f),
                    color = textColor
                )

                // Иконка избранного
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Favorite" else "Not favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.error else iconColor,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (onShare != null) {
                    IconButton(
                        onClick = { onShare(flight) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = stringResource(R.string.share_flight),
                            tint = iconColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                StatusChip(status = flight.flightStatus ?: stringResource(R.string.unknown))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AirportColumn(
                    code = flight.departure?.iata ?: stringResource(R.string.na),
                    time = parseTime(flight.departure?.scheduled),
                    name = flight.departure?.airport ?: stringResource(R.string.unknown),
                    isViewed = isViewed,
                    textColor = textColor
                )

                Column(
                    modifier = Modifier.width(80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Filled.AirplanemodeActive,
                        contentDescription = stringResource(R.string.flight_direction),
                        modifier = Modifier
                            .size(32.dp)
                            .rotate(90f),
                        tint = iconColor
                    )
                    Text(
                        text = flight.flight?.iata ?: stringResource(R.string.na),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp),
                        color = textColor
                    )
                }

                AirportColumn(
                    code = flight.arrival?.iata ?: stringResource(R.string.na),
                    time = parseTime(flight.arrival?.scheduled),
                    name = flight.arrival?.airport ?: stringResource(R.string.unknown),
                    isViewed = isViewed,
                    textColor = textColor
                )
            }

            flight.live?.let { live ->
                if (!live.is_ground && live.latitude != null && live.longitude != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        color = if (!isViewed) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.emoji_location),
                                fontSize = 12.sp,
                                color = textColor
                            )
                            Text(
                                text = "${stringResource(R.string.lat)}: ${"%.2f".format(live.latitude)}, " +
                                        "${stringResource(R.string.lon)}: ${"%.2f".format(live.longitude)}",
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f),
                                color = textColor
                            )
                            live.speed_horizontal?.let { speed ->
                                Text(
                                    text = "${speed.toInt()} ${stringResource(R.string.km_h)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = textColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AirportColumn(
    code: String,
    time: String,
    name: String,
    isViewed: Boolean = false,
    textColor: androidx.compose.ui.graphics.Color
) {
    Column(
        modifier = Modifier.width(100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = code,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (!isViewed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = time,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp),
            color = textColor
        )
        Text(
            text = name,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 2.dp),
            color = textColor
        )
    }
}

@Composable
fun StatusChip(status: String) {
    val (color, text) = when (status.lowercase()) {
        "active" -> MaterialTheme.colorScheme.primary to stringResource(R.string.status_active)
        "landed" -> MaterialTheme.colorScheme.tertiary to stringResource(R.string.status_landed)
        "cancelled" -> MaterialTheme.colorScheme.error to stringResource(R.string.status_cancelled)
        "scheduled" -> MaterialTheme.colorScheme.primary to stringResource(R.string.status_scheduled)
        "delayed" -> MaterialTheme.colorScheme.error to stringResource(R.string.status_delayed)
        else -> MaterialTheme.colorScheme.onSurfaceVariant to status.uppercase()
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

private fun parseTime(isoTime: String?): String {
    if (isoTime.isNullOrBlank()) return "--:--"
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = format.parse(isoTime)
        if (date != null) timeFormat.format(date) else isoTime.takeLast(5)
    } catch (e: Exception) {
        isoTime.takeLast(5)
    }
}

@Preview(showBackground = true)
@Composable
private fun FlightCardUnviewedPreview() {
    Sky_track_mobileTheme {
        val mockFlight = Flight(
            flightDate = "2024-03-27",
            flightStatus = "active",
            departure = Airport(
                airport = "Moscow Sheremetyevo",
                timezone = null,
                iata = "SVO",
                icao = null,
                terminal = null,
                gate = null,
                delay = null,
                scheduled = "2024-03-27T10:00:00",
                estimated = null,
                actual = null,
                estimatedRunway = null,
                actualRunway = null,
                baggage = null
            ),
            arrival = Airport(
                airport = "St Petersburg Pulkovo",
                timezone = null,
                iata = "LED",
                icao = null,
                terminal = null,
                gate = null,
                delay = null,
                scheduled = "2024-03-27T11:30:00",
                estimated = null,
                actual = null,
                estimatedRunway = null,
                actualRunway = null,
                baggage = null
            ),
            airline = Airline(name = "Aeroflot", iata = null, icao = null),
            flight = FlightInfo(number = null, iata = "SU1234", icao = null, codeshared = null),
            aircraft = null,
            live = null
        )
        FlightCard(
            flight = mockFlight,
            isViewed = false,
            isFavorite = false,
            onClick = {},
            onShare = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FlightCardViewedPreview() {
    Sky_track_mobileTheme {
        val mockFlight = Flight(
            flightDate = "2024-03-27",
            flightStatus = "landed",
            departure = Airport(
                airport = "Moscow Sheremetyevo",
                timezone = null,
                iata = "SVO",
                icao = null,
                terminal = null,
                gate = null,
                delay = null,
                scheduled = "2024-03-27T10:00:00",
                estimated = null,
                actual = null,
                estimatedRunway = null,
                actualRunway = null,
                baggage = null
            ),
            arrival = Airport(
                airport = "St Petersburg Pulkovo",
                timezone = null,
                iata = "LED",
                icao = null,
                terminal = null,
                gate = null,
                delay = null,
                scheduled = "2024-03-27T11:30:00",
                estimated = null,
                actual = null,
                estimatedRunway = null,
                actualRunway = null,
                baggage = null
            ),
            airline = Airline(name = "Aeroflot", iata = null, icao = null),
            flight = FlightInfo(number = null, iata = "SU1234", icao = null, codeshared = null),
            aircraft = null,
            live = null
        )
        FlightCard(
            flight = mockFlight,
            isViewed = true,
            isFavorite = true,
            onClick = {},
            onShare = {}
        )
    }
}