package com.example.voyagetime.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TravelExplore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.repository.FirebaseAuthRepositoryImpl
import com.example.voyagetime.data.repository.TripRepositoryImpl
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.HomeStat
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
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

class TripsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    private val _uiState = MutableStateFlow(TripsUiState())
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    init {
        val database = VoyageTimeDatabase.getDatabase(application)
        val authRepository = FirebaseAuthRepositoryImpl()
        repository = TripRepositoryImpl(database.tripDao(), authRepository)

        viewModelScope.launch {
            repository.getAllTrips()
                .catch { error -> Log.e(TAG, "Error observing trips", error) }
                .collect { trips ->
                    _uiState.update { current ->
                        current.copy(
                            upcomingTrips  = trips.filter { it.state == TripState.UPCOMING || it.state == TripState.PLANNED },
                            pastTrips      = trips.filter { it.state == TripState.COMPLETED },
                            favoriteRegion = repository.getFavoriteRegion(),
                            travelGoal     = repository.getTravelGoal(),
                            nextDeparture  = buildNextDeparture(trips)
                        )
                    }
                }
        }
    }

    fun reloadTrips() = Unit

    fun updateTrip(updatedTrip: TripItem) {
        viewModelScope.launch { repository.updateTrip(updatedTrip) }
    }

    fun deleteTrip(tripId: String) {
        viewModelScope.launch { repository.deleteTrip(tripId) }
    }

    fun updateFavoriteRegion(newValue: String) {
        repository.updateFavoriteRegion(newValue)
        _uiState.update { it.copy(favoriteRegion = newValue) }
    }

    fun updateTravelGoal(newValue: String) {
        repository.updateTravelGoal(newValue)
        _uiState.update { it.copy(travelGoal = newValue) }
    }

    private fun buildNextDeparture(trips: List<TripItem>): String =
        trips.firstOrNull { it.state == TripState.UPCOMING || it.state == TripState.PLANNED }
            ?.let { "${it.destination} — ${it.dateRange.substringBefore(" - ").trim()}" }
            .orEmpty()

    companion object {
        private const val TAG = "TripsViewModel"
    }
}

private fun TripItem.budgetValue(): Int =
    budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0

private fun TripItem.durationValue(): Int =
    duration.substringBefore(" ").trim().toIntOrNull() ?: 0