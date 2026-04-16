package com.example.voyagetime.ui.viewmodels

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TravelExplore
import androidx.lifecycle.ViewModel
import com.example.voyagetime.R
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.data.repository.TripRepositoryImpl
import com.example.voyagetime.ui.screens.HomeStat
import com.example.voyagetime.ui.screens.TripItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.listOf

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
    private val repository: TripRepository = TripRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        TripsUiState(
            upcomingTrips = repository.getUpcomingTrips(),
            pastTrips = repository.getPastTrips(),
            favoriteRegion = repository.getFavoriteRegion(),
            travelGoal = repository.getTravelGoal(),
            nextDeparture = repository.getNextDeparture()
        )
    )

    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    fun reloadTrips() {
        _uiState.update { current ->
            current.copy(
                upcomingTrips = repository.getUpcomingTrips(),
                pastTrips = repository.getPastTrips(),
                favoriteRegion = repository.getFavoriteRegion(),
                travelGoal = repository.getTravelGoal(),
                nextDeparture = repository.getNextDeparture()
            )
        }
    }

    fun updateTrip(updatedTrip: TripItem) {
        repository.updateTrip(updatedTrip)
        reloadTrips()
    }

    fun deleteTrip(tripId: String) {
        repository.deleteTrip(tripId)
        reloadTrips()
    }

    fun updateFavoriteRegion(newValue: String) {
        repository.updateFavoriteRegion(newValue)
        reloadTrips()
    }

    fun updateTravelGoal(newValue: String) {
        repository.updateTravelGoal(newValue)
        reloadTrips()
    }
}

private fun TripItem.budgetValue(): Int {
    return budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0
}

private fun TripItem.durationValue(): Int {
    return duration.substringBefore(" ").trim().toIntOrNull() ?: 0
}