package com.example.sky_track_mobile.presentation.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sky_track_mobile.R
import com.example.sky_track_mobile.data.models.*
import com.example.sky_track_mobile.presentation.components.StatusChip
import com.example.sky_track_mobile.presentation.theme.Sky_track_mobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightDetailScreen(
    flight: Flight,
    onBack: () -> Unit,
    onMarkAsViewed: (Flight) -> Unit,
    isFavorite: Boolean = false,
    onToggleFavorite: (Flight) -> Unit = {}
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        onMarkAsViewed(flight)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.flight_info)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onToggleFavorite(flight) }) {
                        Icon(
                            if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = {
                        shareFlight(context, flight)
                    }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(R.string.share_flight)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = flight.airline?.name ?: stringResource(R.string.unknown_airline),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        StatusChip(status = flight.flightStatus ?: stringResource(R.string.unknown))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    InfoRow(
                        label = stringResource(R.string.flight_number),
                        value = flight.flight?.iata ?: stringResource(R.string.na)
                    )
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow(
                        label = stringResource(R.string.flight_date),
                        value = flight.flightDate ?: stringResource(R.string.na)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.route),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Text(
                        text = stringResource(R.string.departure),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    InfoRow(
                        label = stringResource(R.string.airport),
                        value = flight.departure?.airport ?: stringResource(R.string.unknown)
                    )
                    InfoRow(
                        label = stringResource(R.string.airport_code),
                        value = flight.departure?.iata ?: stringResource(R.string.na)
                    )
                    InfoRow(
                        label = stringResource(R.string.time),
                        value = parseTime(flight.departure?.scheduled)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.arrival),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    InfoRow(
                        label = stringResource(R.string.airport),
                        value = flight.arrival?.airport ?: stringResource(R.string.unknown)
                    )
                    InfoRow(
                        label = stringResource(R.string.airport_code),
                        value = flight.arrival?.iata ?: stringResource(R.string.na)
                    )
                    InfoRow(
                        label = stringResource(R.string.time),
                        value = parseTime(flight.arrival?.scheduled)
                    )
                }
            }

            flight.live?.let { live ->
                if (!live.is_ground && live.latitude != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.live_tracking),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                            InfoRow(
                                label = stringResource(R.string.latitude),
                                value = "${"%.4f".format(live.latitude)}°"
                            )
                            InfoRow(
                                label = stringResource(R.string.longitude),
                                value = "${"%.4f".format(live.longitude)}°"
                            )
                            live.speed_horizontal?.let {
                                InfoRow(
                                    label = stringResource(R.string.speed),
                                    value = "${it.toInt()} ${stringResource(R.string.km_h)}"
                                )
                            }
                            live.altitude?.let {
                                InfoRow(
                                    label = stringResource(R.string.altitude),
                                    value = "${it.toInt()} ${stringResource(R.string.meters)}"
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
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun shareFlight(context: android.content.Context, flight: Flight) {
    val shareText = buildString {
        appendLine("✈️ Flight: ${flight.flight?.iata ?: "N/A"}")
        appendLine("🏢 Airline: ${flight.airline?.name ?: "Unknown"}")
        appendLine("🛫 From: ${flight.departure?.airport ?: "Unknown"} (${flight.departure?.iata ?: "N/A"})")
        appendLine("🛬 To: ${flight.arrival?.airport ?: "Unknown"} (${flight.arrival?.iata ?: "N/A"})")
        appendLine("📅 Date: ${flight.flightDate ?: "Unknown"}")
        appendLine("📊 Status: ${flight.flightStatus ?: "Unknown"}")
        appendLine("\nShared via Sky Track ✈️")
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    context.startActivity(Intent.createChooser(shareIntent, "Share flight"))
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

@Preview(showBackground = true)
@Composable
private fun FlightDetailScreenPreview() {
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
        FlightDetailScreen(
            flight = mockFlight,
            onBack = {},
            onMarkAsViewed = {},
            isFavorite = false,
            onToggleFavorite = {}
        )
    }
}