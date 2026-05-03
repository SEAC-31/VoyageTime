package com.example.voyagetime.ui.viewmodels

<<<<<<< HEAD
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
=======
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.repository.ItineraryRepositoryImpl
import com.example.voyagetime.data.repository.TripRepositoryImpl
>>>>>>> sharon
import com.example.voyagetime.domain.repository.ItineraryRepository
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent
import com.example.voyagetime.ui.screens.ItinerarySummary
<<<<<<< HEAD
import kotlinx.coroutines.ExperimentalCoroutinesApi
=======
import com.example.voyagetime.ui.screens.TripItem
import kotlinx.coroutines.Job
>>>>>>> sharon
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
<<<<<<< HEAD
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
=======
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
>>>>>>> sharon

data class ItineraryUiState(
    val summary: ItinerarySummary? = null,
    val days: List<ItineraryDayData> = emptyList()
)

<<<<<<< HEAD
@OptIn(ExperimentalCoroutinesApi::class)
class ItineraryViewModel(
    private val itineraryRepository: ItineraryRepository,
    private val tripRepository: TripRepository
) : ViewModel() {
=======
class ItineraryViewModel(application: Application) : AndroidViewModel(application) {

    private val itineraryRepository: ItineraryRepository
    private val tripRepository: TripRepository
>>>>>>> sharon

    // El tripId activo. Cuando cambia, flatMapLatest cancela el flow anterior
    // y suscribe al nuevo automáticamente.
    private val _currentTripId = MutableStateFlow<String?>(null)

<<<<<<< HEAD
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
=======
    private var currentTripId: String? = null
    private var observerJob: Job? = null

    init {
        val database = VoyageTimeDatabase.getDatabase(application)
        itineraryRepository = ItineraryRepositoryImpl(database.itineraryItemDao())
        tripRepository = TripRepositoryImpl(database.tripDao())
    }

    fun loadTrip(tripId: String) {
        if (tripId == currentTripId && observerJob != null) {
            return
        }

        currentTripId = tripId
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
                        destination = "${trip.destination}, ${trip.country}",
                        dateRange = trip.dateRange,
                        totalDays = trip.duration,
                        estimatedBudget = trip.budget,
                        imageRes = trip.image,
                        status = trip.statusLabel
                    )

                    val expectedDays = createEmptyDaysForTrip(trip)
                    val mergedDays = mergeExpectedDaysWithStoredDays(expectedDays, storedDays)

                    ItineraryUiState(
                        summary = summary,
                        days = mergedDays
                    )
                }
            }
                .catch { error ->
                    Log.e(TAG, "Error observing itinerary", error)
                }
                .collect { newState ->
                    _uiState.update { newState }
                }
        }
>>>>>>> sharon
    }

    // ── Morning ───────────────────────────────────────────────────────────────

    fun addMorningEvent(dayIndex: Int, event: ItineraryEvent) {
<<<<<<< HEAD
        launch { itineraryRepository.addMorningEvent(currentTripId(), dayIndex, event) }
    }

    fun updateMorningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        launch { itineraryRepository.updateMorningEvent(currentTripId(), dayIndex, eventIndex, event) }
    }

    fun deleteMorningEvent(dayIndex: Int, eventIndex: Int) {
        launch { itineraryRepository.deleteMorningEvent(currentTripId(), dayIndex, eventIndex) }
=======
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.addMorningEvent(tripId, dayIndex, event)
        }
    }

    fun updateMorningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.updateMorningEvent(tripId, dayIndex, eventIndex, event)
        }
    }

    fun deleteMorningEvent(dayIndex: Int, eventIndex: Int) {
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.deleteMorningEvent(tripId, dayIndex, eventIndex)
        }
>>>>>>> sharon
    }

    // ── Afternoon ─────────────────────────────────────────────────────────────

    fun addAfternoonEvent(dayIndex: Int, event: ItineraryEvent) {
<<<<<<< HEAD
        launch { itineraryRepository.addAfternoonEvent(currentTripId(), dayIndex, event) }
    }

    fun updateAfternoonEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        launch { itineraryRepository.updateAfternoonEvent(currentTripId(), dayIndex, eventIndex, event) }
    }

    fun deleteAfternoonEvent(dayIndex: Int, eventIndex: Int) {
        launch { itineraryRepository.deleteAfternoonEvent(currentTripId(), dayIndex, eventIndex) }
=======
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.addAfternoonEvent(tripId, dayIndex, event)
        }
    }

    fun updateAfternoonEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.updateAfternoonEvent(tripId, dayIndex, eventIndex, event)
        }
    }

    fun deleteAfternoonEvent(dayIndex: Int, eventIndex: Int) {
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.deleteAfternoonEvent(tripId, dayIndex, eventIndex)
        }
