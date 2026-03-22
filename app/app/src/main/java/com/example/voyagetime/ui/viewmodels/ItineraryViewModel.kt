package com.example.voyagetime.ui.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Tour
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.voyagetime.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ItineraryEvent(
    val time: String,
    val title: String,
    val location: String,
    val cost: String,
    val icon: ImageVector
)

data class ItinerarySummary(
    val destination: String,
    val dateRange: String,
    val totalDays: String,
    val estimatedBudget: String,
    val imageRes: Int,
    val status: String
)

class ItineraryDayData(
    val dayLabel: String,
    val dayDate: String,
    val morningPlan: SnapshotStateList<ItineraryEvent>,
    val afternoonPlan: SnapshotStateList<ItineraryEvent>,
    val eveningPlan: SnapshotStateList<ItineraryEvent>,
    initialNotes: String
) {
    var notes by mutableStateOf(initialNotes)
}

enum class EditSection {
    MORNING,
    AFTERNOON,
    EVENING,
    NOTES
}

enum class FormMode {
    EDIT,
    ADD
}

data class IconOption(
    val label: String,
    val icon: ImageVector
)

class ItineraryViewModel(
    private val tripId: String
) : ViewModel() {

    private val initialData: Pair<ItinerarySummary, List<ItineraryDayData>> =
        when (tripId) {
            "tokyo" -> createTokyoTripData()
            "barcelona" -> createBarcelonaTripData()
            "newyork" -> createNewYorkTripData()
            else -> createParisTripData()
        }

    val summary: ItinerarySummary = initialData.first

    private val _days = MutableStateFlow(initialData.second)
    val days: StateFlow<List<ItineraryDayData>> = _days

    val isCompletedTrip: Boolean =
        tripId == "barcelona" || tripId == "newyork"

    fun addEvent(dayIndex: Int, section: EditSection, event: ItineraryEvent) {
        val day = _days.value.getOrNull(dayIndex) ?: return

        when (section) {
            EditSection.MORNING -> day.morningPlan.add(event)
            EditSection.AFTERNOON -> day.afternoonPlan.add(event)
            EditSection.EVENING -> day.eveningPlan.add(event)
            EditSection.NOTES -> return
        }

        notifyUi()
    }

    fun updateEvent(dayIndex: Int, section: EditSection, eventIndex: Int, event: ItineraryEvent) {
        val day = _days.value.getOrNull(dayIndex) ?: return

        when (section) {
            EditSection.MORNING -> if (eventIndex in day.morningPlan.indices) day.morningPlan[eventIndex] = event
            EditSection.AFTERNOON -> if (eventIndex in day.afternoonPlan.indices) day.afternoonPlan[eventIndex] = event
            EditSection.EVENING -> if (eventIndex in day.eveningPlan.indices) day.eveningPlan[eventIndex] = event
            EditSection.NOTES -> return
        }

        notifyUi()
    }

    fun deleteEvent(dayIndex: Int, section: EditSection, eventIndex: Int) {
        val day = _days.value.getOrNull(dayIndex) ?: return

        when (section) {
            EditSection.MORNING -> if (eventIndex in day.morningPlan.indices) day.morningPlan.removeAt(eventIndex)
            EditSection.AFTERNOON -> if (eventIndex in day.afternoonPlan.indices) day.afternoonPlan.removeAt(eventIndex)
            EditSection.EVENING -> if (eventIndex in day.eveningPlan.indices) day.eveningPlan.removeAt(eventIndex)
            EditSection.NOTES -> return
        }

        notifyUi()
    }

    fun updateNotes(dayIndex: Int, notes: String) {
        val day = _days.value.getOrNull(dayIndex) ?: return
        day.notes = notes
        notifyUi()
    }

    private fun notifyUi() {
        _days.update { current -> current.toList() }
    }
}

