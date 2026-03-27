package com.example.sky_track_mobile.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteFlightDao {
    @Query("SELECT * FROM favorite_flights ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteFlightEntity>>

    @Query("SELECT * FROM favorite_flights ORDER BY timestamp DESC")
    suspend fun getAllFavoritesList(): List<FavoriteFlightEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favorite: FavoriteFlightEntity)

    @Delete
    suspend fun removeFromFavorites(favorite: FavoriteFlightEntity)

    @Query("DELETE FROM favorite_flights WHERE flightKey = :flightKey")
    suspend fun removeByKey(flightKey: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_flights WHERE flightKey = :flightKey)")
    suspend fun isFavorite(flightKey: String): Boolean
}