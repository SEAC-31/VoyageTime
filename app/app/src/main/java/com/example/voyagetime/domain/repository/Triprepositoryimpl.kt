package com.example.voyagetime.data.repository

import android.util.Log
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.entity.TripEntity
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TripRepositoryImpl(
    private val tripDao: TripDao
) : TripRepository {

    // ── Queries ───────────────────────────────────────────────────────────────

    override fun getAllTrips(): Flow<List<TripItem>> =
        tripDao.getAllTrips().map { list -> list.map { it.toTripItem() } }

    override fun getUpcomingTrips(): Flow<List<TripItem>> =
        tripDao.getUpcomingTrips().map { list -> list.map { it.toTripItem() } }

    override fun getPastTrips(): Flow<List<TripItem>> =
        tripDao.getPastTrips().map { list -> list.map { it.toTripItem() } }

    // ── Mutations ─────────────────────────────────────────────────────────────

    override suspend fun addTrip(newTrip: TripItem) {
        val entity = newTrip.toEntity()
        val insertedId = tripDao.insertTrip(entity)
        Log.i(TAG, "Trip inserted with id=$insertedId: ${newTrip.destination}")
    }

    override suspend fun updateTrip(updatedTrip: TripItem) {
        val entity = updatedTrip.toEntity()
        tripDao.updateTrip(entity)
        Log.i(TAG, "Trip updated: id=${updatedTrip.id}, destination=${updatedTrip.destination}")
    }

    override suspend fun deleteTrip(tripId: String) {
        val id = tripId.toLongOrNull()
        if (id == null) {
            Log.e(TAG, "deleteTrip: invalid tripId format '$tripId'")
            return
        }
        tripDao.deleteTripById(id)
        Log.i(TAG, "Trip deleted: id=$tripId")
    }

    // ── Preferences (stored in-memory / SharedPreferences — not in Room) ──────
    // Estas preferencias no son entidades de BD. En T4.x se migrarán a la tabla de usuario.

    private var favoriteRegion: String = "Europe & North America"
    private var travelGoal: String = "Complete memorable trips with clear itineraries"

    override fun getFavoriteRegion(): String = favoriteRegion
    override fun updateFavoriteRegion(newValue: String) {
        favoriteRegion = newValue
        Log.i(TAG, "Favorite region updated: $newValue")
    }

    override fun getTravelGoal(): String = travelGoal
    override fun updateTravelGoal(newValue: String) {
        travelGoal = newValue
        Log.i(TAG, "Travel goal updated: $newValue")
    }

    override fun getNextDeparture(): String {
        // No podemos hacer una query síncrona aquí sin bloquear el hilo.
        // En T1.5 el ViewModel derivará este valor del Flow de getUpcomingTrips().
        // Devolvemos string vacío como fallback temporal.
        return ""
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun TripEntity.toTripItem(): TripItem {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val startStr = startDateTime.format(dateFormatter)
        val endStr = endDateTime.format(dateFormatter)

        return TripItem(
            id = id.toString(),
            destination = destination,
            country = country,
            dateRange = "$startStr - $endStr",
            duration = "$durationDays days",
            budget = "€$budgetAmount",
            statusLabel = statusLabel,
            state = when (statusLabel.uppercase()) {
                "COMPLETED" -> TripState.COMPLETED
                "PLANNED"   -> TripState.PLANNED
                else        -> TripState.UPCOMING
            },
            image = imageRes
        )
    }

    private fun TripItem.toEntity(): TripEntity {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val parts = dateRange.split(" - ").map { it.trim() }

        val startDT = parseToLocalDateTime(parts.getOrNull(0), dateFormatter)
        val endDT   = parseToLocalDateTime(parts.getOrNull(1), dateFormatter)
        val days    = duration.substringBefore(" ").toIntOrNull() ?: 1
        val budgetVal = budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0

        return TripEntity(
            id = id.toLongOrNull() ?: 0L,
            destination = destination,
            country = country,
            startDateTime = startDT,
            endDateTime = endDT,
            durationDays = days,
            budgetAmount = budgetVal,
            statusLabel = statusLabel,
            imageRes = image
        )
    }

    private fun parseToLocalDateTime(value: String?, formatter: DateTimeFormatter): LocalDateTime {
        if (value.isNullOrBlank()) return LocalDateTime.now()
        return try {
            val date = java.time.LocalDate.parse(value, formatter)
            date.atStartOfDay()
        } catch (_: DateTimeParseException) {
            LocalDateTime.now()
        }
    }

    companion object {
        private const val TAG = "TripRepository"
    }
}