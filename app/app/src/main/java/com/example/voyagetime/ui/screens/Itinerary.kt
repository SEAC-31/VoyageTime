package com.example.voyagetime.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tour
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.voyagetime.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

private enum class EditSection {
    MORNING,
    AFTERNOON,
    EVENING,
    NOTES
}

private enum class FormMode {
    EDIT,
    ADD
}

private data class IconOption(
    val label: String,
    val icon: ImageVector
)

@Composable
fun Itinerary(
    tripId: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val (summary, days) = remember(tripId) {
        when (tripId) {
            "tokyo" -> createTokyoTripData()
            "barcelona" -> createBarcelonaTripData()
            "newyork" -> createNewYorkTripData()
            else -> createParisTripData()
        }
    }

    val isCompletedTrip = tripId == "barcelona" || tripId == "newyork"

    val iconOptions = remember {
        listOf(
            IconOption("Restaurant", Icons.Default.Restaurant),
            IconOption("Tour", Icons.Default.Tour),
            IconOption("Place", Icons.Default.Place),
            IconOption("Map", Icons.Default.Map),
            IconOption("Train", Icons.Default.Train)
        )
    }

    var currentDayIndex by remember { mutableIntStateOf(0) }
    val currentDay = days[currentDayIndex]

    var editingSection by remember { mutableStateOf<EditSection?>(null) }
    var formMode by remember { mutableStateOf(FormMode.EDIT) }
    var editingEventIndex by remember { mutableIntStateOf(-1) }

    var draftTitle by remember { mutableStateOf("") }
    var draftLocation by remember { mutableStateOf("") }
    var draftTime by remember { mutableStateOf("") }
    var draftCost by remember { mutableStateOf("") }
    var draftIcon by remember { mutableStateOf(Icons.Default.Restaurant) }
    var draftNotes by remember { mutableStateOf("") }

    fun openEdit(section: EditSection, event: ItineraryEvent, index: Int) {
        editingSection = section
        formMode = FormMode.EDIT
        editingEventIndex = index
        draftTitle = event.title
        draftLocation = event.location
        draftTime = event.time
        draftCost = event.cost
        draftIcon = event.icon
    }

    fun openAdd(section: EditSection) {
        editingSection = section
        formMode = FormMode.ADD
        editingEventIndex = -1
        draftTitle = ""
        draftLocation = ""
        draftTime = ""
        draftCost = ""
        draftIcon = Icons.Default.Restaurant
    }

    fun closeForm() {
        editingSection = null
        editingEventIndex = -1
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ItineraryHeroHeader(summary = summary)

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            DayNavigator(
                currentDayLabel = currentDay.dayLabel,
                currentDate = currentDay.dayDate,
                canGoPrevious = currentDayIndex > 0,
                canGoNext = currentDayIndex < days.lastIndex,
                onPrevious = {
                    if (currentDayIndex > 0) {
                        currentDayIndex--
                        closeForm()
                    }
                },
                onNext = {
                    if (currentDayIndex < days.lastIndex) {
                        currentDayIndex++
                        closeForm()
                    }
                }
            )

            PlannerSection(
                title = stringResource(R.string.itinerary_morning),
                canAddEvent = !isCompletedTrip,
                onAddClick = { openAdd(EditSection.MORNING) }
            ) {
                currentDay.morningPlan.forEachIndexed { index, event ->
                    AgendaEventCard(
                        event = event,
                        showActionButtons = !isCompletedTrip,
                        onEditClick = { openEdit(EditSection.MORNING, event, index) },
                        onDeleteClick = {
                            currentDay.morningPlan.removeAt(index)
                            if (editingSection == EditSection.MORNING && editingEventIndex == index) {
                                closeForm()
                            }
                        }
                    )
                }

                if (editingSection == EditSection.MORNING && !isCompletedTrip) {
                    EventEditForm(
                        title = if (formMode == FormMode.ADD) stringResource(R.string.itinerary_add_morning) else stringResource(R.string.itinerary_edit_morning),
                        iconOptions = iconOptions,
                        selectedIcon = draftIcon,
                        onIconSelected = { draftIcon = it },
                        titleValue = draftTitle,
                        onTitleChange = { draftTitle = it },
                        locationValue = draftLocation,
                        onLocationChange = { draftLocation = it },
                        timeValue = draftTime,
                        onTimeChange = { draftTime = it },
                        costValue = draftCost,
                        onCostChange = { draftCost = it },
                        onCancel = { closeForm() },
                        onSave = {
                            val newEvent = ItineraryEvent(
                                time = draftTime,
                                title = draftTitle,
                                location = draftLocation,
                                cost = draftCost,
                                icon = draftIcon
                            )

                            if (formMode == FormMode.ADD) {
                                currentDay.morningPlan.add(newEvent)
                            } else if (editingEventIndex in currentDay.morningPlan.indices) {
                                currentDay.morningPlan[editingEventIndex] = newEvent
                            }

                            closeForm()
                        }
                    )
                }
            }

            PlannerSection(
                title = stringResource(R.string.itinerary_afternoon),
                canAddEvent = !isCompletedTrip,
                onAddClick = { openAdd(EditSection.AFTERNOON) }
            ) {
                currentDay.afternoonPlan.forEachIndexed { index, event ->
                    AgendaEventCard(
                        event = event,
                        showActionButtons = !isCompletedTrip,
                        onEditClick = { openEdit(EditSection.AFTERNOON, event, index) },
                        onDeleteClick = {
                            currentDay.afternoonPlan.removeAt(index)
                            if (editingSection == EditSection.AFTERNOON && editingEventIndex == index) {
                                closeForm()
                            }
                        }
                    )
                }

                if (editingSection == EditSection.AFTERNOON && !isCompletedTrip) {
                    EventEditForm(
                        title = if (formMode == FormMode.ADD) stringResource(R.string.itinerary_add_afternoon) else stringResource(R.string.itinerary_edit_afternoon),
                        iconOptions = iconOptions,
                        selectedIcon = draftIcon,
                        onIconSelected = { draftIcon = it },
                        titleValue = draftTitle,
                        onTitleChange = { draftTitle = it },
                        locationValue = draftLocation,
                        onLocationChange = { draftLocation = it },
                        timeValue = draftTime,
                        onTimeChange = { draftTime = it },
                        costValue = draftCost,
                        onCostChange = { draftCost = it },
                        onCancel = { closeForm() },
                        onSave = {
                            val newEvent = ItineraryEvent(
                                time = draftTime,
                                title = draftTitle,
                                location = draftLocation,
                                cost = draftCost,
                                icon = draftIcon
                            )

                            if (formMode == FormMode.ADD) {
                                currentDay.afternoonPlan.add(newEvent)
                            } else if (editingEventIndex in currentDay.afternoonPlan.indices) {
                                currentDay.afternoonPlan[editingEventIndex] = newEvent
                            }

                            closeForm()
                        }
                    )
                }
            }

            PlannerSection(
                title = stringResource(R.string.itinerary_evening),
                canAddEvent = !isCompletedTrip,
                onAddClick = { openAdd(EditSection.EVENING) }
            ) {
                currentDay.eveningPlan.forEachIndexed { index, event ->
                    AgendaEventCard(
                        event = event,
                        showActionButtons = !isCompletedTrip,
                        onEditClick = { openEdit(EditSection.EVENING, event, index) },
                        onDeleteClick = {
                            currentDay.eveningPlan.removeAt(index)
                            if (editingSection == EditSection.EVENING && editingEventIndex == index) {
                                closeForm()
                            }
                        }
                    )
                }

                if (editingSection == EditSection.EVENING && !isCompletedTrip) {
                    EventEditForm(
                        title = if (formMode == FormMode.ADD) stringResource(R.string.itinerary_add_evening) else stringResource(R.string.itinerary_edit_evening),
                        iconOptions = iconOptions,
                        selectedIcon = draftIcon,
                        onIconSelected = { draftIcon = it },
                        titleValue = draftTitle,
                        onTitleChange = { draftTitle = it },
                        locationValue = draftLocation,
                        onLocationChange = { draftLocation = it },
                        timeValue = draftTime,
                        onTimeChange = { draftTime = it },
                        costValue = draftCost,
                        onCostChange = { draftCost = it },
                        onCancel = { closeForm() },
                        onSave = {
                            val newEvent = ItineraryEvent(
                                time = draftTime,
                                title = draftTitle,
                                location = draftLocation,
                                cost = draftCost,
                                icon = draftIcon
                            )

                            if (formMode == FormMode.ADD) {
                                currentDay.eveningPlan.add(newEvent)
                            } else if (editingEventIndex in currentDay.eveningPlan.indices) {
                                currentDay.eveningPlan[editingEventIndex] = newEvent
                            }

                            closeForm()
                        }
                    )
                }
            }

            if (!isCompletedTrip) {
                PlannerNotesSection(
                    notes = currentDay.notes,
                    canEditNotes = true,
                    onEditClick = {
                        draftNotes = currentDay.notes
                        editingSection = EditSection.NOTES
                    }
                ) {
                    if (editingSection == EditSection.NOTES) {
                        NotesEditForm(
                            value = draftNotes,
                            onValueChange = { draftNotes = it },
                            onCancel = { closeForm() },
                            onSave = {
                                currentDay.notes = draftNotes
                                closeForm()
                            }
                        )
                    }
                }
            }
        }
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
            dayDate = "11 Mar 2026",
            morningPlan = mutableStateListOf(
                ItineraryEvent("09:00", "Breakfast near Plaça Catalunya", "City Center", "€9", Icons.Default.Restaurant),
                ItineraryEvent("11:00", "Sagrada Família Visit", "Eixample", "€26", Icons.Default.Tour)
            ),
            afternoonPlan = mutableStateListOf(
                ItineraryEvent("14:00", "Lunch in El Born", "El Born", "€19", Icons.Default.Restaurant)
            ),
            eveningPlan = mutableStateListOf(
                ItineraryEvent("18:00", "Walk at Barceloneta", "Barceloneta", "Free", Icons.Default.Map)
            ),
            initialNotes = "Metro ticket useful for all day."
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
            dayDate = "05 Dec 2025",
            morningPlan = mutableStateListOf(
                ItineraryEvent("08:30", "Breakfast near Bryant Park", "Midtown", "€16", Icons.Default.Restaurant),
                ItineraryEvent("10:30", "Top of the Rock", "Rockefeller Center", "€38", Icons.Default.Place)
            ),
            afternoonPlan = mutableStateListOf(
                ItineraryEvent("13:30", "Lunch in Chelsea", "Chelsea Market", "€24", Icons.Default.Restaurant),
                ItineraryEvent("15:30", "High Line Walk", "Manhattan West Side", "Free", Icons.Default.Map)
            ),
            eveningPlan = mutableStateListOf(
                ItineraryEvent("19:30", "Times Square at night", "Times Square", "Free", Icons.Default.Tour)
            ),
            initialNotes = "Cold weather trip. Keep gloves and power bank ready."
        )
    )

    return summary to days
}

