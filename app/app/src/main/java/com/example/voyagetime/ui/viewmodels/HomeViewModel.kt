package com.example.voyagetime.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TravelExplore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.HomeStat
import com.example.voyagetime.ui.screens.HomeTripSummary
import com.example.voyagetime.ui.screens.TripItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val allTrips: List<HomeTripSummary> = emptyList(),
    val featuredTrips: List<HomeTripSummary> = emptyList(),
    val nextTrip: HomeTripSummary? = null,
    val totalBudget: Int = 0,
    val totalDays: Int = 0,
    val stats: List<HomeStat> = emptyList()
)

class HomeViewModel(
    private val repository: TripRepository
) : ViewModel() {

    // Un único flow de todos los trips; derivamos todo lo demás con map.
    // Cuando Room emite, el HomeScreen recompone solo.
    val uiState: StateFlow<HomeUiState> = repository.getAllTrips()
        .map { trips -> trips.toHomeUiState() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )

    // No hace falta reload() — Room notifica los cambios automáticamente.
}

// ── Mappers ───────────────────────────────────────────────────────────────────

private fun List<TripItem>.toHomeUiState(): HomeUiState {
    val summaries = map { trip ->
        val (start, end) = parseDateRange(trip.dateRange)
        HomeTripSummary(
            id          = trip.id,
            destination = trip.destination,
            country     = trip.country,
            startDate   = start,
            endDate     = end,
            duration    = trip.duration,
            budget      = trip.budgetValue(),
            image       = trip.image,
            status      = trip.statusLabel
        )
    }

    val nextTrip = summaries.firstOrNull {
        it.status.equals("Upcoming", ignoreCase = true) ||
                it.status.equals("Planned",  ignoreCase = true)
    } ?: summaries.firstOrNull()

    val featured    = summaries.filter { it.id != nextTrip?.id }.take(2)
    val totalBudget = summaries.sumOf { it.budget }
    val totalDays   = summaries.sumOf { extractDays(it.duration) }

    val stats = listOf(
        HomeStat(summaries.size.toString(), R.string.home_stat_trips, Icons.Default.TravelExplore),
        HomeStat(totalDays.toString(), R.string.home_stat_days_planned, Icons.Default.CalendarMonth),
        HomeStat("€$totalBudget", R.string.home_stat_budget, Icons.Default.AttachMoney)
    )

    return HomeUiState(
        allTrips     = summaries,
        featuredTrips = featured,
        nextTrip     = nextTrip,
        totalBudget  = totalBudget,
        totalDays    = totalDays,
        stats        = stats
    )
}

private fun parseDateRange(dateRange: String): Pair<String, String> {
    val parts = dateRange.split(" - ").map { it.trim() }
    return if (parts.size == 2) parts[0] to parts[1] else "" to ""
}

private fun extractDays(duration: String): Int =
    duration.substringBefore(" ").toIntOrNull() ?: 0

private fun TripItem.budgetValue(): Int =
    budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0