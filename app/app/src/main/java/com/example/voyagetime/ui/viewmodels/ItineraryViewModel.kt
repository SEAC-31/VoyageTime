package com.example.voyagetime.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.voyagetime.domain.repository.ItineraryRepository
import com.example.voyagetime.data.repository.ItineraryRepositoryImpl
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.data.repository.TripRepositoryImpl
import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent
import com.example.voyagetime.ui.screens.ItinerarySummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ItineraryUiState(
    val summary: ItinerarySummary? = null,
    val days: List<ItineraryDayData> = emptyList()
)

class ItineraryViewModel(
    private val itineraryRepository: ItineraryRepository = ItineraryRepositoryImpl(),
    private val tripRepository: TripRepository = TripRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ItineraryUiState())
    val uiState: StateFlow<ItineraryUiState> = _uiState.asStateFlow()

    private var currentTripId: String? = null

    fun loadTrip(tripId: String) {
        currentTripId = tripId
        reload()
    }

    fun addMorningEvent(dayIndex: Int, event: ItineraryEvent) {
        val tripId = currentTripId ?: return
        itineraryRepository.addMorningEvent(tripId, dayIndex, event)
        reload()
    }

    fun updateMorningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val tripId = currentTripId ?: return
        itineraryRepository.updateMorningEvent(tripId, dayIndex, eventIndex, event)
        reload()
    }

    fun deleteMorningEvent(dayIndex: Int, eventIndex: Int) {
        val tripId = currentTripId ?: return
        itineraryRepository.deleteMorningEvent(tripId, dayIndex, eventIndex)
        reload()
    }

    fun addAfternoonEvent(dayIndex: Int, event: ItineraryEvent) {
        val tripId = currentTripId ?: return
        itineraryRepository.addAfternoonEvent(tripId, dayIndex, event)
        reload()
    }

    fun updateAfternoonEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val tripId = currentTripId ?: return
        itineraryRepository.updateAfternoonEvent(tripId, dayIndex, eventIndex, event)
        reload()
    }

    fun deleteAfternoonEvent(dayIndex: Int, eventIndex: Int) {
        val tripId = currentTripId ?: return
        itineraryRepository.deleteAfternoonEvent(tripId, dayIndex, eventIndex)
        reload()
    }

    fun addEveningEvent(dayIndex: Int, event: ItineraryEvent) {
        val tripId = currentTripId ?: return
        itineraryRepository.addEveningEvent(tripId, dayIndex, event)
        reload()
    }

    fun updateEveningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val tripId = currentTripId ?: return
        itineraryRepository.updateEveningEvent(tripId, dayIndex, eventIndex, event)
        reload()
    }

    fun deleteEveningEvent(dayIndex: Int, eventIndex: Int) {
        val tripId = currentTripId ?: return
        itineraryRepository.deleteEveningEvent(tripId, dayIndex, eventIndex)
        reload()
    }

    fun updateNotes(dayIndex: Int, notes: String) {
        val tripId = currentTripId ?: return
        itineraryRepository.updateNotes(tripId, dayIndex, notes)
        reload()
    }

    private fun reload() {
        val tripId = currentTripId ?: return

        val trip = tripRepository.getAllTrips().firstOrNull { it.id == tripId }
            ?: tripRepository.getAllTrips().first()

        val summary = ItinerarySummary(
            destination = "${trip.destination}, ${trip.country}",
            dateRange = trip.dateRange,
            totalDays = trip.duration,
            estimatedBudget = trip.budget,
            imageRes = trip.image,
            status = trip.statusLabel
        )

        val days = itineraryRepository.getTripDays(tripId)

        _uiState.update {
            it.copy(
                summary = summary,
                days = days
            )
        }
    }
}