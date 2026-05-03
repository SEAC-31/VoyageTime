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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TripRepositoryImpl(
    private val tripDao: TripDao,
    private val authRepository: AuthRepository
) : TripRepository {

    override fun getAllTrips(): Flow<List<TripItem>> {
        val uid = currentUid() ?: return emptyFlow()
        return tripDao.getAllTrips(uid).map { it.map { e -> e.toTripItem() } }
    }

    override fun getUpcomingTrips(): Flow<List<TripItem>> {
        val uid = currentUid() ?: return emptyFlow()
        return tripDao.getUpcomingTrips(uid).map { it.map { e -> e.toTripItem() } }
    }

    override fun getPastTrips(): Flow<List<TripItem>> {
        val uid = currentUid() ?: return emptyFlow()
        return tripDao.getPastTrips(uid).map { it.map { e -> e.toTripItem() } }
    }

    override fun observeTrip(tripId: String): Flow<TripItem?> {
        val uid = currentUid() ?: return flowOf(null)
        val id  = tripId.toLongOrNull() ?: return flowOf(null)
        return tripDao.observeTripById(id, uid).map { it?.toTripItem() }
    }

    override suspend fun addTrip(newTrip: TripItem) {
        val uid = currentUid() ?: run { Log.e(TAG, "addTrip: no authenticated user"); return }
        val insertedId = tripDao.insertTrip(newTrip.toEntity(existingId = 0L, userId = uid))
        Log.i(TAG, "Trip inserted id=$insertedId destination=${newTrip.destination}")
    }

    override suspend fun updateTrip(updatedTrip: TripItem) {
        val uid = currentUid() ?: return
        val id  = updatedTrip.id.toLongOrNull() ?: run { Log.e(TAG, "updateTrip: invalid id"); return }
        tripDao.updateTrip(updatedTrip.toEntity(existingId = id, userId = uid))
        Log.i(TAG, "Trip updated id=$id")
    }

    override suspend fun deleteTrip(tripId: String) {
        val uid = currentUid() ?: return
        val id  = tripId.toLongOrNull() ?: run { Log.e(TAG, "deleteTrip: invalid id '$tripId'"); return }
        tripDao.deleteTripById(tripId = id, userId = uid)
        Log.i(TAG, "Trip deleted id=$tripId")
    }

    private var favoriteRegion = "Europe & North America"
    private var travelGoal     = "Complete memorable trips with clear itineraries"

    override fun getFavoriteRegion(): String = favoriteRegion
    override fun updateFavoriteRegion(newValue: String) { favoriteRegion = newValue }
    override fun getTravelGoal(): String = travelGoal
    override fun updateTravelGoal(newValue: String) { travelGoal = newValue }
    override fun getNextDeparture(): String = ""

    private fun currentUid(): String? = authRepository.currentUserId()

    private fun TripEntity.toTripItem(): TripItem {
        val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return TripItem(
            id          = id.toString(),
            destination = destination,
            country     = country,
            dateRange   = "${startDateTime.format(fmt)} - ${endDateTime.format(fmt)}",
            duration    = if (durationDays == 1) "1 day" else "$durationDays days",
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

    private fun TripItem.toEntity(existingId: Long, userId: String): TripEntity {
        val fmt    = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val parts  = dateRange.split(" - ").map { it.trim() }
        val startDT = parseToLocalDateTime(parts.getOrNull(0), fmt)
        val endDT   = parseToLocalDateTime(parts.getOrNull(1), fmt)
        val days    = duration.substringBefore(" ").trim().toIntOrNull() ?: 1
        val budgetVal = budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0

        return TripEntity(
            id            = existingId,
            userId        = userId,
            destination   = destination,
            country       = country,
            startDateTime = startDT,
            endDateTime   = endDT,
            durationDays  = days,
            budgetAmount  = budgetVal,
            statusLabel   = statusLabel,
            imageRes      = image
        )
    }

    private fun parseToLocalDateTime(value: String?, formatter: DateTimeFormatter): LocalDateTime {
        if (value.isNullOrBlank()) return LocalDate.now().atStartOfDay()
        return try {
            LocalDate.parse(value.trim(), formatter).atStartOfDay()
        } catch (_: DateTimeParseException) {
            LocalDate.now().atStartOfDay()
        }
    }

    companion object {
        private const val TAG = "TripRepository"
    }
}