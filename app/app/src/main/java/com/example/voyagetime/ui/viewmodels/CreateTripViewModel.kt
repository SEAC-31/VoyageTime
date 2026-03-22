package com.example.voyagetime.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.voyagetime.R
import com.example.voyagetime.data.repository.TripRepository
import com.example.voyagetime.data.repository.TripRepositoryImpl
import com.example.voyagetime.ui.screens.TripItem
import com.example.voyagetime.ui.screens.TripState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale

class CreateTripViewModel(
    private val repository: TripRepository = TripRepositoryImpl()
) : ViewModel() {

    fun createTrip(
        destination: String,
        country: String,
        startDate: String,
        endDate: String,
        budget: String
    ) {
        val start = parseDate(startDate) ?: return
        val end = parseDate(endDate) ?: return

        val normalizedDestination = destination.trim()
        val normalizedCountry = country.trim()
        val normalizedBudget = budget.trim()

        val durationDays = calculateTripDays(start, end)
        val tripId = buildTripId(normalizedDestination)
        val imageRes = resolveTripImage(normalizedDestination, normalizedCountry)

        val newTrip = TripItem(
            id = tripId,
            destination = normalizedDestination,
            country = normalizedCountry,
            dateRange = formatDateRange(start, end),
            duration = formatDuration(durationDays),
            budget = "€$normalizedBudget",
            statusLabel = "Planned",
            state = TripState.PLANNED,
            image = imageRes
        )

        repository.addTrip(newTrip)
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
        val diff = ChronoUnit.DAYS.between(start, end).toInt()
        return if (diff <= 0) 1 else diff
    }

    private fun formatDuration(days: Int): String {
        return if (days == 1) "1 day" else "$days days"
    }

    private fun buildTripId(destination: String): String {
        val base = destination
            .trim()
            .lowercase(Locale.ENGLISH)
            .replace(Regex("[^a-z0-9]+"), "")
            .ifBlank { "trip" }

        var candidate = base
        var counter = 1
        val existingIds = repository.getAllTrips().map { it.id }.toSet()

        while (candidate in existingIds) {
            counter++
            candidate = "$base$counter"
        }

        return candidate
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
}