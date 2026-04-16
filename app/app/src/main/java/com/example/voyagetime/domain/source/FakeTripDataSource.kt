package com.example.voyagetime.domain.source

import com.example.voyagetime.R
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState

object FakeTripDataSource {

    val trips = mutableListOf(
        TripItem(
            id = "paris",
            destination = "Paris",
            country = "France",
            dateRange = "12/06/2026 - 18/06/2026",
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
            dateRange = "02/08/2026 - 11/08/2026",
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
            dateRange = "10/03/2026 - 13/03/2026",
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
            dateRange = "04/12/2025 - 10/12/2025",
            duration = "6 days",
            budget = "€1,680",
            statusLabel = "Completed",
            state = TripState.COMPLETED,
            image = R.drawable.newyork
        )
    )

    var favoriteRegion: String = "Europe & North America"
    var travelGoal: String = "Complete 4 memorable trips with clear itineraries"
    var nextDeparture: String = "Paris — 12/06/2026"
}