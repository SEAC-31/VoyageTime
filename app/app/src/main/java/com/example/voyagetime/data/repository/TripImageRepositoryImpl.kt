package com.example.voyagetime.data.repository

import com.example.voyagetime.data.local.dao.TripImageDao
import com.example.voyagetime.data.local.entity.TripImageEntity
import com.example.voyagetime.domain.repository.TripImageRepository
import kotlinx.coroutines.flow.Flow

/**
 * Concrete implementation of [TripImageRepository] backed by Room (T3.1, T3.2, T3.3).
 */
class TripImageRepositoryImpl(
    private val tripImageDao: TripImageDao
) : TripImageRepository {

    override fun observeImagesForTrip(tripId: Long, userId: String): Flow<List<TripImageEntity>> =
        tripImageDao.observeImagesForTrip(tripId, userId)

    override suspend fun insertImages(images: List<TripImageEntity>): List<Long> =
        tripImageDao.insertImages(images)

    override suspend fun deleteImage(imageId: Long, userId: String) =
        tripImageDao.deleteImage(imageId, userId)
}
