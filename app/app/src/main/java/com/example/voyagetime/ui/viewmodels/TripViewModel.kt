package com.example.voyagetime.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TravelExplore
<<<<<<< HEAD
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.HomeStat
import com.example.voyagetime.ui.screens.TripItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
=======
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
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
>>>>>>> sharon
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

<<<<<<< HEAD
class TripsViewModel(
    private val repository: TripRepository
) : ViewModel() {

    // Combinamos los dos flows de Room en un único StateFlow de UI.
    // Cada vez que Room emite (insert/update/delete), la UI recompone automáticamente.
    val uiState: StateFlow<TripsUiState> = combine(
        repository.getUpcomingTrips(),
        repository.getPastTrips()
    ) { upcoming, past ->
        val nextDeparture = upcoming.firstOrNull()
            ?.let { "${it.destination} — ${it.dateRange.substringBefore(" -").trim()}" }
            ?: ""

        TripsUiState(
            upcomingTrips = upcoming,
            pastTrips = past,
            favoriteRegion = repository.getFavoriteRegion(),
            travelGoal = repository.getTravelGoal(),
            nextDeparture = nextDeparture
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TripsUiState()
    )

    fun updateTrip(updatedTrip: TripItem) {
        viewModelScope.launch {
            repository.updateTrip(updatedTrip)
        }
    }

=======
class TripsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    private val _uiState = MutableStateFlow(TripsUiState())
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    init {
        val database = VoyageTimeDatabase.getDatabase(application)
        repository = TripRepositoryImpl(database.tripDao())

        viewModelScope.launch {
            repository.getAllTrips()
                .catch { error ->
                    Log.e(TAG, "Error observing trips", error)
                }
                .collect { trips ->
                    _uiState.update { current ->
                        current.copy(
                            upcomingTrips = trips.filter {
                                it.state == TripState.UPCOMING || it.state == TripState.PLANNED
                            },
                            pastTrips = trips.filter {
                                it.state == TripState.COMPLETED
                            },
                            favoriteRegion = repository.getFavoriteRegion(),
                            travelGoal = repository.getTravelGoal(),
                            nextDeparture = buildNextDeparture(trips)
                        )
                    }
                }
        }
    }

    fun reloadTrips() = Unit

    fun updateTrip(updatedTrip: TripItem) {
        viewModelScope.launch {
            repository.updateTrip(updatedTrip)
        }
    }

>>>>>>> sharon
    fun deleteTrip(tripId: String) {
        viewModelScope.launch {
            repository.deleteTrip(tripId)
        }
    }

    fun updateFavoriteRegion(newValue: String) {
<<<<<<< HEAD
        viewModelScope.launch {
            repository.updateFavoriteRegion(newValue)
=======
        repository.updateFavoriteRegion(newValue)
        _uiState.update { current ->
            current.copy(favoriteRegion = newValue)
>>>>>>> sharon
        }
    }

    fun updateTravelGoal(newValue: String) {
<<<<<<< HEAD
        viewModelScope.launch {
            repository.updateTravelGoal(newValue)
        }
=======
        repository.updateTravelGoal(newValue)
        _uiState.update { current ->
            current.copy(travelGoal = newValue)
        }
    }

    private fun buildNextDeparture(trips: List<TripItem>): String {
        val nextTrip = trips.firstOrNull {
            it.state == TripState.UPCOMING || it.state == TripState.PLANNED
        }

        return nextTrip
            ?.let { "${it.destination} — ${it.dateRange.substringBefore(" - ").trim()}" }
            .orEmpty()
    }

    companion object {
        private const val TAG = "TripsViewModel"
>>>>>>> sharon
    }
}

private fun TripItem.budgetValue(): Int =
    budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0

private fun TripItem.durationValue(): Int =
    duration.substringBefore(" ").trim().toIntOrNull() ?: 0