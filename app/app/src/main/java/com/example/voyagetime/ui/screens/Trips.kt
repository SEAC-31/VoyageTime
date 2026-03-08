package com.example.voyagetime.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyagetime.R

enum class TripState {
    UPCOMING,
    PLANNED,
    COMPLETED
}

enum class TripDialogType {
    TRIPS,
    DAYS,
    BUDGET
}

data class TripItem(
    val id: String,
    val destination: String,
    val country: String,
    val dateRange: String,
    val duration: String,
    val budget: String,
    val statusLabel: String,
    val state: TripState,
    val image: Int
)

data class HomeStat(
    val value: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun Trips(
    modifier: Modifier = Modifier,
    onTripClick: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    val upcomingTrips = remember {
        listOf(
            TripItem(
                id = "paris",
                destination = "Paris",
                country = "France",
                dateRange = "12 Jun - 18 Jun 2026",
                duration = "6 days",
                budget = "€820",
                statusLabel = "Upcoming",
                state = TripState.UPCOMING,
                image = R.drawable.paris
            ),
            TripItem(
                id = "tokyo",
                destination = "Tokyo",
                country = "Japan",
                dateRange = "02 Aug - 11 Aug 2026",
                duration = "9 days",
                budget = "€2,450",
                statusLabel = "Planned",
                state = TripState.PLANNED,
                image = R.drawable.tokyo
            ),
            TripItem(
                id = "amsterdam",
                destination = "Amsterdam",
                country = "Netherlands",
                dateRange = "21 Sep - 25 Sep 2026",
                duration = "4 days",
                budget = "€680",
                statusLabel = "Upcoming",
                state = TripState.UPCOMING,
                image = R.drawable.newyork
            )
        )
    }

    val pastTrips = remember {
        listOf(
            TripItem(
                id = "barcelona",
                destination = "Barcelona",
                country = "Spain",
                dateRange = "10 Mar - 13 Mar 2026",
                duration = "3 days",
                budget = "€290",
                statusLabel = "Completed",
                state = TripState.COMPLETED,
                image = R.drawable.barcelona
            ),
            TripItem(
                id = "rome",
                destination = "Rome",
                country = "Italy",
                dateRange = "15 Jan - 20 Jan 2026",
                duration = "5 days",
                budget = "€740",
                statusLabel = "Completed",
                state = TripState.COMPLETED,
                image = R.drawable.paris
            )
        )
    }

    val allTrips = upcomingTrips + pastTrips

    val currentBudget = upcomingTrips.sumOf { it.budget.replace("€", "").replace(",", "").toInt() }
    val pastBudget = pastTrips.sumOf { it.budget.replace("€", "").replace(",", "").toInt() }
    val totalBudget = currentBudget + pastBudget

    val stats = remember(totalBudget) {
        listOf(
            HomeStat("5", "Trips", Icons.Default.TravelExplore),
            HomeStat("27", "Days Planned", Icons.Default.CalendarMonth),
            HomeStat("€$totalBudget", "Budget", Icons.Default.AttachMoney)
        )
    }

    var activeDialog by remember { mutableStateOf<TripDialogType?>(null) }

    activeDialog?.let { dialogType ->
        TripOverviewDialog(
            dialogType = dialogType,
            trips = allTrips,
            onDismiss = { activeDialog = null }
        )
    }

    var favoriteRegion by remember { mutableStateOf("Europe") }
    var travelGoal by remember { mutableStateOf("Visit 3 new cities this year") }

    var editingFavoriteRegion by remember { mutableStateOf(false) }
    var editingTravelGoal by remember { mutableStateOf(false) }

    var favoriteRegionDraft by remember { mutableStateOf(favoriteRegion) }
    var travelGoalDraft by remember { mutableStateOf(travelGoal) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Trips",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        TripCategory(title = "Overview") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stats.forEachIndexed { index, stat ->
                    val dialogType = when (index) {
                        0 -> TripDialogType.TRIPS
                        1 -> TripDialogType.DAYS
                        else -> TripDialogType.BUDGET
                    }

                    HomeStatCard(
                        stat = stat,
                        modifier = Modifier.weight(1f),
                        onClick = { activeDialog = dialogType }
                    )
                }
            }
        }

        TripCategory(title = "Upcoming Trips") {
            upcomingTrips.forEachIndexed { index, trip ->
                EditableUpcomingTripCard(
                    trip = trip,
                    onViewClick = { onTripClick(trip.id) }
                )
                if (index != upcomingTrips.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }

        TripCategory(title = "Past Trips") {
            pastTrips.forEachIndexed { index, trip ->
                PastTripCard(
                    trip = trip,
                    onViewClick = { onTripClick(trip.id) }
                )
                if (index != pastTrips.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }

        TripCategory(title = "Travel Insights") {
            EditableInsightRow(
                icon = Icons.Default.Explore,
                title = "Favorite Region",
                value = favoriteRegion,
                isEditing = editingFavoriteRegion,
                draftValue = favoriteRegionDraft,
                onDraftChange = { favoriteRegionDraft = it },
                onEditClick = {
                    favoriteRegionDraft = favoriteRegion
                    editingFavoriteRegion = true
                    editingTravelGoal = false
                },
                onCancel = {
                    editingFavoriteRegion = false
                    favoriteRegionDraft = favoriteRegion
                },
                onSave = {
                    favoriteRegion = favoriteRegionDraft
                    editingFavoriteRegion = false
                }
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            StaticInsightRow(
                icon = Icons.Default.FlightTakeoff,
                title = "Next Departure",
                subtitle = "Paris — 12 Jun 2026"
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))

            EditableInsightRow(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = "Travel Goal",
                value = travelGoal,
                isEditing = editingTravelGoal,
                draftValue = travelGoalDraft,
                onDraftChange = { travelGoalDraft = it },
                onEditClick = {
                    travelGoalDraft = travelGoal
                    editingTravelGoal = true
                    editingFavoriteRegion = false
                },
                onCancel = {
                    editingTravelGoal = false
                    travelGoalDraft = travelGoal
                },
                onSave = {
                    travelGoal = travelGoalDraft
                    editingTravelGoal = false
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TripOverviewDialog(
    dialogType: TripDialogType,
    trips: List<TripItem>,
    onDismiss: () -> Unit
) {
    val title: String
    val text: String

    when (dialogType) {
        TripDialogType.TRIPS -> {
            title = "Trips Overview"
            text = buildString {
                appendLine("Total trips planned: ${trips.size}")
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination} (${trip.country})")
                }
            }
        }

        TripDialogType.DAYS -> {
            title = "Days Planned"
            text = buildString {
                appendLine("Trip dates:")
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination}: ${trip.dateRange}")
                }
            }
        }

        TripDialogType.BUDGET -> {
            val total = trips.sumOf { it.budget.replace("€", "").replace(",", "").toInt() }
            title = "Budget Details"
            text = buildString {
                appendLine("Estimated costs by trip:")
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination}: ${trip.budget}")
                }
                appendLine()
                append("Total budget: €$total")
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        }
    )
}

@Composable
fun TripCategory(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text = title.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
fun EditableUpcomingTripCard(
    trip: TripItem,
    onViewClick: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var draftDestination by remember { mutableStateOf(trip.destination) }
    var draftCountry by remember { mutableStateOf(trip.country) }
    var draftDateRange by remember { mutableStateOf(trip.dateRange) }
    var draftDuration by remember { mutableStateOf(trip.duration) }
    var draftBudget by remember { mutableStateOf(trip.budget) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val useSecondRowForButtons = maxWidth < 760.dp

            if (useSecondRowForButtons) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TripInfoBlock(
                        image = trip.image,
                        destination = draftDestination,
                        country = draftCountry,
                        dateRange = draftDateRange,
                        duration = draftDuration,
                        budget = draftBudget,
                        statusLabel = trip.statusLabel
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { isEditing = !isEditing }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit trip"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(onClick = onViewClick) {
                            Text("View Plan")
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    TripInfoBlock(
                        image = trip.image,
                        destination = draftDestination,
                        country = draftCountry,
                        dateRange = draftDateRange,
                        duration = draftDuration,
                        budget = draftBudget,
                        statusLabel = trip.statusLabel,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.height(110.dp)
                    ) {
                        Button(onClick = onViewClick) {
                            Text("View Plan")
                        }

                        OutlinedButton(
                            onClick = { isEditing = !isEditing }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit trip"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Edit")
                        }
                    }
                }
            }
        }

        if (isEditing) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                        text = "Edit Upcoming Trip",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    OutlinedTextField(
                        value = draftDestination,
                        onValueChange = { draftDestination = it },
                        label = { Text("Destination") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = draftCountry,
                        onValueChange = { draftCountry = it },
                        label = { Text("Country") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = draftDateRange,
                        onValueChange = { draftDateRange = it },
                        label = { Text("Date range") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = draftDuration,
                        onValueChange = { draftDuration = it },
                        label = { Text("Duration") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = draftBudget,
                        onValueChange = { draftBudget = it },
                        label = { Text("Budget") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                draftDestination = trip.destination
                                draftCountry = trip.country
                                draftDateRange = trip.dateRange
                                draftDuration = trip.duration
                                draftBudget = trip.budget
                                isEditing = false
                            }
                        ) {
                            Text("Cancel")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                isEditing = false
                            }
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PastTripCard(
    trip: TripItem,
    onViewClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val useSecondRowForButtons = maxWidth < 760.dp

            if (useSecondRowForButtons) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TripInfoBlock(
                        image = trip.image,
                        destination = trip.destination,
                        country = trip.country,
                        dateRange = trip.dateRange,
                        duration = trip.duration,
                        budget = trip.budget,
                        statusLabel = trip.statusLabel
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = onViewClick) {
                            Text("View Plan")
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    TripInfoBlock(
                        image = trip.image,
                        destination = trip.destination,
                        country = trip.country,
                        dateRange = trip.dateRange,
                        duration = trip.duration,
                        budget = trip.budget,
                        statusLabel = trip.statusLabel,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        OutlinedButton(onClick = onViewClick) {
                            Text("View Plan")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripInfoBlock(
    image: Int,
    destination: String,
    country: String,
    dateRange: String,
    duration: String,
    budget: String,
    statusLabel: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = destination,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(96.dp)
                .height(96.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = destination,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = country,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniInfo(
                    icon = Icons.Default.CalendarMonth,
                    text = dateRange
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniInfo(
                    icon = Icons.Default.Schedule,
                    text = duration
                )
                MiniInfo(
                    icon = Icons.Default.AttachMoney,
                    text = budget
                )
            }

            Text(
                text = statusLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (statusLabel == "Completed") {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                } else {
                    MaterialTheme.colorScheme.primary
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EditableInsightRow(
    icon: ImageVector,
    title: String,
    value: String,
    isEditing: Boolean,
    draftValue: String,
    onDraftChange: (String) -> Unit,
    onEditClick: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            OutlinedButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit insight"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit")
            }
        }

        if (isEditing) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                        value = draftValue,
                        onValueChange = onDraftChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text(title) }
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
    }
}

@Composable
fun StaticInsightRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }
        }
    }
}

@Composable
fun MiniInfo(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}