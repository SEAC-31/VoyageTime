package com.example.voyagetime.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.domain.repository.ItineraryRepository
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent
import com.example.voyagetime.ui.screens.ItinerarySummary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ItineraryUiState(
    val summary: ItinerarySummary? = null,
    val days: List<ItineraryDayData> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class ItineraryViewModel(
    private val itineraryRepository: ItineraryRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    // El tripId activo. Cuando cambia, flatMapLatest cancela el flow anterior
    // y suscribe al nuevo automáticamente.
    private val _currentTripId = MutableStateFlow<String?>(null)

    val uiState: StateFlow<ItineraryUiState> = combine(
        // Flow de días: reactivo al tripId y a cambios en BD
        _currentTripId.flatMapLatest { tripId ->
            if (tripId == null) flowOf(emptyList())
            else itineraryRepository.getTripDays(tripId)
        },
        // Flow de todos los trips para construir el summary
        tripRepository.getAllTrips(),
        _currentTripId
    ) { days, allTrips, tripId ->
        val trip = allTrips.firstOrNull { it.id == tripId }
        val summary = trip?.let {
            ItinerarySummary(
                destination     = "${it.destination}, ${it.country}",
                dateRange       = it.dateRange,
                totalDays       = it.duration,
                estimatedBudget = it.budget,
                imageRes        = it.image,
                status          = it.statusLabel
            )
        }
        ItineraryUiState(summary = summary, days = days)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ItineraryUiState()
    )

    fun loadTrip(tripId: String) {
        _currentTripId.value = tripId
    }

    // ── Morning ───────────────────────────────────────────────────────────────

    fun addMorningEvent(dayIndex: Int, event: ItineraryEvent) {
        launch { itineraryRepository.addMorningEvent(currentTripId(), dayIndex, event) }
    }

    fun updateMorningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        launch { itineraryRepository.updateMorningEvent(currentTripId(), dayIndex, eventIndex, event) }
    }

    fun deleteMorningEvent(dayIndex: Int, eventIndex: Int) {
        launch { itineraryRepository.deleteMorningEvent(currentTripId(), dayIndex, eventIndex) }
    }

    // ── Afternoon ─────────────────────────────────────────────────────────────

    fun addAfternoonEvent(dayIndex: Int, event: ItineraryEvent) {
        launch { itineraryRepository.addAfternoonEvent(currentTripId(), dayIndex, event) }
    }

    fun updateAfternoonEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        launch { itineraryRepository.updateAfternoonEvent(currentTripId(), dayIndex, eventIndex, event) }
    }

    fun deleteAfternoonEvent(dayIndex: Int, eventIndex: Int) {
        launch { itineraryRepository.deleteAfternoonEvent(currentTripId(), dayIndex, eventIndex) }
    }

    // ── Evening ───────────────────────────────────────────────────────────────

    fun addEveningEvent(dayIndex: Int, event: ItineraryEvent) {
        launch { itineraryRepository.addEveningEvent(currentTripId(), dayIndex, event) }
    }

    fun updateEveningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        launch { itineraryRepository.updateEveningEvent(currentTripId(), dayIndex, eventIndex, event) }
    }

    fun deleteEveningEvent(dayIndex: Int, eventIndex: Int) {
        launch { itineraryRepository.deleteEveningEvent(currentTripId(), dayIndex, eventIndex) }
    }

    // ── Notes ─────────────────────────────────────────────────────────────────

    fun updateNotes(dayIndex: Int, notes: String) {
        launch { itineraryRepository.updateNotes(currentTripId(), dayIndex, notes) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun currentTripId(): String = _currentTripId.value ?: ""

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }
}