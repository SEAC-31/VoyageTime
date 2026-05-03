package com.example.voyagetime.data.repository

import android.util.Log
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.entity.TripEntity
import com.example.voyagetime.domain.repository.AuthRepository
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TripRepositoryImpl(
    private val tripDao: TripDao,
    private val authRepository: AuthRepository
) : TripRepository {

    // ── Queries ───────────────────────────────────────────────────────────────

    override fun getAllTrips(): Flow<List<TripItem>> {
        val uid = currentUid() ?: return emptyFlow()
        return tripDao.getAllTrips(uid).map { list -> list.map { it.toTripItem() } }
    }

    override fun getUpcomingTrips(): Flow<List<TripItem>> {
        val uid = currentUid() ?: return emptyFlow()
        return tripDao.getUpcomingTrips(uid).map { list -> list.map { it.toTripItem() } }
    }

    override fun getPastTrips(): Flow<List<TripItem>> {
        val uid = currentUid() ?: return emptyFlow()
        return tripDao.getPastTrips(uid).map { list -> list.map { it.toTripItem() } }
    }

    override fun observeTrip(tripId: String): Flow<TripItem?> {
        val uid = currentUid() ?: return emptyFlow()
        val id  = tripId.toLongOrNull() ?: return emptyFlow()
        return tripDao.observeTripById(id, uid).map { it?.toTripItem() }
    }

    // ── Mutations ─────────────────────────────────────────────────────────────

    override suspend fun addTrip(newTrip: TripItem) {
        val uid = currentUid() ?: run {
            Log.e(TAG, "addTrip: no authenticated user")
            return
        }
        val insertedId = tripDao.insertTrip(newTrip.toEntity(userId = uid))
        Log.i(TAG, "Trip inserted: id=$insertedId destination=${newTrip.destination}")
    }

    override suspend fun updateTrip(updatedTrip: TripItem) {
        val uid = currentUid() ?: return
        tripDao.updateTrip(updatedTrip.toEntity(userId = uid))
        Log.i(TAG, "Trip updated: id=${updatedTrip.id}")
    }

    override suspend fun deleteTrip(tripId: String) {
        val uid = currentUid() ?: return
        val id  = tripId.toLongOrNull() ?: run {
            Log.e(TAG, "deleteTrip: invalid id '$tripId'")
            return
        }
        tripDao.deleteTripById(tripId = id, userId = uid)
        Log.i(TAG, "Trip deleted: id=$tripId")
    }

    // ── Preferences (en memoria hasta T4.1 completo) ──────────────────────────

    private var favoriteRegion = "Europe & North America"
    private var travelGoal     = "Complete memorable trips with clear itineraries"

    override fun getFavoriteRegion(): String = favoriteRegion
    override fun updateFavoriteRegion(newValue: String) { favoriteRegion = newValue }
    override fun getTravelGoal(): String = travelGoal
    override fun updateTravelGoal(newValue: String) { travelGoal = newValue }
    override fun getNextDeparture(): String = ""

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun currentUid(): String? = authRepository.currentUserId()

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun TripEntity.toTripItem(): TripItem {
        val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return TripItem(
            id          = id.toString(),
            destination = destination,
            country     = country,
            dateRange   = "${startDateTime.format(fmt)} - ${endDateTime.format(fmt)}",
            duration    = "$durationDays days",
            budget      = "€$budgetAmount",
            statusLabel = statusLabel,
            state       = when (statusLabel.uppercase()) {
                "COMPLETED" -> TripState.COMPLETED
                "PLANNED"   -> TripState.PLANNED
                else        -> TripState.UPCOMING
            },
            image = imageRes
        )
    }

    private fun TripItem.toEntity(userId: String): TripEntity {
        val fmt    = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val parts  = dateRange.split(" - ").map { it.trim() }
        val startDT = parseDate(parts.getOrNull(0), fmt)
        val endDT   = parseDate(parts.getOrNull(1), fmt)
        val days    = duration.substringBefore(" ").toIntOrNull() ?: 1
        val budget  = budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0
        return TripEntity(
            id            = id.toLongOrNull() ?: 0L,
            userId        = userId,
            destination   = destination,
            country       = country,
            startDateTime = startDT,
            endDateTime   = endDT,
            durationDays  = days,
            budgetAmount  = budget,
            statusLabel   = statusLabel,
            imageRes      = image
        )
    }

    private fun parseDate(value: String?, fmt: DateTimeFormatter): LocalDateTime {
        if (value.isNullOrBlank()) return LocalDateTime.now()
        return try {
            java.time.LocalDate.parse(value, fmt).atStartOfDay()
        } catch (_: DateTimeParseException) {
            LocalDateTime.now()
        }
    }

    companion object {
        private const val TAG = "TripRepository"
    }
}