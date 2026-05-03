package com.example.voyagetime.domain.repository

import com.example.voyagetime.ui.screens.TripItem
import kotlinx.coroutines.flow.Flow

interface TripRepository {
    fun getUpcomingTrips(): Flow<List<TripItem>>
    fun getPastTrips(): Flow<List<TripItem>>
    fun getAllTrips(): Flow<List<TripItem>>
    fun observeTrip(tripId: String): Flow<TripItem?>

    suspend fun addTrip(newTrip: TripItem)
    suspend fun updateTrip(updatedTrip: TripItem)
    suspend fun deleteTrip(tripId: String)

    fun getFavoriteRegion(): String
    fun updateFavoriteRegion(newValue: String)

    fun getTravelGoal(): String
    fun updateTravelGoal(newValue: String)

    fun getNextDeparture(): String
}