package com.example.voyagetime.domain.repository

import com.example.voyagetime.data.local.entity.ReservationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for local reservation persistence (T2.3, T4.1, T4.2).
 * Abstracts Room DAO access from ViewModels following the MVVM architecture.
 */
interface ReservationRepository {

    /** T4.1 — Observe all reservations for the given user, ordered by creation date. */
    fun getAllReservations(userId: String): Flow<List<ReservationEntity>>

    /** Observe the reservation linked to a specific trip. */
    fun getReservationByTrip(tripId: Long, userId: String): Flow<ReservationEntity?>

    /** Returns true if the trip has at least one reservation. */
    suspend fun hasReservation(tripId: Long, userId: String): Boolean

    /** T2.3 — Persist a new reservation locally after a successful API booking. */
    suspend fun insertReservation(reservation: ReservationEntity): Long

    /** T4.2 — Delete a reservation by its local Room id. */
    suspend fun deleteReservation(reservationId: Long, userId: String)

    /** T4.2 — Delete a reservation by the API-assigned reservation id. */
    suspend fun deleteReservationByApiId(apiResId: String, userId: String)
}
