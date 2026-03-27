package com.example.sky_track_mobile.presentation.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sky_track_mobile.R
import com.example.sky_track_mobile.data.models.Flight
import com.example.sky_track_mobile.data.repository.FlightRepository
import com.example.sky_track_mobile.presentation.components.FlightCard
import com.example.sky_track_mobile.presentation.theme.Sky_track_mobileTheme
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightsScreen(
    repository: FlightRepository = koinInject()
) {
    val context = LocalContext.current
    var allFlights by remember { mutableStateOf<List<Flight>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedFlight by remember { mutableStateOf<Flight?>(null) }
    val scope = rememberCoroutineScope()


    val viewedFlights by repository.getViewedFlightsFlow().collectAsState(initial = emptyList())

    val favoriteFlights by repository.getFavoritesFlow().collectAsState(initial = emptyList())

    val errorUnknown = stringResource(R.string.error_unknown)
    val shareVia = stringResource(R.string.share_via)


    suspend fun markAsViewed(flight: Flight) {
        repository.addToViewed(flight)
    }

    // просмотрен ли рейс
    fun isViewed(flight: Flight): Boolean {
        val key = "${flight.flight?.iata}_${flight.flightDate}"
        return viewedFlights.any { it.flightKey == key }
    }

    //  в избранном ли рейс
    fun isFavorite(flight: Flight): Boolean {
        val key = "${flight.flight?.iata}_${flight.flightDate}"
        return favoriteFlights.any { it.flightKey == key }
    }

    //  просмотренные вверху
    fun sortFlightsWithViewedFirst(flights: List<Flight>): List<Flight> {
        val viewedKeys = viewedFlights.map { it.flightKey }.toSet()
        return flights.sortedByDescending { flight ->
            val key = "${flight.flight?.iata}_${flight.flightDate}"
            viewedKeys.contains(key)
        }
    }

    fun shareFlight(flight: Flight) {
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
        context.startActivity(Intent.createChooser(shareIntent, shareVia))
    }

    suspend fun toggleFavorite(flight: Flight) {
        if (isFavorite(flight)) {
            repository.removeFromFavorites(flight)
        } else {
            repository.addToFavorites(flight)
        }
    }


    LaunchedEffect(Unit) {
        isLoading = true
        loadFlights(repository) { result ->
            result.onSuccess {
                allFlights = it

            }
                .onFailure { error = it.localizedMessage ?: errorUnknown }
            isLoading = false
        }
    }

    if (selectedFlight != null) {
        FlightDetailScreen(
            flight = selectedFlight!!,
            onBack = { selectedFlight = null },
            onMarkAsViewed = { flight ->
                scope.launch {
                    markAsViewed(flight)
                }
            },
            isFavorite = isFavorite(selectedFlight!!),
            onToggleFavorite = { flight ->
                scope.launch {
                    toggleFavorite(flight)
                }
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.title_flights)) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                when {
                    isLoading && allFlights.isEmpty() -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    error != null -> {
                        ErrorView(
                            message = error!!,
                            onRetry = {
                                isLoading = true
                                error = null
                                scope.launch {
                                    loadFlights(repository) { result ->
                                        result.onSuccess { allFlights = it }
                                            .onFailure { error = it.localizedMessage ?: errorUnknown }
                                        isLoading = false
                                    }
                                }
                            }
                        )
                    }
                    else -> {
                        val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)
                        val sortedFlights = sortFlightsWithViewedFirst(allFlights)

                        SwipeRefresh(
                            state = swipeRefreshState,
                            onRefresh = {
                                isLoading = true
                                scope.launch {
                                    loadFlights(repository) { result ->
                                        result.onSuccess {
                                            allFlights = it
                                            // ✅ Просмотренные остаются в базе
                                        }
                                            .onFailure { error = it.localizedMessage ?: errorUnknown }
                                        isLoading = false
                                    }
                                }
                            }
                        ) {
                            LazyColumn(
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                if (sortedFlights.isEmpty()) {
                                    item {
                                        EmptyView()
                                    }
                                } else {
                                    items(
                                        items = sortedFlights,
                                        key = { "${it.flight?.iata}_${it.flightDate}" }
                                    ) { flight ->
                                        val isViewedFlag = isViewed(flight)
                                        val isFav = isFavorite(flight)

                                        FlightCard(
                                            flight = flight,
                                            isViewed = isViewedFlag,
                                            isFavorite = isFav,
                                            onClick = { selectedFlight = it },
                                            onShare = { shareFlight(it) }
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

private suspend fun loadFlights(
    repository: FlightRepository,
    onComplete: (Result<List<Flight>>) -> Unit
) {
    val result = repository.getFlights()
    onComplete(result)
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("⚠️", fontSize = 48.sp)
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.error
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(stringResource(R.string.error_retry))
        }
    }
}

@Composable
private fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🛫", fontSize = 48.sp)
        Text(
            text = stringResource(R.string.empty_flights),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = stringResource(R.string.empty_flights_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FlightsScreenPreview() {
    Sky_track_mobileTheme {
        FlightsScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorViewPreview() {
    Sky_track_mobileTheme {
        ErrorView("Network error", {})
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyViewPreview() {
    Sky_track_mobileTheme {
        EmptyView()
    }
}