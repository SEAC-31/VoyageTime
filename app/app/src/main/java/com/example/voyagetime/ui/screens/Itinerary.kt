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
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voyagetime.R
import com.example.voyagetime.ui.viewmodel.EditSection
import com.example.voyagetime.ui.viewmodel.FormMode
import com.example.voyagetime.ui.viewmodel.IconOption
import com.example.voyagetime.ui.viewmodel.ItineraryEvent
import com.example.voyagetime.ui.viewmodel.ItinerarySummary
import com.example.voyagetime.ui.viewmodel.ItineraryViewModel
import com.example.voyagetime.ui.viewmodel.ItineraryViewModelFactory

@Composable
fun Itinerary(
    tripId: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val viewModel: ItineraryViewModel = viewModel(
        factory = ItineraryViewModelFactory(tripId)
    )

    val summary = viewModel.summary
    val days by viewModel.days.collectAsState()
    val isCompletedTrip = viewModel.isCompletedTrip

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
                title = "Morning Plan",
                canAddEvent = !isCompletedTrip,
                onAddClick = { openAdd(EditSection.MORNING) }
            ) {
                currentDay.morningPlan.forEachIndexed { index, event ->
                    AgendaEventCard(
                        event = event,
                        showActionButtons = !isCompletedTrip,
                        onEditClick = { openEdit(EditSection.MORNING, event, index) },
                        onDeleteClick = {
                            viewModel.deleteEvent(currentDayIndex, EditSection.MORNING, index)
                            if (editingSection == EditSection.MORNING && editingEventIndex == index) {
                                closeForm()
                            }
                        }
                    )
                }

                if (editingSection == EditSection.MORNING && !isCompletedTrip) {
                    EventEditForm(
                        title = if (formMode == FormMode.ADD) "Add Morning Event" else "Edit Morning Event",
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
                                viewModel.addEvent(currentDayIndex, EditSection.MORNING, newEvent)
                            } else if (editingEventIndex in currentDay.morningPlan.indices) {
                                viewModel.updateEvent(currentDayIndex, EditSection.MORNING, editingEventIndex, newEvent)
                            }

                            closeForm()
                        }
                    )
                }
            }

            PlannerSection(
                title = "Afternoon Plan",
                canAddEvent = !isCompletedTrip,
                onAddClick = { openAdd(EditSection.AFTERNOON) }
            ) {
                currentDay.afternoonPlan.forEachIndexed { index, event ->
                    AgendaEventCard(
                        event = event,
                        showActionButtons = !isCompletedTrip,
                        onEditClick = { openEdit(EditSection.AFTERNOON, event, index) },
                        onDeleteClick = {
                            viewModel.deleteEvent(currentDayIndex, EditSection.AFTERNOON, index)
                            if (editingSection == EditSection.AFTERNOON && editingEventIndex == index) {
                                closeForm()
                            }
                        }
                    )
                }

                if (editingSection == EditSection.AFTERNOON && !isCompletedTrip) {
                    EventEditForm(
                        title = if (formMode == FormMode.ADD) "Add Afternoon Event" else "Edit Afternoon Event",
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
                                viewModel.addEvent(currentDayIndex, EditSection.AFTERNOON, newEvent)
                            } else if (editingEventIndex in currentDay.afternoonPlan.indices) {
                                viewModel.updateEvent(currentDayIndex, EditSection.AFTERNOON, editingEventIndex, newEvent)
                            }

                            closeForm()
                        }
                    )
                }
            }

            PlannerSection(
                title = "Evening Plan",
                canAddEvent = !isCompletedTrip,
                onAddClick = { openAdd(EditSection.EVENING) }
            ) {
                currentDay.eveningPlan.forEachIndexed { index, event ->
                    AgendaEventCard(
                        event = event,
                        showActionButtons = !isCompletedTrip,
                        onEditClick = { openEdit(EditSection.EVENING, event, index) },
                        onDeleteClick = {
                            viewModel.deleteEvent(currentDayIndex, EditSection.EVENING, index)
                            if (editingSection == EditSection.EVENING && editingEventIndex == index) {
                                closeForm()
                            }
                        }
                    )
                }

                if (editingSection == EditSection.EVENING && !isCompletedTrip) {
                    EventEditForm(
                        title = if (formMode == FormMode.ADD) "Add Evening Event" else "Edit Evening Event",
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
                                viewModel.addEvent(currentDayIndex, EditSection.EVENING, newEvent)
                            } else if (editingEventIndex in currentDay.eveningPlan.indices) {
                                viewModel.updateEvent(currentDayIndex, EditSection.EVENING, editingEventIndex, newEvent)
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
                                viewModel.updateNotes(currentDayIndex, draftNotes)
                                closeForm()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ItineraryHeroHeader(summary: ItinerarySummary) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
    ) {
        Image(
            painter = painterResource(id = summary.imageRes),
            contentDescription = summary.destination,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.08f),
                            Color.Black.copy(alpha = 0.52f),
                            Color.Black.copy(alpha = 0.78f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = Color.White.copy(alpha = 0.18f)
            ) {
                Text(
                    text = summary.status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = summary.destination,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                ItineraryMiniInfo(
                    icon = Icons.Default.CalendarMonth,
                    text = summary.dateRange,
                    contentColor = Color.White
                )
                Spacer(modifier = Modifier.width(14.dp))
                ItineraryMiniInfo(
                    icon = Icons.Default.LocationOn,
                    text = summary.totalDays,
                    contentColor = Color.White
                )
                Spacer(modifier = Modifier.width(14.dp))
                ItineraryMiniInfo(
                    icon = Icons.Default.AttachMoney,
                    text = summary.estimatedBudget,
                    contentColor = Color.White
                )
            }
        }
    }
}

@Composable
private fun ItineraryMiniInfo(
    icon: ImageVector,
    text: String,
    contentColor: Color
) {
    Row(
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
                                Text("Add")
                            }
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (canAddEvent) {
                            OutlinedButton(onClick = onAddClick) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add event"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Add")
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
                            text = "Notes",
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
                                Text("Edit")
                            }
                        }
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Notes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (canEditNotes) {
                            OutlinedButton(onClick = onEditClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit notes"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit")
                            }
                        }
                    }
                }
            }

            NotesCard(notes = notes)
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
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            EventMainInfo(event = event)

            if (showActionButtons) {
                HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))

                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val stackActions = maxWidth < 520.dp

                    if (!stackActions) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = onEditClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit event"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit")
                            }

                            TextButton(onClick = onDeleteClick) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete event"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete")
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.End
                        ) {
                            TextButton(onClick = onEditClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit event"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Edit")
                            }

                            TextButton(onClick = onDeleteClick) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete event"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete")
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
                    Text("Choose icon")
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
                label = { Text("Title") }
            )

            OutlinedTextField(
                value = locationValue,
                onValueChange = onLocationChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Location") }
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = timeValue,
                    onValueChange = onTimeChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Time") }
                )

                OutlinedTextField(
                    value = costValue,
                    onValueChange = onCostChange,
                    modifier = Modifier.weight(1f),
                    label = { Text("Cost") }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSave) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
private fun NotesCard(notes: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            text = notes,
            modifier = Modifier.padding(14.dp),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.80f)
        )
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                label = { Text("Notes") }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onCancel) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onSave) {
                    Text("Save")
                }
            }
        }
    }
}