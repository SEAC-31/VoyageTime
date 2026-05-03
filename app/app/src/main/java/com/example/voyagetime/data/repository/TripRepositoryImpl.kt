package com.example.voyagetime.data.repository

import android.util.Log
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.entity.TripEntity
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TripRepositoryImpl(
    private val tripDao: TripDao
) : TripRepository {

    override fun getAllTrips(): Flow<List<TripItem>> {
        return tripDao.getAllTrips().map { trips ->
            trips.map { it.toTripItem() }
        }
    }

    override fun getUpcomingTrips(): Flow<List<TripItem>> {
        return tripDao.getUpcomingTrips().map { trips ->
            trips.map { it.toTripItem() }
        }
    }

    override fun getPastTrips(): Flow<List<TripItem>> {
        return tripDao.getPastTrips().map { trips ->
            trips.map { it.toTripItem() }
        }
    }

    override fun observeTrip(tripId: String): Flow<TripItem?> {
        val id = tripId.toLongOrNull() ?: return flowOf(null)

        return tripDao.observeTripById(id).map { trip ->
            trip?.toTripItem()
        }
    }

    override suspend fun addTrip(newTrip: TripItem) {
        val insertedId = tripDao.insertTrip(newTrip.toEntity(existingId = 0L))
        Log.i(TAG, "Trip inserted with id=$insertedId: ${newTrip.destination}")
    }

    override suspend fun updateTrip(updatedTrip: TripItem) {
        val id = updatedTrip.id.toLongOrNull()

        if (id == null) {
            Log.e(TAG, "updateTrip: invalid trip id '${updatedTrip.id}'")
            return
        }

        tripDao.updateTrip(updatedTrip.toEntity(existingId = id))
        Log.i(TAG, "Trip updated with id=$id")
    }

    override suspend fun deleteTrip(tripId: String) {
        val id = tripId.toLongOrNull()

        if (id == null) {
            Log.e(TAG, "deleteTrip: invalid trip id '$tripId'")
            return
        }

        tripDao.deleteTripById(id)
        Log.i(TAG, "Trip deleted with id=$id")
    }

    private var favoriteRegion: String = "Europe & North America"
    private var travelGoal: String = "Complete memorable trips with clear itineraries"

    override fun getFavoriteRegion(): String = favoriteRegion

    override fun updateFavoriteRegion(newValue: String) {
        favoriteRegion = newValue
        Log.i(TAG, "Favorite region updated")
    }

    override fun getTravelGoal(): String = travelGoal

    override fun updateTravelGoal(newValue: String) {
        travelGoal = newValue
        Log.i(TAG, "Travel goal updated")
    }

    override fun getNextDeparture(): String = ""

    private fun TripEntity.toTripItem(): TripItem {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val start = startDateTime.format(formatter)
        val end = endDateTime.format(formatter)

        return TripItem(
            id = id.toString(),
            destination = destination,
            country = country,
            dateRange = "$start - $end",
            duration = if (durationDays == 1) "1 day" else "$durationDays days",
            budget = "€$budgetAmount",
            statusLabel = statusLabel,
            state = when (statusLabel.uppercase()) {
                "COMPLETED" -> TripState.COMPLETED
                "PLANNED" -> TripState.PLANNED
                else -> TripState.UPCOMING
            },
            image = imageRes,
            coverImageUri = coverImageUri
        )
    }

    private fun TripItem.toEntity(existingId: Long): TripEntity {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val parts = dateRange.split(" - ").map { it.trim() }

        val startDateTime = parseToLocalDateTime(parts.getOrNull(0), dateFormatter)
        val endDateTime = parseToLocalDateTime(parts.getOrNull(1), dateFormatter)
        val days = duration.substringBefore(" ").trim().toIntOrNull() ?: 1
        val budgetValue = budget
            .replace("€", "")
            .replace(",", "")
            .trim()
            .toIntOrNull() ?: 0

        return TripEntity(
            id = existingId,
            destination = destination,
            country = country,
            startDateTime = startDateTime,
            endDateTime = endDateTime,
            durationDays = days,
            budgetAmount = budgetValue,
            statusLabel = statusLabel,
            imageRes = image,
            coverImageUri = coverImageUri
        )
    }

    private fun parseToLocalDateTime(
        value: String?,
        formatter: DateTimeFormatter
    ): LocalDateTime {
        if (value.isNullOrBlank()) {
            return LocalDate.now().atStartOfDay()
        }

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