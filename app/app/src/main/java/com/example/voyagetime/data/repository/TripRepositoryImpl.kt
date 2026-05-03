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
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
) : TripRepository {

    override fun getAllTrips(): Flow<List<TripItem>> {
        val uid = currentUid() ?: run {
            Log.e(TAG, "getAllTrips: no authenticated user")
            return emptyFlow()
        }

        Log.i(TAG, "Observing all trips for uid=$uid")
        return tripDao.getAllTrips(uid).map { trips -> trips.map { it.toTripItem() } }
    }

    override fun getUpcomingTrips(): Flow<List<TripItem>> {
        val uid = currentUid() ?: run {
            Log.e(TAG, "getUpcomingTrips: no authenticated user")
            return emptyFlow()
        }

        Log.i(TAG, "Observing upcoming trips for uid=$uid")
        return tripDao.getUpcomingTrips(uid).map { trips -> trips.map { it.toTripItem() } }
    }

    override fun getPastTrips(): Flow<List<TripItem>> {
        val uid = currentUid() ?: run {
            Log.e(TAG, "getPastTrips: no authenticated user")
            return emptyFlow()
        }

        Log.i(TAG, "Observing past trips for uid=$uid")
        return tripDao.getPastTrips(uid).map { trips -> trips.map { it.toTripItem() } }
    }

    override fun observeTrip(tripId: String): Flow<TripItem?> {
        val uid = currentUid() ?: run {
            Log.e(TAG, "observeTrip: no authenticated user")
            return flowOf(null)
        }
        val id = tripId.toLongOrNull() ?: run {
            Log.e(TAG, "observeTrip: invalid trip id '$tripId'")
            return flowOf(null)
        }

        Log.i(TAG, "Observing trip id=$id uid=$uid")
        return tripDao.observeTripById(id, uid).map { trip -> trip?.toTripItem() }
    }

    override suspend fun addTrip(newTrip: TripItem) {
        val uid = currentUid() ?: run {
            Log.e(TAG, "addTrip: no authenticated user")
            return
        }

        try {
            if (tripDao.isTripDestinationTakenForUser(uid, newTrip.destination)) {
                Log.e(TAG, "addTrip: duplicate destination for user uid=$uid destination=${newTrip.destination}")
                return
            }

            val insertedId = tripDao.insertTrip(newTrip.toEntity(existingId = 0L, userId = uid))
            Log.i(TAG, "Trip inserted id=$insertedId destination=${newTrip.destination} uid=$uid")
        } catch (error: Exception) {
            Log.e(TAG, "addTrip: database insert failed destination=${newTrip.destination} uid=$uid", error)
            throw error
        }
    }

    override suspend fun updateTrip(updatedTrip: TripItem) {
        val uid = currentUid() ?: run {
            Log.e(TAG, "updateTrip: no authenticated user")
            return
        }
        val id = updatedTrip.id.toLongOrNull() ?: run {
            Log.e(TAG, "updateTrip: invalid trip id '${updatedTrip.id}'")
            return
        }

        try {
            if (tripDao.isTripDestinationTakenForUser(uid, updatedTrip.destination, excludeId = id)) {
                Log.e(TAG, "updateTrip: duplicate destination for user uid=$uid destination=${updatedTrip.destination}")
                return
            }

            tripDao.updateTrip(updatedTrip.toEntity(existingId = id, userId = uid))
            Log.i(TAG, "Trip updated id=$id uid=$uid")
        } catch (error: Exception) {
            Log.e(TAG, "updateTrip: database update failed id=$id uid=$uid", error)
            throw error
        }
    }

    override suspend fun deleteTrip(tripId: String) {
        val uid = currentUid() ?: run {
            Log.e(TAG, "deleteTrip: no authenticated user")
            return
        }
        val id = tripId.toLongOrNull() ?: run {
            Log.e(TAG, "deleteTrip: invalid trip id '$tripId'")
            return
        }

        try {
            tripDao.deleteTripById(tripId = id, userId = uid)
            Log.i(TAG, "Trip deleted id=$id uid=$uid")
        } catch (error: Exception) {
            Log.e(TAG, "deleteTrip: database delete failed id=$id uid=$uid", error)
            throw error
        }
    }

    override suspend fun isTripDestinationTaken(destination: String, excludeTripId: String?): Boolean {
        val uid = currentUid() ?: run {
            Log.e(TAG, "isTripDestinationTaken: no authenticated user")
            return false
        }

        val excludeId = excludeTripId?.toLongOrNull() ?: 0L

        return try {
            val taken = tripDao.isTripDestinationTakenForUser(
                userId = uid,
                destination = destination.trim(),
                excludeId = excludeId
            )

            Log.i(TAG, "Trip duplicate validation uid=$uid destination=${destination.trim()} excludeId=$excludeId taken=$taken")
            taken
        } catch (error: Exception) {
            Log.e(TAG, "isTripDestinationTaken: database validation failed uid=$uid destination=${destination.trim()}", error)
            false
        }
    }

    private var favoriteRegion: String = "Europe & North America"
    private var travelGoal: String = "Complete 4 memorable trips with clear itineraries"

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

    private fun currentUid(): String? = authRepository.currentUserId()

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

    private fun TripItem.toEntity(existingId: Long, userId: String): TripEntity {
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
            userId = userId,
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
        } catch (error: DateTimeParseException) {
            Log.e(TAG, "parseToLocalDateTime: invalid date '$value', using today", error)
            LocalDate.now().atStartOfDay()
        }
    }

    companion object {
        private const val TAG = "TripRepository"
    }
}
