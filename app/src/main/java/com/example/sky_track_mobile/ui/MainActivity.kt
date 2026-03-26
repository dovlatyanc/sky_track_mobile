package com.example.sky_track_mobile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.sky_track_mobile.R
import com.example.sky_track_mobile.presentation.screens.FavoritesScreen
import com.example.sky_track_mobile.presentation.screens.FlightsScreen
import com.example.sky_track_mobile.presentation.theme.Sky_track_mobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Sky_track_mobileTheme {
                SkyTrackApp()
            }
        }
    }
}

@Composable
fun SkyTrackApp() {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Flight, contentDescription = stringResource(R.string.nav_flights)) },
                    label = { Text(stringResource(R.string.nav_flights)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = stringResource(R.string.nav_favorites)) },
                    label = { Text(stringResource(R.string.nav_favorites)) }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                0 -> FlightsScreen()
                1 -> FavoritesScreen()
            }
        }
    }
}