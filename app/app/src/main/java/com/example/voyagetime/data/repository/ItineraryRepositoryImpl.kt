package com.example.voyagetime.data.repository

import com.example.voyagetime.data.source.FakeItineraryDataSource
import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent

class ItineraryRepositoryImpl : ItineraryRepository {

    override fun getTripDays(tripId: String): List<ItineraryDayData> {
        return FakeItineraryDataSource.getTripDays(tripId).map { day ->
            day.copy(
                morningPlan = day.morningPlan.toMutableList(),
                afternoonPlan = day.afternoonPlan.toMutableList(),
                eveningPlan = day.eveningPlan.toMutableList()
            )
        }
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
}