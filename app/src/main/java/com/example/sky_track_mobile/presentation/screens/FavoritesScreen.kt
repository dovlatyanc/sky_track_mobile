package com.example.sky_track_mobile.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sky_track_mobile.R
import com.example.sky_track_mobile.data.database.FavoriteFlightEntity
import com.example.sky_track_mobile.data.models.*
import com.example.sky_track_mobile.data.repository.FlightRepository
import com.example.sky_track_mobile.presentation.components.FlightCard
import com.example.sky_track_mobile.presentation.theme.Sky_track_mobileTheme
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
fun FavoritesScreen(
    repository: FlightRepository = koinInject()
) {
    var favoriteEntities by remember { mutableStateOf<List<FavoriteFlightEntity>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedFlight by remember { mutableStateOf<Flight?>(null) }
    val scope = rememberCoroutineScope()

    // Подписываемся на поток избранных рейсов
    val favoritesFlow = repository.getFavoritesFlow()
    val favorites by favoritesFlow.collectAsState(initial = emptyList())

    LaunchedEffect(Unit) {
        isLoading = false
    }

    // Преобразуем FavoriteFlightEntity в Flight для отображения
    val favoriteFlights = favorites.map { entity ->
        Flight(
            flightDate = entity.flightDate,
            flightStatus = entity.flightStatus,
            departure = Airport(
                airport = entity.departureAirport,
                timezone = null,
                iata = entity.departureCode,
                icao = null,
                terminal = null,
                gate = null,
                delay = null,
                scheduled = entity.departureTime,
                estimated = null,
                actual = null,
                estimatedRunway = null,
                actualRunway = null,
                baggage = null
            ),
            arrival = Airport(
                airport = entity.arrivalAirport,
                timezone = null,
                iata = entity.arrivalCode,
                icao = null,
                terminal = null,
                gate = null,
                delay = null,
                scheduled = entity.arrivalTime,
                estimated = null,
                actual = null,
                estimatedRunway = null,
                actualRunway = null,
                baggage = null
            ),
            airline = Airline(name = entity.airlineName, iata = null, icao = null),
            flight = FlightInfo(number = null, iata = entity.flightIata, icao = null, codeshared = null),
            aircraft = null,
            live = null
        )
    }

    // Функция удаления из избранного
    fun removeFromFavorites(flight: Flight) {
        scope.launch {
            repository.removeFromFavorites(flight)
        }
    }

    if (selectedFlight != null) {
        FlightDetailScreen(
            flight = selectedFlight!!,
            onBack = { selectedFlight = null },
            onMarkAsViewed = {},
            isFavorite = true,
            onToggleFavorite = { flight ->
                removeFromFavorites(flight)
                selectedFlight = null
            }
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = if (favoriteFlights.isEmpty()) Alignment.Center else Alignment.TopStart
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                favoriteFlights.isEmpty() -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text("⭐", fontSize = 64.sp)
                        Text(
                            text = "No favorites yet",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "Tap the heart icon on any flight to add it to favorites",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
                    ) {
                        items(
                            items = favoriteFlights,
                            key = { "${it.flight?.iata}_${it.flightDate}" }
                        ) { flight ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {

                                    FlightCard(
                                        flight = flight,
                                        isViewed = true,
                                        onClick = { selectedFlight = it },
                                        onShare = null,
                                        modifier = Modifier.weight(1f)
                                    )


                                    IconButton(
                                        onClick = { removeFromFavorites(flight) },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Remove from favorites",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    Sky_track_mobileTheme {
        FavoritesScreen()
    }
}