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
import com.example.voyagetime.ui.screens.HomeTripSummary
import com.example.voyagetime.ui.screens.TripItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeUiState(
    val allTrips: List<HomeTripSummary> = emptyList(),
    val featuredTrips: List<HomeTripSummary> = emptyList(),
    val nextTrip: HomeTripSummary? = null,
    val totalBudget: Int = 0,
    val totalDays: Int = 0,
    val stats: List<HomeStat> = emptyList()
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val database = VoyageTimeDatabase.getDatabase(application)
        val authRepository = FirebaseAuthRepositoryImpl()
        repository = TripRepositoryImpl(database.tripDao(), authRepository)

        viewModelScope.launch {
            repository.getAllTrips()
                .catch { error -> Log.e(TAG, "Error observing home trips", error) }
                .collect { trips ->
                    val summaries = trips.map { trip ->
                        val (start, end) = parseTripDateRange(trip.dateRange)
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

                    _uiState.update {
                        it.copy(
                            allTrips      = summaries,
                            featuredTrips = featured,
                            nextTrip      = nextTrip,
                            totalBudget   = totalBudget,
                            totalDays     = totalDays,
                            stats         = stats
                        )
                    }
                }
        }
    }

    fun reload() = Unit

    companion object {
        private const val TAG = "HomeViewModel"
    }
}

private fun parseTripDateRange(dateRange: String): Pair<String, String> {
    val parts = dateRange.split(" - ").map { it.trim() }
    return if (parts.size == 2) parts[0] to parts[1] else "" to ""
}

private fun extractDays(duration: String): Int =
    duration.substringBefore(" ").toIntOrNull() ?: 0

private fun TripItem.budgetValue(): Int =
    budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0