>>>>>>> sharon
    }

    // ── Evening ───────────────────────────────────────────────────────────────

    fun addEveningEvent(dayIndex: Int, event: ItineraryEvent) {
<<<<<<< HEAD
        launch { itineraryRepository.addEveningEvent(currentTripId(), dayIndex, event) }
    }

    fun updateEveningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        launch { itineraryRepository.updateEveningEvent(currentTripId(), dayIndex, eventIndex, event) }
    }

    fun deleteEveningEvent(dayIndex: Int, eventIndex: Int) {
        launch { itineraryRepository.deleteEveningEvent(currentTripId(), dayIndex, eventIndex) }
=======
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.addEveningEvent(tripId, dayIndex, event)
        }
    }

    fun updateEveningEvent(dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.updateEveningEvent(tripId, dayIndex, eventIndex, event)
        }
    }

    fun deleteEveningEvent(dayIndex: Int, eventIndex: Int) {
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.deleteEveningEvent(tripId, dayIndex, eventIndex)
        }
>>>>>>> sharon
    }

    // ── Notes ─────────────────────────────────────────────────────────────────

    fun updateNotes(dayIndex: Int, notes: String) {
<<<<<<< HEAD
        launch { itineraryRepository.updateNotes(currentTripId(), dayIndex, notes) }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun currentTripId(): String = _currentTripId.value ?: ""

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
=======
        val tripId = currentTripId ?: return

        viewModelScope.launch {
            itineraryRepository.updateNotes(tripId, dayIndex, notes)
        }
    }

    private fun createEmptyDaysForTrip(trip: TripItem): List<ItineraryDayData> {
        val dates = buildTripDates(trip)

        return dates.mapIndexed { index, date ->
            ItineraryDayData(
                dayLabel = "Day ${index + 1}",
                dayDate = date,
                morningPlan = mutableListOf(),
                afternoonPlan = mutableListOf(),
                eveningPlan = mutableListOf(),
                notes = ""
            )
        }
>>>>>>> sharon
    }

    private fun mergeExpectedDaysWithStoredDays(
        expectedDays: List<ItineraryDayData>,
        storedDays: List<ItineraryDayData>
    ): List<ItineraryDayData> {
        if (expectedDays.isEmpty()) {
            return storedDays
        }

        return expectedDays.mapIndexed { index, expected ->
            val stored = storedDays.getOrNull(index)

            if (stored == null) {
                expected
            } else {
                expected.copy(
                    morningPlan = stored.morningPlan,
                    afternoonPlan = stored.afternoonPlan,
                    eveningPlan = stored.eveningPlan,
                    notes = stored.notes
                )
            }
        }
    }

    private fun buildTripDates(trip: TripItem): List<String> {
        val parsedRange = parseDateRange(trip.dateRange)

        if (parsedRange != null) {
            val outputFormatter = DateTimeFormatter.ISO_LOCAL_DATE
            val dates = mutableListOf<String>()
            var current = parsedRange.first

            while (!current.isAfter(parsedRange.second)) {
                dates.add(current.format(outputFormatter))
                current = current.plusDays(1)
            }

            if (dates.isNotEmpty()) {
                return dates
            }
        }

        val totalDays = trip.duration.substringBefore(" ").toIntOrNull()?.coerceAtLeast(1) ?: 1

        return List(totalDays) { index ->
            "Day ${index + 1}"
        }
    }

    private fun parseDateRange(dateRange: String): Pair<LocalDate, LocalDate>? {
        val parts = dateRange.split(" - ").map { it.trim() }

        if (parts.size != 2) {
            return null
        }

        val start = parseFlexibleDate(parts[0]) ?: return null
        val end = parseFlexibleDate(parts[1]) ?: return null

        return start to end
    }

    private fun parseFlexibleDate(value: String): LocalDate? {
        val formatters = listOf(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
        )

        for (formatter in formatters) {
            try {
                return LocalDate.parse(value.trim(), formatter)
            } catch (_: DateTimeParseException) {
            }
        }

        return null
    }

    companion object {
        private const val TAG = "ItineraryViewModel"
    }
}