package com.example.voyagetime.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.voyagetime.data.local.entity.ItineraryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItineraryItemDao {

    @Query(
        """
        SELECT * FROM itinerary_items
        WHERE trip_id = :tripId
        ORDER BY day_number ASC, scheduled_at ASC
        """
    )
    fun getItemsByTrip(tripId: Long): Flow<List<ItineraryItemEntity>>

    @Query(
        """
        SELECT * FROM itinerary_items
        WHERE trip_id = :tripId AND day_number = :dayNumber
        ORDER BY scheduled_at ASC
        """
    )
    fun getItemsByDay(tripId: Long, dayNumber: Int): Flow<List<ItineraryItemEntity>>

    @Query(
        """
        SELECT * FROM itinerary_items
        WHERE trip_id = :tripId AND day_number = :dayNumber AND UPPER(section) = UPPER(:section)
        ORDER BY scheduled_at ASC
        """
    )
    fun getItemsBySection(
        tripId: Long,
        dayNumber: Int,
        section: String
    ): Flow<List<ItineraryItemEntity>>

    @Query("SELECT * FROM itinerary_items WHERE id = :itemId LIMIT 1")
    suspend fun getItemById(itemId: Long): ItineraryItemEntity?

    @Query(
        """
        SELECT * FROM itinerary_items
        WHERE trip_id = :tripId
        ORDER BY day_number ASC, scheduled_at ASC
        """
    )
    suspend fun getItemsByTripOnce(tripId: Long): List<ItineraryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ItineraryItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<ItineraryItemEntity>)

    @Update
    suspend fun updateItem(item: ItineraryItemEntity)

    @Delete
    suspend fun deleteItem(item: ItineraryItemEntity)

    @Query("DELETE FROM itinerary_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: Long)

    @Query("DELETE FROM itinerary_items WHERE trip_id = :tripId")
    suspend fun deleteAllItemsForTrip(tripId: Long)

    @Query("DELETE FROM itinerary_items WHERE trip_id = :tripId AND day_number = :dayNumber")
    suspend fun deleteItemsForDay(tripId: Long, dayNumber: Int)
}