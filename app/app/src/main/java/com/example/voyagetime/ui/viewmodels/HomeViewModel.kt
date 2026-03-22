package com.example.voyagetime.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TravelExplore
import androidx.lifecycle.ViewModel
import com.example.voyagetime.data.repository.TripRepository
import com.example.voyagetime.data.repository.TripRepositoryImpl
import com.example.voyagetime.ui.screens.HomeStat
import com.example.voyagetime.ui.screens.HomeTripSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeUiState(
    val allTrips: List<HomeTripSummary> = emptyList(),
    val featuredTrips: List<HomeTripSummary> = emptyList(),
    val nextTrip: HomeTripSummary? = null,
    val totalBudget: Int = 0,
    val totalDays: Int = 0,
    val stats: List<HomeStat> = emptyList()
)

class HomeViewModel(
    private val repository: TripRepository = TripRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        reload()
    }

    fun reload() {
        val allTrips = repository.getAllTrips().map { trip ->
            val parsedRange = parseTripDateRange(trip.dateRange)

            HomeTripSummary(
                id = trip.id,
                destination = trip.destination,
                country = trip.country,
                startDate = parsedRange.first,
                endDate = parsedRange.second,
                duration = trip.duration,
                budget = trip.budgetValue(),
                image = trip.image,
                status = trip.statusLabel
            )
        }

        val nextTrip = allTrips.firstOrNull {
            it.status.equals("Upcoming", ignoreCase = true) ||
                    it.status.equals("Planned", ignoreCase = true)
        } ?: allTrips.firstOrNull()

        val featuredTrips = allTrips
            .filter { it.id != nextTrip?.id }
            .take(2)

        val totalBudget = allTrips.sumOf { it.budget }
        val totalDays = allTrips.sumOf { extractDays(it.duration) }

        val stats = listOf(
            HomeStat(allTrips.size.toString(), "Trips", Icons.Default.TravelExplore),
            HomeStat(totalDays.toString(), "Days Planned", Icons.Default.CalendarMonth),
            HomeStat("€$totalBudget", "Budget", Icons.Default.AttachMoney)
        )

        _uiState.update {
            it.copy(
                allTrips = allTrips,
                featuredTrips = featuredTrips,
                nextTrip = nextTrip,
                totalBudget = totalBudget,
                totalDays = totalDays,
                stats = stats
            )
        }
    }
}

private fun parseTripDateRange(dateRange: String): Pair<String, String> {
    val parts = dateRange.split("-").map { it.trim() }

    return when {
        parts.size != 2 -> "" to ""
        else -> {
            val start = parts[0]
            val end = parts[1]

            val startHasYear = start.takeLast(4).all { it.isDigit() }
            val endYear = end.takeLast(4).takeIf { it.length == 4 && it.all { ch -> ch.isDigit() } }

            val normalizedStart = if (!startHasYear && endYear != null) {
                "$start $endYear"
            } else {
                start
            }

            normalizedStart to end
        }
    }
}

private fun extractDays(duration: String): Int {
    return duration.substringBefore(" ").toIntOrNull() ?: 0
}

private fun com.example.voyagetime.ui.screens.TripItem.budgetValue(): Int {
    return budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0
}