@Composable
private fun ItineraryHeroHeader(summary: ItinerarySummary) {
    val sky = MaterialTheme.colorScheme.secondary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        Image(
            painter = painterResource(id = summary.imageRes),
            contentDescription = summary.destination,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.10f),
                            Color.Black.copy(alpha = 0.28f),
                            Color.Black.copy(alpha = 0.72f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = summary.destination,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroChip(Icons.Default.CalendarMonth, summary.dateRange)
                HeroChip(Icons.Default.Schedule, summary.totalDays)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeroChip(Icons.Default.AttachMoney, summary.estimatedBudget)
                HeroChip(Icons.Default.Place, summary.status, highlight = true)
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 4.dp),
                color = sky.copy(alpha = 0.22f)
            )

            Text(
                text = stringResource(R.string.itinerary_hero_subtitle),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun HeroChip(
    icon: ImageVector,
    text: String,
    highlight: Boolean = false
) {
    val background = if (highlight) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
    } else {
        Color.Black.copy(alpha = 0.34f)
    }

    val contentColor = if (highlight) {
        MaterialTheme.colorScheme.secondary
    } else {
        Color.White
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = contentColor,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = contentColor
        )
    }
}

@Composable
private fun DayNavigator(
    currentDayLabel: String,
    currentDate: String,
    canGoPrevious: Boolean,
    canGoNext: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onPrevious, enabled = canGoPrevious) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Previous day",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentDayLabel,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = currentDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }

            IconButton(onClick = onNext, enabled = canGoNext) {
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Next day",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PlannerSection(
    title: String,
    canAddEvent: Boolean,
    onAddClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val stackActions = maxWidth < 560.dp

                if (!stackActions) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (canAddEvent) {
                            OutlinedButton(onClick = onAddClick) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add event"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.itinerary_add_event))
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (canAddEvent) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(onClick = onAddClick) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add event"
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(stringResource(R.string.itinerary_add_event))
                                }
                            }
                        }
                    }
                }
            }

            content()
        }
    }
}

