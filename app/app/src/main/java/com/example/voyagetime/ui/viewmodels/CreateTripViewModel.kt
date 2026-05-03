package com.example.voyagetime.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.repository.TripRepositoryImpl
import com.example.voyagetime.domain.repository.TripRepository
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale

class CreateTripViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TripRepository

    init {
        val database = VoyageTimeDatabase.getDatabase(application)
        repository = TripRepositoryImpl(database.tripDao())
    }

    fun createTrip(
        destination: String,
        country: String,
        startDate: String,
        endDate: String,
        budget: String
    ) {
        val start = parseDate(startDate) ?: return
        val end = parseDate(endDate) ?: return
        val today = LocalDate.now()

        if (start.isBefore(today) || end.isBefore(today) || end.isBefore(start)) {
            Log.e(TAG, "Invalid date range")
            return
        }

        val normalizedDestination = destination.trim()
        val normalizedCountry = country.trim()
        val normalizedBudget = budget.trim()

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

        viewModelScope.launch {
            repository.addTrip(newTrip)
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