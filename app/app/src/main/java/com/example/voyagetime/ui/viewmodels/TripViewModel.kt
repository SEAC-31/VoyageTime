package com.example.voyagetime.ui.viewmodels

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.TravelExplore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.data.local.dao.ReservationDao
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.HomeStat
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

data class TripReservationSummary(
    val hotelName: String,
    val roomType: String,
    val apiReservationId: String,
    val startDate: String,
    val endDate: String,
    val price: Double
)

data class TripsUiState(
    val upcomingTrips: List<TripItem> = emptyList(),
    val pastTrips: List<TripItem> = emptyList(),
    val favoriteRegion: String = "",
    val travelGoal: String = "",
    val nextDeparture: String = "",
    val reservationsByTrip: Map<String, TripReservationSummary> = emptyMap()
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

@HiltViewModel
class TripsViewModel @Inject constructor(
    private val repository: TripRepository,
    private val reservationDao: ReservationDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(TripsUiState())
    val uiState: StateFlow<TripsUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "Initializing TripsViewModel")
        viewModelScope.launch {
            Log.d(TAG, "Starting trips observation")
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: LOCAL_USER_ID
            val reservationFlow = reservationDao.getAllReservations(userId)

            combine(
                repository.getAllTrips(),
                reservationFlow
            ) { trips, reservations ->
                trips to reservations.associate { reservation ->
                    reservation.tripId.toString() to TripReservationSummary(
                        hotelName = reservation.hotelName,
                        roomType = reservation.roomType,
                        apiReservationId = reservation.apiReservationId,
                        startDate = reservation.startDate,
                        endDate = reservation.endDate,
                        price = reservation.roomPrice
                    )
                }
            }
                .catch { error -> Log.e(TAG, "Error observing trips/reservations", error) }
                .collect { (trips, reservationsByTrip) ->
                    Log.i(TAG, "Trips updated: ${trips.size} total (upcoming+past)")
                    _uiState.update { current ->
                        current.copy(
                            upcomingTrips  = trips.filter { it.state == TripState.UPCOMING || it.state == TripState.PLANNED },
                            pastTrips      = trips.filter { it.state == TripState.COMPLETED },
                            favoriteRegion = repository.getFavoriteRegion(),
                            travelGoal     = repository.getTravelGoal(),
                            nextDeparture  = buildNextDeparture(trips),
                            reservationsByTrip = reservationsByTrip
                        )
                    }
                }
        }
    }

    fun reloadTrips() {
        Log.d(TAG, "reloadTrips() called")
    }

    fun updateTrip(updatedTrip: TripItem) {
        Log.i(TAG, "updateTrip: id=${updatedTrip.id}, destination=${updatedTrip.destination}")
        viewModelScope.launch {
            repository.updateTrip(updatedTrip)
            Log.i(TAG, "updateTrip: success id=${updatedTrip.id}")
        }
    }

    fun deleteTrip(tripId: String) {
        Log.i(TAG, "deleteTrip: id=$tripId")
        viewModelScope.launch {
            repository.deleteTrip(tripId)
            Log.i(TAG, "deleteTrip: success id=$tripId")
        }
    }

    fun updateFavoriteRegion(newValue: String) {
        Log.d(TAG, "updateFavoriteRegion: $newValue")
        repository.updateFavoriteRegion(newValue)
        _uiState.update { it.copy(favoriteRegion = newValue) }
    }

    fun updateTravelGoal(newValue: String) {
        Log.d(TAG, "updateTravelGoal: $newValue")
        repository.updateTravelGoal(newValue)
        _uiState.update { it.copy(travelGoal = newValue) }
    }

    private fun buildNextDeparture(trips: List<TripItem>): String =
        trips.firstOrNull { it.state == TripState.UPCOMING || it.state == TripState.PLANNED }
            ?.let { "${it.destination} — ${it.dateRange.substringBefore(" - ").trim()}" }
            .orEmpty()

    companion object {
        private const val TAG = "TripsViewModel"
        private const val LOCAL_USER_ID = "local_user"
    }
}

private fun TripItem.budgetValue(): Int =
    budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0

private fun TripItem.durationValue(): Int =
    duration.substringBefore(" ").trim().toIntOrNull() ?: 0