@Composable
private fun PlannerNotesSection(
    notes: String,
    canEditNotes: Boolean,
    onEditClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val stackActions = maxWidth < 560.dp

                if (!stackActions) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.itinerary_day_notes),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        if (canEditNotes) {
                            OutlinedButton(onClick = onEditClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit notes"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.itinerary_btn_edit))
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.itinerary_day_notes),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (canEditNotes) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                OutlinedButton(onClick = onEditClick) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit notes"
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(stringResource(R.string.itinerary_btn_edit))
                                }
                            }
                        }
                    }
                }
            }

            NotesCard(
                icon = Icons.Default.LocationOn,
                title = stringResource(R.string.itinerary_notes_label),
                text = notes
            )

            content()
        }
    }
}

@Composable
private fun AgendaEventCard(
    event: ItineraryEvent,
    showActionButtons: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            val useSecondRowForButtons = maxWidth < 660.dp

            if (!useSecondRowForButtons) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    EventMainInfo(event = event, modifier = Modifier.weight(1f))

                    if (showActionButtons) {
                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(onClick = onEditClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit event"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.itinerary_btn_edit))
                            }

                            TextButton(onClick = onDeleteClick) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete event"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.itinerary_btn_delete))
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EventMainInfo(event = event)

                    if (showActionButtons) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(onClick = onEditClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit event"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.itinerary_btn_edit))
                            }

                            TextButton(onClick = onDeleteClick) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete event"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(stringResource(R.string.itinerary_btn_delete))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventMainInfo(
    event: ItineraryEvent,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = event.icon,
                contentDescription = event.title,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = event.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = event.location,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.68f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                EventMeta(icon = Icons.Default.Schedule, text = event.time)
                EventMeta(icon = Icons.Default.AttachMoney, text = event.cost)
            }
        }
    }
}

