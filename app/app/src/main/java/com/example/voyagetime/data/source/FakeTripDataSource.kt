package com.example.voyagetime.data.source

import com.example.voyagetime.R
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState

object FakeTripDataSource {

    val trips = mutableListOf(
        TripItem(
            id = "paris",
            destination = "Paris",
            country = "France",
            dateRange = "12 Jun - 18 Jun 2026",
            duration = "6 days",
            budget = "€820",
            statusLabel = "Upcoming",
            state = TripState.UPCOMING,
            image = R.drawable.paris
        ),
        TripItem(
            id = "tokyo",
            destination = "Tokyo",
            country = "Japan",
            dateRange = "02 Aug - 11 Aug 2026",
            duration = "9 days",
            budget = "€2,450",
            statusLabel = "Planned",
            state = TripState.PLANNED,
            image = R.drawable.tokyo
        ),
        TripItem(
            id = "barcelona",
            destination = "Barcelona",
            country = "Spain",
            dateRange = "10 Mar - 13 Mar 2026",
            duration = "3 days",
            budget = "€290",
            statusLabel = "Completed",
            state = TripState.COMPLETED,
            image = R.drawable.barcelona
        ),
        TripItem(
            id = "newyork",
            destination = "New York",
            country = "United States",
            dateRange = "04 Dec - 10 Dec 2025",
            duration = "6 days",
            budget = "€1,680",
            statusLabel = "Completed",
            state = TripState.COMPLETED,
            image = R.drawable.newyork
        )
    )

    var favoriteRegion: String = "Europe & North America"
    var travelGoal: String = "Complete 4 memorable trips with clear itineraries"
    var nextDeparture: String = "Paris — 12 Jun 2026"
}