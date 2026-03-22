package com.example.voyagetime.data.repository

import com.example.voyagetime.ui.screens.TripItem

interface TripRepository {
    fun getUpcomingTrips(): List<TripItem>
    fun getPastTrips(): List<TripItem>
    fun getAllTrips(): List<TripItem>

    fun updateTrip(updatedTrip: TripItem)

    fun getFavoriteRegion(): String
    fun updateFavoriteRegion(newValue: String)

    fun getTravelGoal(): String
    fun updateTravelGoal(newValue: String)

    fun getNextDeparture(): String
}