@Composable
private fun EventMeta(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )
    }
}

@Composable
private fun EventEditForm(
    title: String,
    iconOptions: List<IconOption>,
    selectedIcon: ImageVector,
    onIconSelected: (ImageVector) -> Unit,
    titleValue: String,
    onTitleChange: (String) -> Unit,
    locationValue: String,
    onLocationChange: (String) -> Unit,
    timeValue: String,
    onTimeChange: (String) -> Unit,
    costValue: String,
    onCostChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    var iconMenuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box {
                OutlinedButton(onClick = { iconMenuExpanded = true }) {
                    Icon(
                        imageVector = selectedIcon,
                        contentDescription = "Selected icon"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.itinerary_btn_select_icon))
                }

                DropdownMenu(
                    expanded = iconMenuExpanded,
                    onDismissRequest = { iconMenuExpanded = false }
                ) {
                    iconOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option.label) },
                            leadingIcon = {
                                Icon(
                                    imageVector = option.icon,
                                    contentDescription = option.label
                                )
                            },
                            onClick = {
                                onIconSelected(option.icon)
                                iconMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = titleValue,
                onValueChange = onTitleChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.itinerary_field_title)) }
            )

            OutlinedTextField(
                value = locationValue,
                onValueChange = onLocationChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.itinerary_field_location)) }
            )

            OutlinedTextField(
                value = timeValue,
                onValueChange = onTimeChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.itinerary_field_time)) }
            )

            OutlinedTextField(
                value = costValue,
                onValueChange = onCostChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.itinerary_field_cost)) }
            )

            ResponsiveActionRow(
                primaryText = stringResource(R.string.itinerary_btn_save),
                secondaryText = stringResource(R.string.itinerary_btn_cancel),
                onPrimaryClick = onSave,
                onSecondaryClick = onCancel
            )
        }
    }
}

@Composable
private fun NotesEditForm(
    value: String,
    onValueChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                label = { Text(stringResource(R.string.itinerary_field_notes)) }
            )

            ResponsiveActionRow(
                primaryText = stringResource(R.string.itinerary_btn_save),
                secondaryText = stringResource(R.string.itinerary_btn_cancel),
                onPrimaryClick = onSave,
                onSecondaryClick = onCancel
            )
        }
    }
}

@Composable
private fun ResponsiveActionRow(
    primaryText: String,
    secondaryText: String,
    onPrimaryClick: () -> Unit,
    onSecondaryClick: () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val useSecondRow = maxWidth < 420.dp

        if (!useSecondRow) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onSecondaryClick) {
                    Text(secondaryText)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = onPrimaryClick) {
                    Text(primaryText)
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onSecondaryClick) {
                    Text(secondaryText)
                }

                Button(onClick = onPrimaryClick) {
                    Text(primaryText)
                }
            }
        }
    }
}

@Composable
private fun NotesCard(
    icon: ImageVector,
    title: String,
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = text,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.76f)
                )
            }
        }
    }
}