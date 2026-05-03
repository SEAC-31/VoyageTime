package com.example.voyagetime.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.repository.TripRepositoryImpl
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale

data class CreateTripUiState(
    @StringRes val destinationErrorRes: Int? = null,
    @StringRes val dateErrorRes: Int? = null,
    @StringRes val generalErrorRes: Int? = null,
    val isSaving: Boolean = false,
    val isCreated: Boolean = false
)

class CreateTripViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    private val _uiState = MutableStateFlow(CreateTripUiState())
    val uiState: StateFlow<CreateTripUiState> = _uiState.asStateFlow()

    init {
        val database = VoyageTimeDatabase.getDatabase(application)
        repository = TripRepositoryImpl(database.tripDao())
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                destinationErrorRes = null,
                dateErrorRes = null,
                generalErrorRes = null,
                isCreated = false
            )
        }
    }

    fun consumeCreatedEvent() {
        _uiState.update { it.copy(isCreated = false) }
    }

    fun createTrip(
        destination: String,
        country: String,
        startDate: String,
        endDate: String,
        budget: String
    ) {
        val normalizedDestination = destination.trim()
        val normalizedCountry = country.trim()
        val normalizedBudget = budget.trim()

        val start = parseDate(startDate)
        val end = parseDate(endDate)
        val today = LocalDate.now()

        if (start == null || end == null) {
            Log.e(TAG, "createTrip: invalid date format start=$startDate end=$endDate")
            _uiState.update { it.copy(dateErrorRes = R.string.validation_date_range_example) }
            return
        }

        if (start.isBefore(today) || end.isBefore(today)) {
            Log.e(TAG, "createTrip: past date rejected start=$start end=$end today=$today")
            _uiState.update { it.copy(dateErrorRes = R.string.validation_date_past) }
            return
        }

        if (end.isBefore(start)) {
            Log.e(TAG, "createTrip: end before start rejected start=$start end=$end")
            _uiState.update { it.copy(dateErrorRes = R.string.validation_end_before_start) }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isSaving = true,
                        destinationErrorRes = null,
                        dateErrorRes = null,
                        generalErrorRes = null,
                        isCreated = false
                    )
                }

                if (repository.isTripDestinationTaken(normalizedDestination)) {
                    Log.w(TAG, "createTrip: duplicate destination rejected destination=$normalizedDestination")
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            destinationErrorRes = R.string.validation_trip_duplicate
                        )
                    }
                    return@launch
                }

                val durationDays = calculateTripDays(start, end)
                val imageRes = resolveTripImage(normalizedDestination, normalizedCountry)

                val newTrip = TripItem(
                    id = "0",
                    destination = normalizedDestination,
                    country = normalizedCountry,
                    dateRange = formatDateRange(start, end),
                    duration = formatDuration(durationDays),
                    budget = "€$normalizedBudget",
                    statusLabel = "Planned",
                    state = TripState.PLANNED,
                    image = imageRes,
                    coverImageUri = null
                )

                repository.addTrip(newTrip)
                Log.i(TAG, "createTrip: trip creation completed destination=$normalizedDestination")

                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isCreated = true
                    )
                }
            } catch (error: Exception) {
                Log.e(TAG, "createTrip: database operation failed", error)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        generalErrorRes = R.string.validation_database_error
                    )
                }
            }
        }
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

    private fun calculateTripDays(start: LocalDate, end: LocalDate): Int {
        return ChronoUnit.DAYS.between(start, end).toInt() + 1
    }

    private fun formatDuration(days: Int): String {
        return if (days == 1) "1 day" else "$days days"
    }

    private fun resolveTripImage(destination: String, country: String): Int {
        val key = "${destination.trim().lowercase(Locale.ENGLISH)} ${country.trim().lowercase(Locale.ENGLISH)}"

        return when {
            "paris" in key || "france" in key -> R.drawable.paris
            "tokyo" in key || "japan" in key -> R.drawable.tokyo
            "barcelona" in key || "spain" in key -> R.drawable.barcelona
            "new york" in key || "united states" in key || "usa" in key -> R.drawable.newyork
            else -> R.drawable.logo_no_background
        }
    }

    companion object {
        private const val TAG = "CreateTripViewModel"
    }
}
