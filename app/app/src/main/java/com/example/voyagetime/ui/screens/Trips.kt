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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.LaunchedEffect
import com.example.voyagetime.R
import com.example.voyagetime.ui.viewmodels.TripsViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

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
    onTripClick: (String) -> Unit,
    viewModel: TripsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.reloadTrips()
    }

    val scrollState = rememberScrollState()
    var activeDialog by remember { mutableStateOf<TripDialogType?>(null) }

    activeDialog?.let { dialogType ->
        TripOverviewDialog(
            dialogType = dialogType,
            trips = uiState.allTrips,
            onDismiss = { activeDialog = null }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        TripsHeader(totalTrips = uiState.allTrips.size)

        TripCategory(title = "Overview") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.stats.forEachIndexed { index, stat ->
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
            uiState.upcomingTrips.forEachIndexed { index, trip ->
                EditableUpcomingTripCard(
                    trip = trip,
                    onViewClick = { onTripClick(trip.id) },
                    onSave = { updatedTrip -> viewModel.updateTrip(updatedTrip) }
                )

                if (index != uiState.upcomingTrips.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
                    )
                }
            }
        }

        TripCategory(title = "Past Trips") {
            uiState.pastTrips.forEachIndexed { index, trip ->
                PastTripCard(
                    trip = trip,
                    onViewClick = { onTripClick(trip.id) }
                )

                if (index != uiState.pastTrips.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
                    )
                }
            }
        }

        TripCategory(title = "Travel Insights") {
            EditableInsightRow(
                icon = Icons.Default.Explore,
                title = "Favorite Region",
                value = uiState.favoriteRegion,
                onSave = { viewModel.updateFavoriteRegion(it) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
            )

            StaticInsightRow(
                icon = Icons.Default.FlightTakeoff,
                title = "Next Departure",
                subtitle = uiState.nextDeparture
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
            )

            EditableInsightRow(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = "Travel Goal",
                value = uiState.travelGoal,
                onSave = { viewModel.updateTravelGoal(it) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TripsHeader(totalTrips: Int) {
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
                contentDescription = "VoyageTime logo",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Trips",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Review your planned and completed trips in one place.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
            )

            Text(
                text = "Total trips: $totalTrips",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
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
            val total = trips.sumOf { it.budgetValue() }
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
    onViewClick: () -> Unit,
    onSave: (TripItem) -> Unit
) {
    var isEditing by remember(trip.id) { mutableStateOf(false) }
    var draftDestination by remember(trip.id, trip.destination) { mutableStateOf(trip.destination) }
    var draftCountry by remember(trip.id, trip.country) { mutableStateOf(trip.country) }
    var draftDateRange by remember(trip.id, trip.dateRange) { mutableStateOf(normalizeDateRangeForEditing(trip.dateRange)) }
    var draftDuration by remember(trip.id, trip.duration) { mutableStateOf(trip.duration) }
    var draftBudget by remember(trip.id, trip.budget) { mutableStateOf(extractBudgetDigits(trip.budget)) }

    val destinationError = if (draftDestination.trim().isBlank()) "Destination is required" else null
    val countryError = if (draftCountry.trim().isBlank()) "Country is required" else null
    val dateRangeError = validateDateRangeMessage(draftDateRange)
    val durationError = validateDurationMessage(draftDuration)
    val budgetError = validateBudgetMessage(draftBudget)

    val canSave = destinationError == null &&
            countryError == null &&
            dateRangeError == null &&
            durationError == null &&
            budgetError == null

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
                        TripMainInfo(
                            trip = trip,
                            modifier = Modifier.weight(1f)
                        )

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
                                Text("Edit")
                            }

                            Button(onClick = onViewClick) {
                                Text("View Trip")
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TripMainInfo(trip = trip)

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
                                Text("Edit")
                            }

                            Button(onClick = onViewClick) {
                                Text("View Trip")
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
                        label = { Text("Destination") },
                        isError = destinationError != null,
                        supportingText = {
                            if (destinationError != null) {
                                Text(destinationError)
                            }
                        }
                    )

                    OutlinedTextField(
                        value = draftCountry,
                        onValueChange = { draftCountry = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Country") },
                        isError = countryError != null,
                        supportingText = {
                            if (countryError != null) {
                                Text(countryError)
                            }
                        }
                    )

                    OutlinedTextField(
                        value = draftDateRange,
                        onValueChange = { newValue ->
                            draftDateRange = newValue.filter { char ->
                                char.isDigit() || char.isLetter() || char == ' ' || char == '-'
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Date Range") },
                        placeholder = { Text("12 Jun 2026 - 18 Jun 2026") },
                        isError = dateRangeError != null,
                        supportingText = {
                            if (dateRangeError != null) {
                                Text(dateRangeError)
                            }
                        }
                    )

                    OutlinedTextField(
                        value = draftDuration,
                        onValueChange = { newValue ->
                            draftDuration = sanitizeDurationInput(newValue)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Duration") },
                        placeholder = { Text("6 days") },
                        isError = durationError != null,
                        supportingText = {
                            if (durationError != null) {
                                Text(durationError)
                            }
                        }
                    )

                    OutlinedTextField(
                        value = draftBudget,
                        onValueChange = { newValue ->
                            draftBudget = newValue.filter { it.isDigit() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Budget") },
                        placeholder = { Text("820") },
                        isError = budgetError != null,
                        supportingText = {
                            if (budgetError != null) {
                                Text(budgetError)
                            }
                        }
                    )

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                        val useSecondRowForButtons = maxWidth < 420.dp

                        val cancelAction = {
                            draftDestination = trip.destination
                            draftCountry = trip.country
                            draftDateRange = normalizeDateRangeForEditing(trip.dateRange)
                            draftDuration = trip.duration
                            draftBudget = extractBudgetDigits(trip.budget)
                            isEditing = false
                        }

                        val saveAction = {
                            if (canSave) {
                                onSave(
                                    trip.copy(
                                        destination = draftDestination.trim(),
                                        country = draftCountry.trim(),
                                        dateRange = normalizeDateRangeForStorage(draftDateRange),
                                        duration = normalizeDuration(draftDuration),
                                        budget = "€${draftBudget.trim()}"
                                    )
                                )
                                isEditing = false
                            }
                        }

                        if (!useSecondRowForButtons) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = cancelAction) {
                                    Text("Cancel")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = saveAction,
                                    enabled = canSave
                                ) {
                                    Text("Save")
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = cancelAction) {
                                    Text("Cancel")
                                }

                                Button(
                                    onClick = saveAction,
                                    enabled = canSave
                                ) {
                                    Text("Save")
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
private fun TripMainInfo(
    trip: TripItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
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
    onSave: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var draftValue by remember(value) { mutableStateOf(value) }

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

                    OutlinedButton(onClick = {
                        draftValue = value
                        isEditing = true
                    }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit insight"
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Edit")
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
                        OutlinedButton(onClick = {
                            draftValue = value
                            isEditing = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit insight"
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
                    OutlinedTextField(
                        value = draftValue,
                        onValueChange = { draftValue = it },
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
                                TextButton(
                                    onClick = {
                                        draftValue = value
                                        isEditing = false
                                    }
                                ) {
                                    Text("Cancel")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        onSave(draftValue.trim())
                                        isEditing = false
                                    }
                                ) {
                                    Text("Save")
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(
                                    onClick = {
                                        draftValue = value
                                        isEditing = false
                                    }
                                ) {
                                    Text("Cancel")
                                }

                                Button(
                                    onClick = {
                                        onSave(draftValue.trim())
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
    Row(verticalAlignment = Alignment.CenterVertically) {
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

@Composable
fun HomeStatCard(
    stat: HomeStat,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = stat.label,
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stat.value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stat.label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
    }
}

private fun TripItem.budgetValue(): Int {
    return budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0
}

private fun extractBudgetDigits(budget: String): String {
    return budget.filter { it.isDigit() }
}

private fun validateBudgetMessage(value: String): String? {
    if (value.isBlank()) return "Budget is required"
    if (!value.all { it.isDigit() }) return "Budget must contain numbers only"
    return null
}

private fun sanitizeDurationInput(value: String): String {
    return value.filter { char ->
        char.isDigit() || char.isLetter() || char == ' '
    }
}

private fun validateDurationMessage(value: String): String? {
    val trimmed = value.trim()

    if (trimmed.isBlank()) {
        return "Duration is required"
    }

    val regex = Regex("""^\d+\s+(day|days|month|months|year|years)$""", RegexOption.IGNORE_CASE)

    if (!regex.matches(trimmed)) {
        return "Use format like: 6 days, 2 months, 1 year"
    }

    return null
}

private fun normalizeDuration(value: String): String {
    val trimmed = value.trim().lowercase()
    val parts = trimmed.split(Regex("""\s+"""))

    if (parts.size != 2) return value.trim()

    val amount = parts[0]
    val unit = when (parts[1]) {
        "day", "days" -> if (amount == "1") "day" else "days"
        "month", "months" -> if (amount == "1") "month" else "months"
        "year", "years" -> if (amount == "1") "year" else "years"
        else -> parts[1]
    }

    return "$amount $unit"
}

private fun validateDateRangeMessage(value: String): String? {
    val trimmed = value.trim()

    if (trimmed.isBlank()) {
        return "Date range is required"
    }

    val parseResult = parseDateRange(trimmed) ?: return "Use real dates like: 12 Jun 2026 - 18 Jun 2026"

    val startDate = parseResult.first
    val endDate = parseResult.second

    if (endDate.isBefore(startDate)) {
        return "End date cannot be before start date"
    }

    return null
}

private fun parseDateRange(value: String): Pair<LocalDate, LocalDate>? {
    val input = value.trim()

    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.ENGLISH)

    val fullYearPattern = Regex(
        """^(\d{1,2}\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\s+\d{4})\s*-\s*(\d{1,2}\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\s+\d{4})$""",
        RegexOption.IGNORE_CASE
    )

    val shortStartPattern = Regex(
        """^(\d{1,2}\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))\s*-\s*(\d{1,2}\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\s+\d{4})$""",
        RegexOption.IGNORE_CASE
    )

    return try {
        val fullMatch = fullYearPattern.matchEntire(input)
        if (fullMatch != null) {
            val startText = fullMatch.groupValues[1]
            val endText = fullMatch.groupValues[3]

            val startDate = LocalDate.parse(startText, formatter)
            val endDate = LocalDate.parse(endText, formatter)

            return startDate to endDate
        }

        val shortMatch = shortStartPattern.matchEntire(input)
        if (shortMatch != null) {
            val startWithoutYear = shortMatch.groupValues[1]
            val endText = shortMatch.groupValues[3]

            val endDate = LocalDate.parse(endText, formatter)
            val startTextSameYear = "$startWithoutYear ${endDate.year}"
            val tentativeStartDate = LocalDate.parse(startTextSameYear, formatter)

            val startDate = if (tentativeStartDate.isAfter(endDate)) {
                LocalDate.parse("$startWithoutYear ${endDate.year - 1}", formatter)
            } else {
                tentativeStartDate
            }

            return startDate to endDate
        }

        null
    } catch (_: DateTimeParseException) {
        null
    }
}

private fun normalizeDateRangeForStorage(value: String): String {
    val parsed = parseDateRange(value) ?: return value.trim()

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

    return "${parsed.first.format(formatter)} - ${parsed.second.format(formatter)}"
}

private fun normalizeDateRangeForEditing(value: String): String {
    val parsed = parseDateRange(value) ?: return value.trim()

    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH)

    return "${parsed.first.format(formatter)} - ${parsed.second.format(formatter)}"
}