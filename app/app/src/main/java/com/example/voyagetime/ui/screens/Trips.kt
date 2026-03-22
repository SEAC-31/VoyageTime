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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
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
                id = "newyork",
                destination = "New York",
                country = "United States",
                dateRange = "04 Dec - 10 Dec 2025",
                duration = "6 days",
                budget = "€1,680",
                statusLabel = "Completed",
                state = TripState.COMPLETED,
                image = R.drawable.newyork
            )
        )
    }

    val allTrips = upcomingTrips + pastTrips

    val currentBudget = upcomingTrips.sumOf { it.budget.replace("€", "").replace(",", "").toInt() }
    val pastBudget = pastTrips.sumOf { it.budget.replace("€", "").replace(",", "").toInt() }
    val totalBudget = currentBudget + pastBudget
    val totalDays = allTrips.sumOf { it.duration.substringBefore(" ").toIntOrNull() ?: 0 }

    val labelTrips = stringResource(R.string.stat_trips)
    val labelDays = stringResource(R.string.stat_days_planned)
    val labelBudget = stringResource(R.string.stat_budget)

    val stats = remember(totalBudget, totalDays, labelTrips, labelDays, labelBudget) {
        listOf(
            HomeStat("4", labelTrips, Icons.Default.TravelExplore),
            HomeStat(totalDays.toString(), labelDays, Icons.Default.CalendarMonth),
            HomeStat("€$totalBudget", labelBudget, Icons.Default.AttachMoney)
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

    var favoriteRegion by remember { mutableStateOf("Europe & North America") }
    var travelGoal by remember { mutableStateOf("Complete 4 memorable trips with clear itineraries") }

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
        TripsHeader(totalTrips = allTrips.size)

        TripCategory(title = stringResource(R.string.trips_section_overview)) {
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

        TripCategory(title = stringResource(R.string.trips_section_upcoming)) {
            upcomingTrips.forEachIndexed { index, trip ->
                EditableUpcomingTripCard(
                    trip = trip,
                    onViewClick = { onTripClick(trip.id) }
                )
                if (index != upcomingTrips.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
                    )
                }
            }
        }

        TripCategory(title = stringResource(R.string.trips_section_past)) {
            pastTrips.forEachIndexed { index, trip ->
                PastTripCard(
                    trip = trip,
                    onViewClick = { onTripClick(trip.id) }
                )
                if (index != pastTrips.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
                    )
                }
            }
        }

        TripCategory(title = stringResource(R.string.trips_section_insights)) {
            EditableInsightRow(
                icon = Icons.Default.Explore,
                title = stringResource(R.string.trips_insight_region),
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

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
            )

            StaticInsightRow(
                icon = Icons.Default.FlightTakeoff,
                title = stringResource(R.string.trips_insight_departure),
                subtitle = "Paris — 12 Jun 2026"
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
            )

            EditableInsightRow(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = stringResource(R.string.trips_insight_goal),
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
fun TripsHeader(totalTrips: Int) {
    val orange = MaterialTheme.colorScheme.primary
    val sky = MaterialTheme.colorScheme.secondary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.90f))
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_no_background),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.trips_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.trips_header_subtitle),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
            )
        }
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
            title = "Trips Overview" // translated below
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
                Text(stringResource(R.string.dialog_close))
            }
        },
        title = { Text(text = title) },
        text = { Text(text = text) }
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
        if (!isEditing) {
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val useSecondRowForButtons = maxWidth < 700.dp

                if (!useSecondRowForButtons) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = trip.image),
                                contentDescription = trip.destination,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(18.dp))
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = trip.destination,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = trip.country,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    MiniInfo(
                                        icon = Icons.Default.CalendarMonth,
                                        text = trip.dateRange
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    MiniInfo(
                                        icon = Icons.Default.Schedule,
                                        text = trip.duration
                                    )
                                    MiniInfo(
                                        icon = Icons.Default.AttachMoney,
                                        text = trip.budget
                                    )
                                }

                                Text(
                                    text = trip.statusLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(onClick = { isEditing = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit trip"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.trips_btn_edit))
                            }

                            Button(onClick = onViewClick) {
                                Text(stringResource(R.string.trips_btn_view))
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = trip.image),
                                contentDescription = trip.destination,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(100.dp)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(18.dp))
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = trip.destination,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = trip.country,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    MiniInfo(
                                        icon = Icons.Default.CalendarMonth,
                                        text = trip.dateRange
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    MiniInfo(
                                        icon = Icons.Default.Schedule,
                                        text = trip.duration
                                    )
                                    MiniInfo(
                                        icon = Icons.Default.AttachMoney,
                                        text = trip.budget
                                    )
                                }

                                Text(
                                    text = trip.statusLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(onClick = { isEditing = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit trip"
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(stringResource(R.string.trips_btn_edit))
                            }

                            Button(onClick = onViewClick) {
                                Text(stringResource(R.string.trips_btn_view))
                            }
                        }
                    }
                }
            }
        } else {
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
                        value = draftDestination,
                        onValueChange = { draftDestination = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.trips_field_destination)) }
                    )

                    OutlinedTextField(
                        value = draftCountry,
                        onValueChange = { draftCountry = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.trips_field_country)) }
                    )

                    OutlinedTextField(
                        value = draftDateRange,
                        onValueChange = { draftDateRange = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.trips_field_date_range)) }
                    )

                    OutlinedTextField(
                        value = draftDuration,
                        onValueChange = { draftDuration = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.trips_field_duration)) }
                    )

                    OutlinedTextField(
                        value = draftBudget,
                        onValueChange = { draftBudget = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(R.string.trips_field_budget)) }
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val useSecondRowForButtons = maxWidth < 420.dp

                        if (!useSecondRowForButtons) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
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
                                    Text(stringResource(R.string.trips_btn_cancel))
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(onClick = { isEditing = false }) {
                                    Text(stringResource(R.string.trips_btn_save))
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
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
                                        Text(stringResource(R.string.trips_btn_cancel))
                                    }

                                    Button(onClick = { isEditing = false }) {
                                        Text(stringResource(R.string.trips_btn_save))
                                    }
                                }
                            }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onViewClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = trip.image),
            contentDescription = trip.destination,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(18.dp))
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = trip.destination,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = trip.country,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniInfo(
                    icon = Icons.Default.CalendarMonth,
                    text = trip.dateRange
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MiniInfo(
                    icon = Icons.Default.Schedule,
                    text = trip.duration
                )
                MiniInfo(
                    icon = Icons.Default.AttachMoney,
                    text = trip.budget
                )
            }

            Text(
                text = trip.statusLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
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
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val useSecondRowForButtons = maxWidth < 620.dp

            if (!useSecondRowForButtons) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit insight"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(stringResource(R.string.trips_btn_edit))
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
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
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = onEditClick) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit insight"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(stringResource(R.string.trips_btn_edit))
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
                    OutlinedTextField(
                        value = draftValue,
                        onValueChange = onDraftChange,
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        label = { Text(title) }
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val useSecondRowForButtons = maxWidth < 420.dp

                        if (!useSecondRowForButtons) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = onCancel) {
                                    Text(stringResource(R.string.trips_btn_cancel))
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(onClick = onSave) {
                                    Text(stringResource(R.string.trips_btn_save))
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = onCancel) {
                                        Text(stringResource(R.string.trips_btn_cancel))
                                    }

                                    Button(onClick = onSave) {
                                        Text(stringResource(R.string.trips_btn_save))
                                    }
                                }
                            }
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