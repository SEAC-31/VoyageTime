package com.example.voyagetime.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.voyagetime.data.local.entity.ReservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    // T4.1 — Todas las reservas del usuario, ordenadas por fecha de creación
    @Query("SELECT * FROM reservations WHERE user_id = :userId ORDER BY created_at DESC")
    fun getAllReservations(userId: String): Flow<List<ReservationEntity>>

    // T4.1 — Reserva vinculada a un viaje concreto
    @Query("SELECT * FROM reservations WHERE trip_id = :tripId AND user_id = :userId LIMIT 1")
    fun getReservationByTrip(tripId: Long, userId: String): Flow<ReservationEntity?>

    // T4.4 — Saber si un viaje tiene reserva (para My Trips)
    @Query("SELECT COUNT(*) FROM reservations WHERE trip_id = :tripId AND user_id = :userId")
    suspend fun hasReservation(tripId: Long, userId: String): Int

    // T2.3 — Guardar reserva localmente tras confirmar con la API
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity): Long

    // T4.2 — Borrar reserva local
    @Query("DELETE FROM reservations WHERE id = :reservationId AND user_id = :userId")
    suspend fun deleteReservation(reservationId: Long, userId: String)

    // T4.2 — Borrar por ID de reserva de la API
    @Query("DELETE FROM reservations WHERE api_reservation_id = :apiResId AND user_id = :userId")
    suspend fun deleteReservationByApiId(apiResId: String, userId: String)
}