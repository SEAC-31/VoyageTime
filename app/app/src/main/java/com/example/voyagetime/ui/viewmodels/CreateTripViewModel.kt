package com.example.voyagetime.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.repository.FirebaseAuthRepositoryImpl
import com.example.voyagetime.data.repository.TripRepositoryImpl
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

class CreateTripViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    private val _uiState = MutableStateFlow(CreateTripUiState())
    val uiState: StateFlow<CreateTripUiState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "Initializing CreateTripViewModel")
        val database       = VoyageTimeDatabase.getDatabase(application)
        val authRepository = FirebaseAuthRepositoryImpl()
        repository = TripRepositoryImpl(database.tripDao(), authRepository)
    }

    fun createTrip(
        destination: String,
        country: String,
        startDate: String,
        endDate: String,
        budget: String
    ) {
        Log.d(TAG, "createTrip called: destination=$destination, country=$country, start=$startDate, end=$endDate")

        val start = parseDate(startDate) ?: run {
            Log.e(TAG, "createTrip: invalid startDate format — $startDate")
            return
        }
        val end = parseDate(endDate) ?: run {
            Log.e(TAG, "createTrip: invalid endDate format — $endDate")
            return
        }
        val today = LocalDate.now()

        if (start.isBefore(today) || end.isBefore(today) || end.isBefore(start)) {
            Log.e(TAG, "createTrip: invalid date range — start=$start, end=$end, today=$today")
            return
        }

        val normalizedDestination = destination.trim()
        val normalizedCountry     = country.trim()
        val normalizedBudget      = budget.trim()
        val durationDays          = ChronoUnit.DAYS.between(start, end).toInt() + 1
        val imageRes              = resolveTripImage(normalizedDestination, normalizedCountry)

        Log.d(TAG, "createTrip: durationDays=$durationDays, imageRes=$imageRes")
        _uiState.value = CreateTripUiState(isLoading = true)

        viewModelScope.launch {
            val newTrip = TripItem(
                id          = "0",
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
            Log.i(TAG, "createTrip: trip saved successfully — destination=$normalizedDestination")
            _uiState.value = CreateTripUiState(isSaved = true)
        }
    }

    fun resetState() {
        Log.d(TAG, "resetState called")
        _uiState.value = CreateTripUiState()
    }

    private fun parseDate(value: String): LocalDate? {
        return try {
            LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (_: DateTimeParseException) { null }
    }

    private fun formatDateRange(start: LocalDate, end: LocalDate): String {
        val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return "${start.format(fmt)} - ${end.format(fmt)}"
    }

    private fun formatDuration(days: Int): String =
        if (days == 1) "1 day" else "$days days"

    private fun resolveTripImage(destination: String, country: String): Int {
        val key = "${destination.lowercase(Locale.ENGLISH)} ${country.lowercase(Locale.ENGLISH)}"
        return when {
            "paris"     in key || "france"        in key -> R.drawable.paris
            "tokyo"     in key || "japan"         in key -> R.drawable.tokyo
            "barcelona" in key || "spain"         in key -> R.drawable.barcelona
            "new york"  in key || "united states" in key || "usa" in key -> R.drawable.newyork
            else -> R.drawable.logo_no_background
        }
    }

    companion object {
        private const val TAG = "CreateTripViewModel"
    }
}