package com.example.voyagetime.data.repository

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import com.example.voyagetime.data.local.dao.ItineraryItemDao
import com.example.voyagetime.data.local.entity.ItineraryItemEntity
import com.example.voyagetime.domain.repository.ItineraryRepository
import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ItineraryRepositoryImpl(
    private val itineraryItemDao: ItineraryItemDao
) : ItineraryRepository {

    override fun getTripDays(tripId: String): Flow<List<ItineraryDayData>> {
        val id = tripId.toLongOrNull() ?: return flowOf(emptyList())

        return itineraryItemDao.getItemsByTrip(id).map { items ->
            if (items.isEmpty()) {
                emptyList()
            } else {
                items
                    .groupBy { it.dayNumber }
                    .toSortedMap()
                    .map { (dayNumber, dayItems) ->
                        val morning = dayItems
                            .filter { it.section.equals(SECTION_MORNING, ignoreCase = true) }
                            .sortedBy { it.scheduledAt }
                            .map { it.toItineraryEvent() }
                            .toMutableList()

                        val afternoon = dayItems
                            .filter { it.section.equals(SECTION_AFTERNOON, ignoreCase = true) }
                            .sortedBy { it.scheduledAt }
                            .map { it.toItineraryEvent() }
                            .toMutableList()

                        val evening = dayItems
                            .filter { it.section.equals(SECTION_EVENING, ignoreCase = true) }
                            .sortedBy { it.scheduledAt }
                            .map { it.toItineraryEvent() }
                            .toMutableList()

                        val notes = dayItems
                            .firstOrNull { it.section.equals(SECTION_NOTES, ignoreCase = true) }
                            ?.notes
                            .orEmpty()

                        ItineraryDayData(
                            dayLabel = "Day $dayNumber",
                            dayDate = dayItems.first().scheduledAt.toLocalDate()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                            morningPlan = morning,
                            afternoonPlan = afternoon,
                            eveningPlan = evening,
                            notes = notes
                        )
                    }
            }
        }
    }

    override suspend fun addMorningEvent(
        tripId: String,
        dayIndex: Int,
        event: ItineraryEvent
    ) {
        insertEvent(tripId, dayIndex + 1, SECTION_MORNING, event)
    }

    override suspend fun updateMorningEvent(
        tripId: String,
        dayIndex: Int,
        eventIndex: Int,
        event: ItineraryEvent
    ) {
        updateEventAtIndex(tripId, dayIndex + 1, SECTION_MORNING, eventIndex, event)
    }

    override suspend fun deleteMorningEvent(
        tripId: String,
        dayIndex: Int,
        eventIndex: Int
    ) {
        deleteEventAtIndex(tripId, dayIndex + 1, SECTION_MORNING, eventIndex)
    }

    override suspend fun addAfternoonEvent(
        tripId: String,
        dayIndex: Int,
        event: ItineraryEvent
    ) {
        insertEvent(tripId, dayIndex + 1, SECTION_AFTERNOON, event)
    }

    override suspend fun updateAfternoonEvent(
        tripId: String,
        dayIndex: Int,
        eventIndex: Int,
        event: ItineraryEvent
    ) {
        updateEventAtIndex(tripId, dayIndex + 1, SECTION_AFTERNOON, eventIndex, event)
    }

    override suspend fun deleteAfternoonEvent(
        tripId: String,
        dayIndex: Int,
        eventIndex: Int
    ) {
        deleteEventAtIndex(tripId, dayIndex + 1, SECTION_AFTERNOON, eventIndex)
    }

    override suspend fun addEveningEvent(
        tripId: String,
        dayIndex: Int,
        event: ItineraryEvent
    ) {
        insertEvent(tripId, dayIndex + 1, SECTION_EVENING, event)
    }

    override suspend fun updateEveningEvent(
        tripId: String,
        dayIndex: Int,
        eventIndex: Int,
        event: ItineraryEvent
    ) {
        updateEventAtIndex(tripId, dayIndex + 1, SECTION_EVENING, eventIndex, event)
    }

    override suspend fun deleteEveningEvent(
        tripId: String,
        dayIndex: Int,
        eventIndex: Int
    ) {
        deleteEventAtIndex(tripId, dayIndex + 1, SECTION_EVENING, eventIndex)
    }

    override suspend fun updateNotes(
        tripId: String,
        dayIndex: Int,
        notes: String
    ) {
        val id = tripId.toLongOrNull() ?: return
        val dayNumber = dayIndex + 1

        val existingItems = itineraryItemDao.getItemsByTripOnce(id)

        val notesItem = existingItems.firstOrNull {
            it.dayNumber == dayNumber && it.section.equals(SECTION_NOTES, ignoreCase = true)
        }

        if (notesItem != null) {
            itineraryItemDao.updateItem(notesItem.copy(notes = notes))
        } else {
            itineraryItemDao.insertItem(
                ItineraryItemEntity(
                    tripId = id,
                    dayNumber = dayNumber,
                    section = SECTION_NOTES,
                    title = "",
                    location = "",
                    costAmount = 0,
                    scheduledAt = buildScheduledAt(dayNumber, "00:00"),
                    notes = notes
                )
            )
        }

        Log.i(TAG, "Notes updated for trip=$tripId day=$dayNumber")
    }

    private suspend fun insertEvent(
        tripId: String,
        dayNumber: Int,
        section: String,
        event: ItineraryEvent
    ) {
        val id = tripId.toLongOrNull() ?: return

        itineraryItemDao.insertItem(
            event.toEntity(
                tripId = id,
                dayNumber = dayNumber,
                section = section
            )
        )

        Log.i(TAG, "Event inserted trip=$tripId day=$dayNumber section=$section")
    }

    private suspend fun updateEventAtIndex(
        tripId: String,
        dayNumber: Int,
        section: String,
        eventIndex: Int,
        event: ItineraryEvent
    ) {
        val id = tripId.toLongOrNull() ?: return

        val target = itineraryItemDao.getItemsByTripOnce(id)
            .filter {
                it.dayNumber == dayNumber &&
                        it.section.equals(section, ignoreCase = true)
            }
            .sortedBy { it.scheduledAt }
            .getOrNull(eventIndex)

        if (target == null) {
            Log.e(TAG, "Event not found for update: trip=$tripId day=$dayNumber section=$section index=$eventIndex")
            return
        }

        itineraryItemDao.updateItem(
            event.toEntity(
                tripId = id,
                dayNumber = dayNumber,
                section = section,
                existingId = target.id
            )
        )

        Log.i(TAG, "Event updated id=${target.id}")
    }

    private suspend fun deleteEventAtIndex(
        tripId: String,
        dayNumber: Int,
        section: String,
        eventIndex: Int
    ) {
        val id = tripId.toLongOrNull() ?: return

        val target = itineraryItemDao.getItemsByTripOnce(id)
            .filter {
                it.dayNumber == dayNumber &&
                        it.section.equals(section, ignoreCase = true)
            }
            .sortedBy { it.scheduledAt }
            .getOrNull(eventIndex)

        if (target == null) {
            Log.e(TAG, "Event not found for deletion: trip=$tripId day=$dayNumber section=$section index=$eventIndex")
            return
        }

        itineraryItemDao.deleteItem(target)
        Log.i(TAG, "Event deleted id=${target.id}")
    }

    private fun ItineraryItemEntity.toItineraryEvent(): ItineraryEvent {
        val costText = if (costAmount == 0) "Free" else "€$costAmount"

        return ItineraryEvent(
            time = scheduledAt.format(DateTimeFormatter.ofPattern("HH:mm")),
            title = title,
            location = location,
            cost = costText,
            icon = Icons.Default.Place
        )
    }

    private fun ItineraryEvent.toEntity(
        tripId: Long,
        dayNumber: Int,
        section: String,
        existingId: Long = 0L
    ): ItineraryItemEntity {
        return ItineraryItemEntity(
            id = existingId,
            tripId = tripId,
            dayNumber = dayNumber,
            section = section,
            title = title.trim(),
            location = location.trim(),
            costAmount = parseCost(cost),
            scheduledAt = buildScheduledAt(dayNumber, time),
            notes = null
        )
    }

    private fun parseCost(value: String): Int {
        return value
            .replace("€", "")
            .replace("Free", "0", ignoreCase = true)
            .replace(",", "")
            .trim()
            .toIntOrNull() ?: 0
    }

    private fun buildScheduledAt(dayNumber: Int, time: String): LocalDateTime {
        val parsedTime = parseTime(time)
        val date = LocalDate.now().plusDays((dayNumber - 1).toLong())
        return LocalDateTime.of(date, parsedTime)
    }

    private fun parseTime(value: String): LocalTime {
        val parts = value.split(":")
        val hour = parts.getOrNull(0)?.trim()?.toIntOrNull()?.coerceIn(0, 23) ?: 0
        val minute = parts.getOrNull(1)?.trim()?.toIntOrNull()?.coerceIn(0, 59) ?: 0

        return LocalTime.of(hour, minute)
    }

    companion object {
        private const val TAG = "ItineraryRepository"

        private const val SECTION_MORNING = "MORNING"
        private const val SECTION_AFTERNOON = "AFTERNOON"
        private const val SECTION_EVENING = "EVENING"
        private const val SECTION_NOTES = "NOTES"
    }
}