class ItineraryViewModelFactory(
    private val tripId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItineraryViewModel::class.java)) {
            return ItineraryViewModel(tripId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

private fun createParisTripData(): Pair<ItinerarySummary, List<ItineraryDayData>> {
    val summary = ItinerarySummary(
        destination = "Paris, France",
        dateRange = "12 Jun - 18 Jun 2026",
        totalDays = "6 days",
        estimatedBudget = "€820",
        imageRes = R.drawable.paris,
        status = "Upcoming"
    )

    val days = listOf(
        ItineraryDayData(
            dayLabel = "Day 1",
            dayDate = "13 Jun 2026",
            morningPlan = mutableStateListOf(
                ItineraryEvent("08:00", "Breakfast at Café de Flore", "Saint-Germain-des-Prés", "€14", Icons.Default.Restaurant),
                ItineraryEvent("10:00", "Visit Louvre Museum", "Rue de Rivoli", "€22", Icons.Default.Tour)
            ),
            afternoonPlan = mutableStateListOf(
                ItineraryEvent("13:00", "Lunch near Tuileries", "1st arrondissement", "€18", Icons.Default.Restaurant),
                ItineraryEvent("15:30", "Seine River Walk", "Pont Neuf", "Free", Icons.Default.Map)
            ),
            eveningPlan = mutableStateListOf(
                ItineraryEvent("19:00", "Eiffel Tower Visit", "Champ de Mars", "€26", Icons.Default.Place)
            ),
            initialNotes = "Buy museum ticket online before arrival."
        ),
        ItineraryDayData(
            dayLabel = "Day 2",
            dayDate = "14 Jun 2026",
            morningPlan = mutableStateListOf(
                ItineraryEvent("09:00", "Coffee and croissant", "Le Marais", "€9", Icons.Default.Restaurant)
            ),
            afternoonPlan = mutableStateListOf(
                ItineraryEvent("14:00", "Notre-Dame area visit", "Île de la Cité", "Free", Icons.Default.Place)
            ),
            eveningPlan = mutableStateListOf(
                ItineraryEvent("19:30", "Dinner in Latin Quarter", "Latin Quarter", "€24", Icons.Default.Restaurant)
            ),
            initialNotes = "Metro is the fastest option for moving between areas."
        )
    )

    return summary to days
}

private fun createTokyoTripData(): Pair<ItinerarySummary, List<ItineraryDayData>> {
    val summary = ItinerarySummary(
        destination = "Tokyo, Japan",
        dateRange = "02 Aug - 11 Aug 2026",
        totalDays = "9 days",
        estimatedBudget = "€2,450",
        imageRes = R.drawable.tokyo,
        status = "Planned"
    )

    val days = listOf(
        ItineraryDayData(
            dayLabel = "Day 1",
            dayDate = "03 Aug 2026",
            morningPlan = mutableStateListOf(
                ItineraryEvent("08:30", "Breakfast in Shibuya", "Shibuya Station Area", "€12", Icons.Default.Restaurant),
                ItineraryEvent("10:30", "Meiji Shrine Visit", "Shibuya", "Free", Icons.Default.Tour)
            ),
            afternoonPlan = mutableStateListOf(
                ItineraryEvent("13:00", "Lunch in Harajuku", "Takeshita Street", "€18", Icons.Default.Restaurant),
                ItineraryEvent("15:00", "Tokyo Skytree", "Sumida", "€24", Icons.Default.Place)
            ),
            eveningPlan = mutableStateListOf(
                ItineraryEvent("19:30", "Dinner in Akihabara", "Akihabara", "€32", Icons.Default.Map)
            ),
            initialNotes = "Use metro card. Start early to avoid queues."
        ),
        ItineraryDayData(
            dayLabel = "Day 2",
            dayDate = "04 Aug 2026",
            morningPlan = mutableStateListOf(
                ItineraryEvent("09:00", "Ueno Park Walk", "Ueno", "Free", Icons.Default.Map)
            ),
            afternoonPlan = mutableStateListOf(
                ItineraryEvent("13:00", "Tokyo National Museum", "Ueno", "€16", Icons.Default.Tour)
            ),
            eveningPlan = mutableStateListOf(
                ItineraryEvent("19:00", "Dinner in Ginza", "Ginza", "€28", Icons.Default.Restaurant)
            ),
            initialNotes = "Comfortable shoes recommended."
        )
    )

    return summary to days
}

private fun createBarcelonaTripData(): Pair<ItinerarySummary, List<ItineraryDayData>> {
    val summary = ItinerarySummary(
        destination = "Barcelona, Spain",
        dateRange = "10 Mar - 13 Mar 2026",
        totalDays = "3 days",
        estimatedBudget = "€290",
        imageRes = R.drawable.barcelona,
        status = "Completed"
    )

    val days = listOf(
        ItineraryDayData(
            dayLabel = "Day 1",
            dayDate = "10 Mar 2026",
            morningPlan = mutableStateListOf(
                ItineraryEvent("09:00", "Sagrada Família Visit", "Eixample", "€26", Icons.Default.Tour)
            ),
            afternoonPlan = mutableStateListOf(
                ItineraryEvent("13:30", "Lunch near Gothic Quarter", "Ciutat Vella", "€18", Icons.Default.Restaurant)
            ),
            eveningPlan = mutableStateListOf(
                ItineraryEvent("18:30", "Walk at Barceloneta", "Barcelona", "Free", Icons.Default.Place)
            ),
            initialNotes = "Completed trip."
        )
    )

    return summary to days
}

private fun createNewYorkTripData(): Pair<ItinerarySummary, List<ItineraryDayData>> {
    val summary = ItinerarySummary(
        destination = "New York, United States",
        dateRange = "04 Dec - 10 Dec 2025",
        totalDays = "6 days",
        estimatedBudget = "€1,680",
        imageRes = R.drawable.newyork,
        status = "Completed"
    )

    val days = listOf(
        ItineraryDayData(
            dayLabel = "Day 1",
            dayDate = "04 Dec 2025",
            morningPlan = mutableStateListOf(
                ItineraryEvent("09:30", "Central Park Walk", "Manhattan", "Free", Icons.Default.Map)
            ),
            afternoonPlan = mutableStateListOf(
                ItineraryEvent("13:00", "Lunch near 5th Avenue", "Midtown", "€22", Icons.Default.Restaurant)
            ),
            eveningPlan = mutableStateListOf(
                ItineraryEvent("19:00", "Times Square Visit", "Manhattan", "Free", Icons.Default.Place)
            ),
            initialNotes = "Completed trip."
        )
    )

    return summary to days
}