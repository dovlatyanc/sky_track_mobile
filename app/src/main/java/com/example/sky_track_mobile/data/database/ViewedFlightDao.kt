package com.example.sky_track_mobile.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ViewedFlightDao {
    @Query("SELECT * FROM viewed_flights ORDER BY timestamp DESC")
    fun getAllViewedFlights(): Flow<List<ViewedFlightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(viewedFlight: ViewedFlightEntity)

    @Query("DELETE FROM viewed_flights")
    suspend fun clearAll()

    @Query("SELECT EXISTS(SELECT 1 FROM viewed_flights WHERE flightKey = :flightKey)")
    suspend fun isViewed(flightKey: String): Boolean
}