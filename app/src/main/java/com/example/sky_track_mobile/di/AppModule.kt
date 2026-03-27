package com.example.sky_track_mobile.di

import android.content.Context
import com.example.sky_track_mobile.BuildConfig
import com.example.sky_track_mobile.data.database.AppDatabase
import com.example.sky_track_mobile.data.networks.AviationApi
import com.example.sky_track_mobile.data.networks.NetworkModule
import com.example.sky_track_mobile.data.repository.FlightRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single { NetworkModule.provideApi() }
    single { BuildConfig.AVIATIONSTACK_API_KEY }
    single { AppDatabase.getDatabase(androidContext()) }
    single { FlightRepository(get(), get(), get()) }
}