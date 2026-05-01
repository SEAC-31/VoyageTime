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

    @Query("SELECT * FROM trips ORDER BY start_datetime ASC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query(
        """
        SELECT * FROM trips
        WHERE UPPER(status_label) IN ('UPCOMING', 'PLANNED')
        ORDER BY start_datetime ASC
        """
    )
    fun getUpcomingTrips(): Flow<List<TripEntity>>

    @Query(
        """
        SELECT * FROM trips
        WHERE UPPER(status_label) = 'COMPLETED'
        ORDER BY start_datetime DESC
        """
    )
    fun getPastTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    fun observeTripById(tripId: Long): Flow<TripEntity?>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    suspend fun getTripById(tripId: Long): TripEntity?

    @Query("SELECT * FROM trips ORDER BY start_datetime ASC")
    suspend fun getAllTripsOnce(): List<TripEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity): Long

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("DELETE FROM trips WHERE id = :tripId")
    suspend fun deleteTripById(tripId: Long)

    @Query("DELETE FROM trips")
    suspend fun deleteAllTrips()
}