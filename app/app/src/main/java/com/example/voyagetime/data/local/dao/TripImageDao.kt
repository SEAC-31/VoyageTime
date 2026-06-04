package com.example.voyagetime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.voyagetime.data.local.entity.TripImageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TripImageDao {

    @Query("""
        SELECT * FROM trip_images
        WHERE trip_id = :tripId AND user_id = :userId
        ORDER BY created_at DESC
    """)
    fun observeImagesForTrip(tripId: Long, userId: String): Flow<List<TripImageEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertImages(images: List<TripImageEntity>): List<Long>

    @Query("DELETE FROM trip_images WHERE id = :imageId AND user_id = :userId")
    suspend fun deleteImage(imageId: Long, userId: String)
}
