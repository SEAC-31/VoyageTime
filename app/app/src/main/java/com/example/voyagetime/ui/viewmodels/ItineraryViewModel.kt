package com.example.voyagetime.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.domain.repository.ItineraryRepository
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent
import com.example.voyagetime.ui.screens.ItinerarySummary
import com.example.voyagetime.ui.screens.TripItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class ItineraryUiState(
    val summary: ItinerarySummary? = null,
    val days: List<ItineraryDayData> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class ItineraryViewModel(
    private val itineraryRepository: ItineraryRepository,
    private val tripRepository: TripRepository
) : ViewModel() {

    private val _currentTripId = MutableStateFlow<String?>(null)
    private val _uiState = MutableStateFlow(ItineraryUiState())
    val uiState: StateFlow<ItineraryUiState> = _uiState.asStateFlow()

    private var observerJob: Job? = null

    fun loadTrip(tripId: String) {
        if (tripId == _currentTripId.value && observerJob != null) return

        _currentTripId.value = tripId
        observerJob?.cancel()

        observerJob = viewModelScope.launch {
            combine(
                tripRepository.observeTrip(tripId),
                itineraryRepository.getTripDays(tripId)
            ) { trip, storedDays ->
                if (trip == null) {
                    ItineraryUiState()
                } else {
                    val summary = ItinerarySummary(
                        destination     = "${trip.destination}, ${trip.country}",
                        dateRange       = trip.dateRange,
                        totalDays       = trip.duration,
                        estimatedBudget = trip.budget,
                        imageRes        = trip.image,
                        status          = trip.statusLabel
                    )
                    val expectedDays = createEmptyDaysForTrip(trip)
                    val mergedDays   = mergeExpectedDaysWithStoredDays(expectedDays, storedDays)
                    ItineraryUiState(summary = summary, days = mergedDays)
                }
            }
                .catch { error -> Log.e(TAG, "Error observing itinerary", error) }
                .collect { newState -> _uiState.update { newState } }
        }
    }

    // ── Morning ───────────────────────────────────────────────────────────────

    fun addMorningEvent(dayIndex: Int, event: ItineraryEvent) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.addMorningEvent(tripId, dayIndex, event) }
    }

    fun updateMorningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.updateMorningEvent(tripId, dayIndex, eventIndex, event) }
    }

    fun deleteMorningEvent(dayIndex: Int, eventIndex: Int) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.deleteMorningEvent(tripId, dayIndex, eventIndex) }
    }

    // ── Afternoon ─────────────────────────────────────────────────────────────

    fun addAfternoonEvent(dayIndex: Int, event: ItineraryEvent) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.addAfternoonEvent(tripId, dayIndex, event) }
    }

    fun updateAfternoonEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.updateAfternoonEvent(tripId, dayIndex, eventIndex, event) }
    }

    fun deleteAfternoonEvent(dayIndex: Int, eventIndex: Int) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.deleteAfternoonEvent(tripId, dayIndex, eventIndex) }
    }

    // ── Evening ───────────────────────────────────────────────────────────────

    fun addEveningEvent(dayIndex: Int, event: ItineraryEvent) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.addEveningEvent(tripId, dayIndex, event) }
    }

    fun updateEveningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.updateEveningEvent(tripId, dayIndex, eventIndex, event) }
    }

    fun deleteEveningEvent(dayIndex: Int, eventIndex: Int) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.deleteEveningEvent(tripId, dayIndex, eventIndex) }
    }

    // ── Notes ─────────────────────────────────────────────────────────────────

    fun updateNotes(dayIndex: Int, notes: String) {
        val tripId = _currentTripId.value ?: return
        viewModelScope.launch { itineraryRepository.updateNotes(tripId, dayIndex, notes) }
    }

    // ── Helpers de Sharon (lógica de días) ────────────────────────────────────

    private fun createEmptyDaysForTrip(trip: TripItem): List<ItineraryDayData> {
        return buildTripDates(trip).mapIndexed { index, date ->
            ItineraryDayData(
                dayLabel      = "Day ${index + 1}",
                dayDate       = date,
                morningPlan   = mutableListOf(),
                afternoonPlan = mutableListOf(),
                eveningPlan   = mutableListOf(),
                notes         = ""
            )
        }
    }

    private fun mergeExpectedDaysWithStoredDays(
        expectedDays: List<ItineraryDayData>,
        storedDays: List<ItineraryDayData>
    ): List<ItineraryDayData> {
        if (expectedDays.isEmpty()) return storedDays
        return expectedDays.mapIndexed { index, expected ->
            val stored = storedDays.getOrNull(index)
            if (stored == null) expected
            else expected.copy(
                morningPlan   = stored.morningPlan,
                afternoonPlan = stored.afternoonPlan,
                eveningPlan   = stored.eveningPlan,
                notes         = stored.notes
            )
        }
    }

    private fun buildTripDates(trip: TripItem): List<String> {
        val parsedRange = parseDateRange(trip.dateRange)
        if (parsedRange != null) {
            val fmt   = DateTimeFormatter.ISO_LOCAL_DATE
            val dates = mutableListOf<String>()
            var cur   = parsedRange.first
            while (!cur.isAfter(parsedRange.second)) {
                dates.add(cur.format(fmt))
                cur = cur.plusDays(1)
            }
            if (dates.isNotEmpty()) return dates
        }
        val totalDays = trip.duration.substringBefore(" ").toIntOrNull()?.coerceAtLeast(1) ?: 1
        return List(totalDays) { index -> "Day ${index + 1}" }
    }

    private fun parseDateRange(dateRange: String): Pair<LocalDate, LocalDate>? {
        val parts = dateRange.split(" - ").map { it.trim() }
        if (parts.size != 2) return null
        val start = parseFlexibleDate(parts[0]) ?: return null
        val end   = parseFlexibleDate(parts[1]) ?: return null
        return start to end
    }

    private fun parseFlexibleDate(value: String): LocalDate? {
        val formatters = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )
        for (fmt in formatters) {
            try { return LocalDate.parse(value.trim(), fmt) } catch (_: DateTimeParseException) {}
        }
        return null
    }

    companion object {
        private const val TAG = "ItineraryViewModel"
    }
}