package com.example.voyagetime.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.voyagetime.data.local.entity.TripEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {

    // ── Queries filtradas por usuario (T4.2) ──────────────────────────────────

    @Query("SELECT * FROM trips WHERE user_id = :userId ORDER BY start_datetime ASC")
    fun getAllTrips(userId: String): Flow<List<TripEntity>>

    @Query("""
        SELECT * FROM trips
        WHERE user_id = :userId AND UPPER(status_label) IN ('UPCOMING', 'PLANNED')
        ORDER BY start_datetime ASC
    """)
    fun getUpcomingTrips(userId: String): Flow<List<TripEntity>>

    @Query("""
        SELECT * FROM trips
        WHERE user_id = :userId AND UPPER(status_label) = 'COMPLETED'
        ORDER BY start_datetime DESC
    """)
    fun getPastTrips(userId: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId AND user_id = :userId LIMIT 1")
    fun observeTripById(tripId: Long, userId: String): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :tripId AND user_id = :userId LIMIT 1")
    suspend fun getTripById(tripId: Long, userId: String): TripEntity?

    @Query("SELECT * FROM trips WHERE user_id = :userId ORDER BY start_datetime ASC")
    suspend fun getAllTripsOnce(userId: String): List<TripEntity>

    // ── Mutations ─────────────────────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity): Long

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId AND user_id = :userId")
    suspend fun deleteTripById(tripId: Long, userId: String)

    @Query("DELETE FROM trips WHERE user_id = :userId")
    suspend fun deleteAllTripsForUser(userId: String)
}