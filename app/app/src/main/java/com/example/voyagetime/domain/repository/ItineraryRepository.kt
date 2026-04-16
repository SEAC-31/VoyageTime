package com.example.voyagetime.domain.repository

import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent

interface ItineraryRepository {
    fun getTripDays(tripId: String): List<ItineraryDayData>

    fun addMorningEvent(tripId: String, dayIndex: Int, event: ItineraryEvent)
    fun updateMorningEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent)
    fun deleteMorningEvent(tripId: String, dayIndex: Int, eventIndex: Int)

    fun addAfternoonEvent(tripId: String, dayIndex: Int, event: ItineraryEvent)
    fun updateAfternoonEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent)
    fun deleteAfternoonEvent(tripId: String, dayIndex: Int, eventIndex: Int)

    fun addEveningEvent(tripId: String, dayIndex: Int, event: ItineraryEvent)
    fun updateEveningEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent)
    fun deleteEveningEvent(tripId: String, dayIndex: Int, eventIndex: Int)

    fun updateNotes(tripId: String, dayIndex: Int, notes: String)
}