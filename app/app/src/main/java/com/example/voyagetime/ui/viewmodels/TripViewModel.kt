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
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TripsUiState(
    val upcomingTrips: List<TripItem> = emptyList(),
    val pastTrips: List<TripItem> = emptyList(),
    val favoriteRegion: String = "",
    val travelGoal: String = "",
    val nextDeparture: String = ""
) {
    val allTrips: List<TripItem>
        get() = upcomingTrips + pastTrips

    val totalBudget: Int
        get() = allTrips.sumOf { it.budgetValue() }

    val totalDays: Int
        get() = allTrips.sumOf { it.durationValue() }

    val stats: List<HomeStat>
        get() = listOf(
            HomeStat(allTrips.size.toString(), R.string.home_stat_trips, Icons.Default.TravelExplore),
            HomeStat(totalDays.toString(), R.string.home_stat_days_planned, Icons.Default.CalendarMonth),
            HomeStat("€$totalBudget", R.string.home_stat_budget, Icons.Default.AttachMoney)
        )
}

class TripsViewModel(
    private val repository: TripRepository
) : ViewModel() {

    val uiState: StateFlow<TripsUiState> = combine(
        repository.getUpcomingTrips(),
        repository.getPastTrips()
    ) { upcoming, past ->
        val nextDeparture = upcoming.firstOrNull()
            ?.let { "${it.destination} — ${it.dateRange.substringBefore(" -").trim()}" }
            ?: ""

        TripsUiState(
            upcomingTrips  = upcoming,
            pastTrips      = past,
            favoriteRegion = repository.getFavoriteRegion(),
            travelGoal     = repository.getTravelGoal(),
            nextDeparture  = nextDeparture
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TripsUiState()
    )

    fun reloadTrips() = Unit  // Room notifica automáticamente, mantenemos por compatibilidad

    fun updateTrip(updatedTrip: TripItem) {
        viewModelScope.launch { repository.updateTrip(updatedTrip) }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch { repository.deleteTrip(tripId) }
    }

    fun updateFavoriteRegion(newValue: String) {
        viewModelScope.launch { repository.updateFavoriteRegion(newValue) }
    }

    fun updateTravelGoal(newValue: String) {
        viewModelScope.launch { repository.updateTravelGoal(newValue) }
    }

    private fun buildNextDeparture(trips: List<TripItem>): String {
        return trips.firstOrNull { it.state == TripState.UPCOMING || it.state == TripState.PLANNED }
            ?.let { "${it.destination} — ${it.dateRange.substringBefore(" - ").trim()}" }
            .orEmpty()
    }

    companion object {
        private const val TAG = "TripsViewModel"
    }
}

private fun TripItem.budgetValue(): Int =
    budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0

private fun TripItem.durationValue(): Int =
    duration.substringBefore(" ").trim().toIntOrNull() ?: 0