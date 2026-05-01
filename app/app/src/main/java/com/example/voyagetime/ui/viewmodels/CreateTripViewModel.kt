package com.example.voyagetime.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale

data class CreateTripUiState(
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

class CreateTripViewModel(
    private val repository: TripRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTripUiState())
    val uiState: StateFlow<CreateTripUiState> = _uiState.asStateFlow()

    fun createTrip(
        destination: String,
        country: String,
        startDate: String,
        endDate: String,
        budget: String
    ) {
        val start = parseDate(startDate) ?: return
        val end   = parseDate(endDate)   ?: return
        val today = LocalDate.now()

        if (start.isBefore(today) || end.isBefore(today) || end.isBefore(start)) return

        val normalizedDestination = destination.trim()
        val normalizedCountry     = country.trim()
        val normalizedBudget      = budget.trim()
        val durationDays          = ChronoUnit.DAYS.between(start, end).toInt() + 1
        val imageRes              = resolveTripImage(normalizedDestination, normalizedCountry)

        _uiState.value = CreateTripUiState(isLoading = true)

        viewModelScope.launch {
            // Generamos el ID comprobando duplicados contra Room
            val tripId = buildTripId(normalizedDestination)

            val newTrip = TripItem(
                id          = tripId,
                destination = normalizedDestination,
                country     = normalizedCountry,
                dateRange   = formatDateRange(start, end),
                duration    = formatDuration(durationDays),
                budget      = "€$normalizedBudget",
                statusLabel = "Planned",
                state       = TripState.PLANNED,
                image       = imageRes
            )

            repository.addTrip(newTrip)
            _uiState.value = CreateTripUiState(isSaved = true)
        }
    }

    fun resetState() {
        _uiState.value = CreateTripUiState()
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private suspend fun buildTripId(destination: String): String {
        val base = destination
            .trim()
            .lowercase(Locale.ENGLISH)
            .replace(Regex("[^a-z0-9]+"), "")
            .ifBlank { "trip" }

        // Recogemos la lista una sola vez (no necesitamos el flow reactivo aquí)
        val existingIds = repository.getAllTrips().first().map { it.id }.toSet()

        var candidate = base
        var counter   = 1
        while (candidate in existingIds) {
            counter++
            candidate = "$base$counter"
        }
        return candidate
    }

    private fun parseDate(value: String): LocalDate? {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return try {
            LocalDate.parse(value.trim(), formatter)
        } catch (_: DateTimeParseException) {
            null
        }
    }

    private fun formatDateRange(start: LocalDate, end: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return "${start.format(formatter)} - ${end.format(formatter)}"
    }

    private fun formatDuration(days: Int): String =
        if (days == 1) "1 day" else "$days days"

    private fun resolveTripImage(destination: String, country: String): Int {
        val key = "${destination.lowercase(Locale.ENGLISH)} ${country.lowercase(Locale.ENGLISH)}"
        return when {
            "paris"    in key || "france"        in key -> R.drawable.paris
            "tokyo"    in key || "japan"         in key -> R.drawable.tokyo
            "barcelona" in key || "spain"        in key -> R.drawable.barcelona
            "new york" in key || "united states" in key || "usa" in key -> R.drawable.newyork
            else -> R.drawable.logo_no_background
        }
    }
}