package com.example.voyagetime.data.repository

import android.util.Log
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.domain.source.FakeTripDataSource
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState

class TripRepositoryImpl : TripRepository {

    override fun getUpcomingTrips(): List<TripItem> {
        return FakeTripDataSource.trips.filter {
            it.state == TripState.UPCOMING || it.state == TripState.PLANNED
        }
    }

    override fun getPastTrips(): List<TripItem> {
        return FakeTripDataSource.trips.filter {
            it.state == TripState.COMPLETED
        }
    }

    override fun getAllTrips(): List<TripItem> {
        return FakeTripDataSource.trips.toList()
    }

    override fun addTrip(newTrip: TripItem) {
        FakeTripDataSource.trips.add(0, newTrip)
        Log.i("TripRepository", "Trip added: ${newTrip.id}")
    }

    override fun updateTrip(updatedTrip: TripItem) {
        val index = FakeTripDataSource.trips.indexOfFirst { it.id == updatedTrip.id }

        if (index != -1) {
            FakeTripDataSource.trips[index] = updatedTrip
            Log.i("TripRepository", "Trip updated: ${updatedTrip.id}")
        } else {
            Log.e("TripRepository", "Trip not found: ${updatedTrip.id}")
        }
    }

    override fun deleteTrip(tripId: String) {
        val wasRemoved = FakeTripDataSource.trips.removeAll { it.id == tripId }

        if (wasRemoved) {
            Log.i("TripRepository", "Trip deleted: $tripId")
        } else {
            Log.e("TripRepository", "Trip not found for deletion: $tripId")
        }
    }

    override fun getFavoriteRegion(): String {
        return FakeTripDataSource.favoriteRegion
    }

    override fun updateFavoriteRegion(newValue: String) {
        FakeTripDataSource.favoriteRegion = newValue
        Log.i("TripRepository", "Favorite region updated")
    }

    override fun getTravelGoal(): String {
        return FakeTripDataSource.travelGoal
    }

    override fun updateTravelGoal(newValue: String) {
        FakeTripDataSource.travelGoal = newValue
        Log.i("TripRepository", "Travel goal updated")
    }

    override fun getNextDeparture(): String {
        return getUpcomingTrips()
            .firstOrNull()
            ?.let { "${it.destination} — ${extractDisplayStartDate(it.dateRange)}" }
            ?: FakeTripDataSource.nextDeparture
    }

    private fun extractDisplayStartDate(dateRange: String): String {
        return dateRange.substringBefore("-").trim()
    }
}