package com.example.voyagetime.domain.repository

import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent
import kotlinx.coroutines.flow.Flow

interface ItineraryRepository {
    fun getTripDays(tripId: String): Flow<List<ItineraryDayData>>

    suspend fun addMorningEvent(tripId: String, dayIndex: Int, event: ItineraryEvent)
    suspend fun updateMorningEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent)
    suspend fun deleteMorningEvent(tripId: String, dayIndex: Int, eventIndex: Int)

    suspend fun addAfternoonEvent(tripId: String, dayIndex: Int, event: ItineraryEvent)
    suspend fun updateAfternoonEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent)
    suspend fun deleteAfternoonEvent(tripId: String, dayIndex: Int, eventIndex: Int)

    suspend fun addEveningEvent(tripId: String, dayIndex: Int, event: ItineraryEvent)
    suspend fun updateEveningEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent)
    suspend fun deleteEveningEvent(tripId: String, dayIndex: Int, eventIndex: Int)

    suspend fun updateNotes(tripId: String, dayIndex: Int, notes: String)
}