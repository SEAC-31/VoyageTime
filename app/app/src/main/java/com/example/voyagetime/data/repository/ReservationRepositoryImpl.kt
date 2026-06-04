package com.example.voyagetime.data.repository

import com.example.voyagetime.data.local.dao.ReservationDao
import com.example.voyagetime.data.local.entity.ReservationEntity
import com.example.voyagetime.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow

/**
 * Concrete implementation of [ReservationRepository] backed by Room (T2.3, T4.1, T4.2).
 */
class ReservationRepositoryImpl(
    private val reservationDao: ReservationDao
) : ReservationRepository {

    override fun getAllReservations(userId: String): Flow<List<ReservationEntity>> =
        reservationDao.getAllReservations(userId)

    override fun getReservationByTrip(tripId: Long, userId: String): Flow<ReservationEntity?> =
        reservationDao.getReservationByTrip(tripId, userId)

    override suspend fun hasReservation(tripId: Long, userId: String): Boolean =
        reservationDao.hasReservation(tripId, userId) > 0

    override suspend fun insertReservation(reservation: ReservationEntity): Long =
        reservationDao.insertReservation(reservation)

    override suspend fun deleteReservation(reservationId: Long, userId: String) =
        reservationDao.deleteReservation(reservationId, userId)

    override suspend fun deleteReservationByApiId(apiResId: String, userId: String) =
        reservationDao.deleteReservationByApiId(apiResId, userId)
}
