package com.example.voyagetime.data.repository

import com.example.voyagetime.data.source.FakeItineraryDataSource
import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent
import com.example.voyagetime.ui.screens.TripItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ItineraryRepositoryImpl(
    private val tripRepository: TripRepository = TripRepositoryImpl()
) : ItineraryRepository {

    override fun getTripDays(tripId: String): List<ItineraryDayData> {
        val stored = FakeItineraryDataSource.getStoredTripDaysOrNull(tripId)
        if (stored != null) {
            return stored.map { it.deepCopy() }
        }

        val trip = tripRepository.getAllTrips().firstOrNull { it.id == tripId }
            ?: return emptyList()

        val emptyDays = createEmptyDaysForTrip(trip)
        FakeItineraryDataSource.replaceTripDays(tripId, emptyDays)

        return emptyDays.map { it.deepCopy() }
    }

    override fun addMorningEvent(tripId: String, dayIndex: Int, event: ItineraryEvent) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices) {
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(
                morningPlan = updatedDays[dayIndex].morningPlan.toMutableList().apply { add(event) }
            )
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    override fun updateMorningEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices && eventIndex in updatedDays[dayIndex].morningPlan.indices) {
            val newPlan = updatedDays[dayIndex].morningPlan.toMutableList()
            newPlan[eventIndex] = event
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(morningPlan = newPlan)
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    override fun deleteMorningEvent(tripId: String, dayIndex: Int, eventIndex: Int) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices && eventIndex in updatedDays[dayIndex].morningPlan.indices) {
            val newPlan = updatedDays[dayIndex].morningPlan.toMutableList()
            newPlan.removeAt(eventIndex)
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(morningPlan = newPlan)
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    override fun addAfternoonEvent(tripId: String, dayIndex: Int, event: ItineraryEvent) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices) {
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(
                afternoonPlan = updatedDays[dayIndex].afternoonPlan.toMutableList().apply { add(event) }
            )
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    override fun updateAfternoonEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices && eventIndex in updatedDays[dayIndex].afternoonPlan.indices) {
            val newPlan = updatedDays[dayIndex].afternoonPlan.toMutableList()
            newPlan[eventIndex] = event
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(afternoonPlan = newPlan)
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    override fun deleteAfternoonEvent(tripId: String, dayIndex: Int, eventIndex: Int) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices && eventIndex in updatedDays[dayIndex].afternoonPlan.indices) {
            val newPlan = updatedDays[dayIndex].afternoonPlan.toMutableList()
            newPlan.removeAt(eventIndex)
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(afternoonPlan = newPlan)
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    override fun addEveningEvent(tripId: String, dayIndex: Int, event: ItineraryEvent) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices) {
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(
                eveningPlan = updatedDays[dayIndex].eveningPlan.toMutableList().apply { add(event) }
            )
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    override fun updateEveningEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices && eventIndex in updatedDays[dayIndex].eveningPlan.indices) {
            val newPlan = updatedDays[dayIndex].eveningPlan.toMutableList()
            newPlan[eventIndex] = event
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(eveningPlan = newPlan)
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    override fun deleteEveningEvent(tripId: String, dayIndex: Int, eventIndex: Int) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices && eventIndex in updatedDays[dayIndex].eveningPlan.indices) {
            val newPlan = updatedDays[dayIndex].eveningPlan.toMutableList()
            newPlan.removeAt(eventIndex)
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(eveningPlan = newPlan)
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    override fun updateNotes(tripId: String, dayIndex: Int, notes: String) {
        val updatedDays = getTripDays(tripId).toMutableList()
        if (dayIndex in updatedDays.indices) {
            updatedDays[dayIndex] = updatedDays[dayIndex].copy(notes = notes)
            FakeItineraryDataSource.replaceTripDays(tripId, updatedDays)
        }
    }

    private fun createEmptyDaysForTrip(trip: TripItem): MutableList<ItineraryDayData> {
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
        }.toMutableList()
    }

    private fun buildTripDates(trip: TripItem): List<String> {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val parsedRange = parseDateRange(trip.dateRange)
        if (parsedRange != null) {
            val (start, end) = parsedRange
            val dates = mutableListOf<String>()
            var current = start
            while (!current.isAfter(end)) {
                dates.add(current.format(formatter))
                current = current.plusDays(1)
            }
            if (dates.isNotEmpty()) return dates
        }

        val totalDays = trip.duration.substringBefore(" ").toIntOrNull() ?: 1
        return List(totalDays.coerceAtLeast(1)) { index ->
            "Day ${index + 1}"
        }
    }

    private fun parseDateRange(dateRange: String): Pair<LocalDate, LocalDate>? {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val parts = dateRange.split("-").map { it.trim() }
        if (parts.size != 2) return null

        val startRaw = parts[0]
        val endRaw = parts[1]

        return try {
            val start = LocalDate.parse(startRaw, formatter)
            val end = LocalDate.parse(endRaw, formatter)
            start to end
        } catch (_: DateTimeParseException) {
            null
        }
    }

    private fun ItineraryDayData.deepCopy(): ItineraryDayData {
        return copy(
            morningPlan = morningPlan.toMutableList(),
            afternoonPlan = afternoonPlan.toMutableList(),
            eveningPlan = eveningPlan.toMutableList()
        )
    }
}