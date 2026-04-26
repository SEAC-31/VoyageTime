package com.example.voyagetime.data.repository

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import com.example.voyagetime.data.local.dao.ItineraryItemDao
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.entity.ItineraryItemEntity
import com.example.voyagetime.domain.repository.ItineraryRepository
import com.example.voyagetime.ui.screens.ItineraryDayData
import com.example.voyagetime.ui.screens.ItineraryEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ItineraryRepositoryImpl(
    private val itineraryItemDao: ItineraryItemDao,
    private val tripDao: TripDao
) : ItineraryRepository {

    // ── Queries ───────────────────────────────────────────────────────────────

    /**
     * Devuelve los días del itinerario de un viaje como Flow reactivo.
     * Los items de Room (planos, uno por evento) se agrupan por día y sección
     * para reconstruir la estructura ItineraryDayData que consume la UI.
     */
    override fun getTripDays(tripId: String): Flow<List<ItineraryDayData>> {
        val id = tripId.toLongOrNull() ?: return kotlinx.coroutines.flow.flowOf(emptyList())

        return itineraryItemDao.getItemsByTrip(id).map { items ->
            if (items.isEmpty()) return@map emptyList()

            // Agrupar por dayNumber y reconstruir la estructura de días
            val grouped = items.groupBy { it.dayNumber }.toSortedMap()

            grouped.map { (dayNumber, dayItems) ->
                val morning   = dayItems.filter { it.section.uppercase() == "MORNING" }
                    .sortedBy { it.scheduledAt }.map { it.toItineraryEvent() }.toMutableList()
                val afternoon = dayItems.filter { it.section.uppercase() == "AFTERNOON" }
                    .sortedBy { it.scheduledAt }.map { it.toItineraryEvent() }.toMutableList()
                val evening   = dayItems.filter { it.section.uppercase() == "EVENING" }
                    .sortedBy { it.scheduledAt }.map { it.toItineraryEvent() }.toMutableList()

                // La nota del día la guardamos en el primer item del día con sección "NOTES"
                val notesItem = dayItems.firstOrNull { it.section.uppercase() == "NOTES" }
                val notes = notesItem?.notes ?: ""

                // La fecha del día la calculamos a partir del primer item
                val dayDate = dayItems.first().scheduledAt.toLocalDate()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                ItineraryDayData(
                    dayLabel  = "Day $dayNumber",
                    dayDate   = dayDate,
                    morningPlan   = morning,
                    afternoonPlan = afternoon,
                    eveningPlan   = evening,
                    notes = notes
                )
            }
        }
    }

    // ── Mutations: Morning ────────────────────────────────────────────────────

    override suspend fun addMorningEvent(tripId: String, dayIndex: Int, event: ItineraryEvent) {
        insertEvent(tripId, dayIndex + 1, "MORNING", event)
    }

    override suspend fun updateMorningEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        updateEventAtIndex(tripId, dayIndex + 1, "MORNING", eventIndex, event)
    }

    override suspend fun deleteMorningEvent(tripId: String, dayIndex: Int, eventIndex: Int) {
        deleteEventAtIndex(tripId, dayIndex + 1, "MORNING", eventIndex)
    }

    // ── Mutations: Afternoon ──────────────────────────────────────────────────

    override suspend fun addAfternoonEvent(tripId: String, dayIndex: Int, event: ItineraryEvent) {
        insertEvent(tripId, dayIndex + 1, "AFTERNOON", event)
    }

    override suspend fun updateAfternoonEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        updateEventAtIndex(tripId, dayIndex + 1, "AFTERNOON", eventIndex, event)
    }

    override suspend fun deleteAfternoonEvent(tripId: String, dayIndex: Int, eventIndex: Int) {
        deleteEventAtIndex(tripId, dayIndex + 1, "AFTERNOON", eventIndex)
    }

    // ── Mutations: Evening ────────────────────────────────────────────────────

    override suspend fun addEveningEvent(tripId: String, dayIndex: Int, event: ItineraryEvent) {
        insertEvent(tripId, dayIndex + 1, "EVENING", event)
    }

    override suspend fun updateEveningEvent(tripId: String, dayIndex: Int, eventIndex: Int, event: ItineraryEvent) {
        updateEventAtIndex(tripId, dayIndex + 1, "EVENING", eventIndex, event)
    }

    override suspend fun deleteEveningEvent(tripId: String, dayIndex: Int, eventIndex: Int) {
        deleteEventAtIndex(tripId, dayIndex + 1, "EVENING", eventIndex)
    }

    // ── Mutations: Notes ──────────────────────────────────────────────────────

    /**
     * Las notas se persisten como un ItineraryItemEntity especial con section="NOTES".
     * Si ya existe uno para ese día, lo actualiza; si no, lo inserta.
     */
    override suspend fun updateNotes(tripId: String, dayIndex: Int, notes: String) {
        val id = tripId.toLongOrNull() ?: return
        val dayNumber = dayIndex + 1

        val existingItems = itineraryItemDao.getItemsByTripOnce(id)
        val notesItem = existingItems.firstOrNull {
            it.dayNumber == dayNumber && it.section.uppercase() == "NOTES"
        }

        if (notesItem != null) {
            itineraryItemDao.updateItem(notesItem.copy(notes = notes))
        } else {
            itineraryItemDao.insertItem(
                ItineraryItemEntity(
                    tripId      = id,
                    dayNumber   = dayNumber,
                    section     = "NOTES",
                    title       = "",
                    location    = "",
                    costAmount  = 0,
                    scheduledAt = LocalDateTime.now(),
                    notes       = notes
                )
            )
        }
        Log.i(TAG, "Notes updated for trip=$tripId day=$dayNumber")
    }

    // ── Helpers privados ──────────────────────────────────────────────────────

    private suspend fun insertEvent(tripId: String, dayNumber: Int, section: String, event: ItineraryEvent) {
        val id = tripId.toLongOrNull() ?: return
        val entity = event.toEntity(tripId = id, dayNumber = dayNumber, section = section)
        itineraryItemDao.insertItem(entity)
        Log.i(TAG, "Event inserted: trip=$tripId day=$dayNumber section=$section title=${event.title}")
    }

    private suspend fun updateEventAtIndex(
        tripId: String, dayNumber: Int, section: String, eventIndex: Int, event: ItineraryEvent
    ) {
        val id = tripId.toLongOrNull() ?: return
        val items = itineraryItemDao.getItemsByTripOnce(id)
            .filter { it.dayNumber == dayNumber && it.section.uppercase() == section }
            .sortedBy { it.scheduledAt }

        val target = items.getOrNull(eventIndex)
        if (target == null) {
            Log.e(TAG, "updateEvent: index $eventIndex not found in $section day=$dayNumber trip=$tripId")
            return
        }
        itineraryItemDao.updateItem(event.toEntity(tripId = id, dayNumber = dayNumber, section = section, existingId = target.id))
        Log.i(TAG, "Event updated: id=${target.id} title=${event.title}")
    }

    private suspend fun deleteEventAtIndex(tripId: String, dayNumber: Int, section: String, eventIndex: Int) {
        val id = tripId.toLongOrNull() ?: return
        val items = itineraryItemDao.getItemsByTripOnce(id)
            .filter { it.dayNumber == dayNumber && it.section.uppercase() == section }
            .sortedBy { it.scheduledAt }

        val target = items.getOrNull(eventIndex)
        if (target == null) {
            Log.e(TAG, "deleteEvent: index $eventIndex not found in $section day=$dayNumber trip=$tripId")
            return
        }
        itineraryItemDao.deleteItem(target)
        Log.i(TAG, "Event deleted: id=${target.id} from $section day=$dayNumber trip=$tripId")
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private fun ItineraryItemEntity.toItineraryEvent(): ItineraryEvent {
        val timeStr = scheduledAt.format(DateTimeFormatter.ofPattern("HH:mm"))
        val costStr = if (costAmount == 0) "Free" else "€$costAmount"
        return ItineraryEvent(
            time     = timeStr,
            title    = title,
            location = location,
            cost     = costStr,
            icon     = Icons.Default.Place   // El icono no se persiste en BD; se usa un default
        )
    }

    private fun ItineraryEvent.toEntity(
        tripId: Long,
        dayNumber: Int,
        section: String,
        existingId: Long = 0L
    ): ItineraryItemEntity {
        val costVal = cost.replace("€", "").replace("Free", "0").trim().toIntOrNull() ?: 0

        // Parsear la hora del evento para construir el LocalDateTime del día correcto
        val timeParts = time.split(":").map { it.trim().toIntOrNull() ?: 0 }
        val hour   = timeParts.getOrNull(0) ?: 0
        val minute = timeParts.getOrNull(1) ?: 0
        val scheduledAt = LocalDateTime.now()
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)
            .withNano(0)

        return ItineraryItemEntity(
            id          = existingId,
            tripId      = tripId,
            dayNumber   = dayNumber,
            section     = section,
            title       = title,
            location    = location,
            costAmount  = costVal,
            scheduledAt = scheduledAt
        )
    }

    companion object {
        private const val TAG = "ItineraryRepository"
    }
}