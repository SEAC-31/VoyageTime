package com.example.voyagetime.domain.repository

import com.example.voyagetime.data.local.entity.TripImageEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for trip gallery images (T3.1, T3.2, T3.3).
 */
interface TripImageRepository {

    /** T3.3 — Observe all images attached to a specific trip. */
    fun observeImagesForTrip(tripId: Long, userId: String): Flow<List<TripImageEntity>>

    /** T3.1/T3.2 — Persist a batch of images for a trip. */
    suspend fun insertImages(images: List<TripImageEntity>): List<Long>

    /** Delete a single image by its Room id. */
    suspend fun deleteImage(imageId: Long, userId: String)
}
