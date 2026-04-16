package com.example.voyagetime.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voyagetime.R
import com.example.voyagetime.ui.viewmodels.TripsViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
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
    @StringRes val labelRes: Int,
    val icon: ImageVector
)

@Composable
private fun localizedTripDuration(rawDuration: String): String {
    val days = rawDuration.substringBefore(" ").trim().toIntOrNull() ?: return rawDuration
    return if (days == 1) {
        stringResource(R.string.duration_single_day)
    } else {
        stringResource(R.string.duration_multiple_days, days)
    }
}

@Composable
private fun localizedTripStateLabel(state: TripState): String = when (state) {
    TripState.UPCOMING -> stringResource(R.string.status_upcoming)
    TripState.PLANNED -> stringResource(R.string.status_planned)
    TripState.COMPLETED -> stringResource(R.string.status_completed)
}

@Composable
private fun localizedInsightValue(value: String, @StringRes defaultRes: Int, englishDefault: String): String {
    return if (value == englishDefault) stringResource(defaultRes) else value
}

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

        TripCategory(title = stringResource(R.string.trips_section_overview)) {
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

        TripCategory(title = stringResource(R.string.trips_section_upcoming)) {
            uiState.upcomingTrips.forEachIndexed { index, trip ->
                EditableUpcomingTripCard(
                    trip = trip,
                    onViewClick = { onTripClick(trip.id) },
                    onDeleteClick = { viewModel.deleteTrip(trip.id) },
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

        TripCategory(title = stringResource(R.string.trips_section_past)) {
            uiState.pastTrips.forEachIndexed { index, trip ->
                PastTripCard(
                    trip = trip,
                    onViewClick = { onTripClick(trip.id) },
                    onDeleteClick = { viewModel.deleteTrip(trip.id) }
                )

                if (index != uiState.pastTrips.lastIndex) {
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
                value = localizedInsightValue(uiState.favoriteRegion, R.string.trips_insight_region_default, "Europe & North America"),
                onSave = { viewModel.updateFavoriteRegion(it) }
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
            )

            StaticInsightRow(
                icon = Icons.Default.FlightTakeoff,
                title = stringResource(R.string.trips_insight_departure),
                subtitle = uiState.nextDeparture
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
            )

            EditableInsightRow(
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                title = stringResource(R.string.trips_insight_goal),
                value = localizedInsightValue(uiState.travelGoal, R.string.trips_insight_goal_default, "Complete 4 memorable trips with clear itineraries"),
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
                contentDescription = stringResource(R.string.app_logo_content_description),
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

            Text(
                text = stringResource(R.string.trips_total_count, totalTrips),
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
            title = stringResource(R.string.dialog_trips_title)
            text = buildString {
                appendLine(stringResource(R.string.trips_total_planned, trips.size))
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination} (${trip.country})")
                }
            }
        }

        TripDialogType.DAYS -> {
            title = stringResource(R.string.dialog_days_title)
            text = buildString {
                appendLine(stringResource(R.string.trips_trip_dates))
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination}: ${trip.dateRange}")
                }
            }
        }

        TripDialogType.BUDGET -> {
            val total = trips.sumOf { it.budgetValue() }
            title = stringResource(R.string.dialog_budget_title)
            text = buildString {
                appendLine(stringResource(R.string.dialog_budget_header))
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination}: ${trip.budget}")
                }
                appendLine()
                append(stringResource(R.string.dialog_budget_total, total))
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
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onSave: (TripItem) -> Unit
) {
    var isEditing by rememberSaveable(trip.id) { mutableStateOf(false) }
    var draftDestination by rememberSaveable(trip.id, trip.destination) { mutableStateOf(trip.destination) }
    var draftCountry by rememberSaveable(trip.id, trip.country) { mutableStateOf(trip.country) }
    var draftDateRange by rememberSaveable(trip.id, trip.dateRange) {
        mutableStateOf(normalizeDateRangeForEditing(trip.dateRange))
    }
    var draftBudget by rememberSaveable(trip.id, trip.budget) {
        mutableStateOf(extractBudgetDigits(trip.budget))
    }

    val destinationError = if (draftDestination.trim().isBlank()) stringResource(R.string.validation_destination_required) else null
    val countryError = if (draftCountry.trim().isBlank()) stringResource(R.string.validation_country_required) else null
    val dateRangeError = validateDateRangeMessage(
        draftDateRange,
        requiredMessage = stringResource(R.string.validation_date_range_required),
        exampleMessage = stringResource(R.string.validation_date_range_example),
        pastMessage = stringResource(R.string.validation_date_past),
        endBeforeStartMessage = stringResource(R.string.validation_end_before_start)
    )
    val budgetError = validateBudgetMessage(
        draftBudget,
        requiredMessage = stringResource(R.string.validation_budget_required),
        digitsOnlyMessage = stringResource(R.string.validation_budget_digits)
    )
    val computedDuration = calculateDurationFromDateRange(draftDateRange)

    val canSave = destinationError == null &&
            countryError == null &&
            dateRangeError == null &&
            budgetError == null &&
            computedDuration != null

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                                        contentDescription = stringResource(R.string.trips_btn_edit)
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
                            TripMainInfo(trip = trip)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(onClick = { isEditing = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = stringResource(R.string.trips_btn_edit)
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
                            label = { Text(stringResource(R.string.trips_field_destination)) },
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
                            label = { Text(stringResource(R.string.trips_field_country)) },
                            isError = countryError != null,
                            supportingText = {
                                if (countryError != null) {
                                    Text(countryError)
                                }
                            }
                        )

                        DateRangePickerField(
                            value = draftDateRange,
                            errorMessage = dateRangeError,
                            onValueChange = { draftDateRange = it }
                        )

                        AutoDurationField(
                            value = computedDuration ?: "",
                            errorMessage = if (dateRangeError == null && computedDuration == null) {
                                stringResource(R.string.field_select_valid_date_range)
                            } else {
                                null
                            }
                        )

                        OutlinedTextField(
                            value = draftBudget,
                            onValueChange = { newValue ->
                                draftBudget = newValue.filter { it.isDigit() }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(stringResource(R.string.trips_field_budget)) },
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

                            val cancelAction: () -> Unit = {
                                draftDestination = trip.destination
                                draftCountry = trip.country
                                draftDateRange = normalizeDateRangeForEditing(trip.dateRange)
                                draftBudget = extractBudgetDigits(trip.budget)
                                isEditing = false
                            }

                            val saveAction: () -> Unit = {
                                computedDuration?.let { safeDuration ->
                                    onSave(
                                        trip.copy(
                                            destination = draftDestination.trim(),
                                            country = draftCountry.trim(),
                                            dateRange = normalizeDateRangeForStorage(draftDateRange),
                                            duration = safeDuration,
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
                                        Text(stringResource(R.string.trips_btn_cancel))
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = saveAction,
                                        enabled = canSave
                                    ) {
                                        Text(stringResource(R.string.trips_btn_save))
                                    }
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(onClick = cancelAction) {
                                        Text(stringResource(R.string.trips_btn_cancel))
                                    }

                                    Button(
                                        onClick = saveAction,
                                        enabled = canSave
                                    ) {
                                        Text(stringResource(R.string.trips_btn_save))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (!isEditing) {
            TripDeleteButton(
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = onDeleteClick
            )
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
                text = "${trip.destination}, ${trip.country}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
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
                    text = localizedTripDuration(trip.duration)
                )
                MiniInfo(
                    icon = Icons.Default.AttachMoney,
                    text = trip.budget
                )
            }

            Text(
                text = localizedTripStateLabel(trip.state),
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
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onViewClick() },
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
                    text = "${trip.destination}, ${trip.country}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
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
                        text = localizedTripDuration(trip.duration)
                    )
                    MiniInfo(
                        icon = Icons.Default.AttachMoney,
                        text = trip.budget
                    )
                }

                Text(
                    text = localizedTripStateLabel(trip.state),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        TripDeleteButton(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = onDeleteClick
        )
    }
}

@Composable
private fun TripDeleteButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(width = 32.dp, height = 32.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.trips_delete_cd),
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun EditableInsightRow(
    icon: ImageVector,
    title: String,
    value: String,
    onSave: (String) -> Unit
) {
    var isEditing by rememberSaveable { mutableStateOf(false) }
    var draftValue by rememberSaveable(value) { mutableStateOf(value) }

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
                            contentDescription = stringResource(R.string.trips_btn_edit)
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
                        OutlinedButton(onClick = {
                            draftValue = value
                            isEditing = true
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = stringResource(R.string.trips_btn_edit)
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
                                    Text(stringResource(R.string.trips_btn_cancel))
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        onSave(draftValue.trim())
                                        isEditing = false
                                    }
                                ) {
                                    Text(stringResource(R.string.trips_btn_save))
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
                                    Text(stringResource(R.string.trips_btn_cancel))
                                }

                                Button(
                                    onClick = {
                                        onSave(draftValue.trim())
                                        isEditing = false
                                    }
                                ) {
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
                contentDescription = stringResource(stat.labelRes),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = stat.value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(stat.labelRes),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )
        }
    }
}

private val EUROPEAN_DATE_FORMATTER: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun TripItem.budgetValue(): Int {
    return budget.replace("€", "").replace(",", "").trim().toIntOrNull() ?: 0
}

private fun extractBudgetDigits(budget: String): String {
    return budget.filter { it.isDigit() }
}

private fun validateBudgetMessage(
    value: String,
    requiredMessage: String,
    digitsOnlyMessage: String
): String? {
    if (value.isBlank()) return requiredMessage
    if (!value.all { it.isDigit() }) return digitsOnlyMessage
    return null
}

private fun calculateDurationFromDateRange(value: String): String? {
    val parsed = parseDateRange(value) ?: return null
    val startDate = parsed.first
    val endDate = parsed.second

    if (endDate.isBefore(startDate)) return null

    val totalDays = ChronoUnit.DAYS.between(startDate, endDate).toInt() + 1
    return if (totalDays == 1) "1 day" else "$totalDays days"
}

private fun parseDateRange(value: String): Pair<LocalDate, LocalDate>? {
    val input = value.trim()
    val fullPattern = Regex(
        """^(\d{2}/\d{2}/\d{4}|\d{4}-\d{2}-\d{2})\s+-\s+(\d{2}/\d{2}/\d{4}|\d{4}-\d{2}-\d{2})$"""
    )

    val match = fullPattern.matchEntire(input) ?: return null
    val startDate = parseFlexibleDate(match.groupValues[1]) ?: return null
    val endDate = parseFlexibleDate(match.groupValues[2]) ?: return null

    return startDate to endDate
}

private fun parseFlexibleDate(value: String): LocalDate? {
    val trimmed = value.trim()
    if (trimmed.isBlank()) return null

    val formatters = listOf(
        EUROPEAN_DATE_FORMATTER,
        DateTimeFormatter.ISO_LOCAL_DATE
    )

    for (formatter in formatters) {
        try {
            return LocalDate.parse(trimmed, formatter)
        } catch (_: DateTimeParseException) {
        }
    }

    return null
}

private fun normalizeDateRangeForStorage(value: String): String {
    val parsed = parseDateRange(value) ?: return value.trim()
    return "${parsed.first.format(EUROPEAN_DATE_FORMATTER)} - ${parsed.second.format(EUROPEAN_DATE_FORMATTER)}"
}

private fun normalizeDateRangeForEditing(value: String): String {
    val parsed = parseDateRange(value) ?: return value.trim()
    return "${parsed.first.format(EUROPEAN_DATE_FORMATTER)} - ${parsed.second.format(EUROPEAN_DATE_FORMATTER)}"
}

private fun validateDateRangeMessage(
    value: String,
    requiredMessage: String,
    exampleMessage: String,
    pastMessage: String,
    endBeforeStartMessage: String
): String? {
    val trimmed = value.trim()

    if (trimmed.isBlank()) {
        return requiredMessage
    }

    val parseResult = parseDateRange(trimmed)
        ?: return exampleMessage

    val startDate = parseResult.first
    val endDate = parseResult.second
    val today = LocalDate.now()

    if (startDate.isBefore(today) || endDate.isBefore(today)) {
        return pastMessage
    }

    if (endDate.isBefore(startDate)) {
        return endBeforeStartMessage
    }

    return null
}

private data class CalendarDayCell(
    val date: LocalDate?,
    val isFromCurrentMonth: Boolean
)

private fun buildMonthGrid(month: YearMonth): List<List<CalendarDayCell>> {
    val firstDayOfMonth = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()
    val leadingEmptyCells = firstDayOfMonth.dayOfWeek.value - DayOfWeek.MONDAY.value

    val cells = MutableList(42) { index ->
        val dayNumber = index - leadingEmptyCells + 1

        if (dayNumber in 1..daysInMonth) {
            CalendarDayCell(
                date = month.atDay(dayNumber),
                isFromCurrentMonth = true
            )
        } else {
            CalendarDayCell(
                date = null,
                isFromCurrentMonth = false
            )
        }
    }

    return cells.chunked(7)
}

private fun isDateInRange(
    date: LocalDate,
    startDate: LocalDate?,
    endDate: LocalDate?
): Boolean {
    if (startDate == null || endDate == null) return false
    return !date.isBefore(startDate) && !date.isAfter(endDate)
}

private fun isSameDay(date: LocalDate?, other: LocalDate?): Boolean {
    return date != null && other != null && date == other
}

private fun formatYearMonthLabel(yearMonth: YearMonth): String {
    val monthName = yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
    return "${monthName.replaceFirstChar { it.uppercase() }} ${yearMonth.year}"
}

@Composable
private fun AutoDurationField(
    value: String,
    errorMessage: String?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(R.string.trips_field_duration),
            fontSize = 12.sp,
            color = if (errorMessage != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ),
            border = BorderStroke(
                1.dp,
                if (errorMessage != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                }
            )
        ) {
            Text(
                text = if (value.isBlank()) stringResource(R.string.field_duration_auto_placeholder) else localizedTripDuration(value),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp),
                color = if (value.isBlank()) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun YearPickerDialog(
    currentYear: Int,
    onYearSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var startYear by remember(currentYear) { mutableStateOf(currentYear - 5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dialog_close))
            }
        },
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { startYear -= 12 }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.date_picker_prev_years)
                    )
                }

                Text(
                    text = "${startYear} - ${startYear + 11}",
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(onClick = { startYear += 12 }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.date_picker_next_years)
                    )
                }
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        repeat(3) { column ->
                            val year = startYear + row * 3 + column
                            val isSelected = year == currentYear

                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { onYearSelected(year) },
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                    } else {
                                        MaterialTheme.colorScheme.surface
                                    }
                                ),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    }
                                )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = year.toString(),
                                        color = if (isSelected) {
                                            MaterialTheme.colorScheme.primary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun CustomDateRangeDialog(
    initialStartDate: LocalDate?,
    initialEndDate: LocalDate?,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate, LocalDate) -> Unit
) {
    var displayedMonth by remember(initialStartDate, initialEndDate) {
        mutableStateOf(
            YearMonth.from(initialStartDate ?: initialEndDate ?: LocalDate.now())
        )
    }

    var selectedStartDate by remember(initialStartDate) { mutableStateOf(initialStartDate) }
    var selectedEndDate by remember(initialEndDate) { mutableStateOf(initialEndDate) }
    var showYearDialog by remember { mutableStateOf(false) }
    val today = remember { LocalDate.now() }

    val monthGrid = remember(displayedMonth) {
        buildMonthGrid(displayedMonth)
    }

    val weekdayLabels = remember {
        DayOfWeek.values().map { day ->
            day.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val start = selectedStartDate
                    val end = selectedEndDate
                    if (start != null && end != null) {
                        onConfirm(start, end)
                    }
                },
                enabled = selectedStartDate != null && selectedEndDate != null
            ) {
                Text(stringResource(R.string.action_ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.trips_btn_cancel))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { displayedMonth = displayedMonth.minusMonths(1) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowLeft,
                            contentDescription = stringResource(R.string.date_picker_prev_month)
                        )
                    }

                    Text(
                        text = formatYearMonthLabel(displayedMonth),
                        modifier = Modifier.clickable { showYearDialog = true },
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { displayedMonth = displayedMonth.plusMonths(1) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = stringResource(R.string.date_picker_next_month)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    weekdayLabels.forEach { label ->
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    monthGrid.forEach { week ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            week.forEach { cell ->
                                val date = cell.date
                                val isDisabled = date == null || date.isBefore(today)
                                val isStart = isSameDay(date, selectedStartDate)
                                val isEnd = isSameDay(date, selectedEndDate)
                                val isSingleDayRange =
                                    selectedStartDate != null &&
                                            selectedEndDate != null &&
                                            selectedStartDate == selectedEndDate &&
                                            isStart && isEnd

                                val isBetween = if (date != null) {
                                    isDateInRange(date, selectedStartDate, selectedEndDate) &&
                                            !isStart && !isEnd
                                } else {
                                    false
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when {
                                                isDisabled -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                                isSingleDayRange -> MaterialTheme.colorScheme.primary
                                                isStart || isEnd -> MaterialTheme.colorScheme.primary
                                                isBetween -> MaterialTheme.colorScheme.primary.copy(alpha = 0.14f)
                                                else -> MaterialTheme.colorScheme.surface
                                            }
                                        )
                                        .clickable(enabled = !isDisabled) {
                                            if (date == null || date.isBefore(today)) return@clickable

                                            when {
                                                selectedStartDate == null -> {
                                                    selectedStartDate = date
                                                    selectedEndDate = null
                                                }

                                                selectedEndDate == null -> {
                                                    if (date.isBefore(selectedStartDate)) {
                                                        selectedStartDate = date
                                                    } else {
                                                        selectedEndDate = date
                                                    }
                                                }

                                                else -> {
                                                    selectedStartDate = date
                                                    selectedEndDate = null
                                                }
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = date?.dayOfMonth?.toString().orEmpty(),
                                        color = when {
                                            date == null -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0f)
                                            isDisabled -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                                            isStart || isEnd -> MaterialTheme.colorScheme.onPrimary
                                            else -> MaterialTheme.colorScheme.onSurface
                                        },
                                        fontWeight = if (isStart || isEnd) {
                                            FontWeight.SemiBold
                                        } else {
                                            FontWeight.Normal
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Text(
                    text = when {
                        selectedStartDate != null && selectedEndDate != null ->
                            "${selectedStartDate!!.format(EUROPEAN_DATE_FORMATTER)} - ${selectedEndDate!!.format(EUROPEAN_DATE_FORMATTER)}"
                        selectedStartDate != null ->
                            selectedStartDate!!.format(EUROPEAN_DATE_FORMATTER)
                        else ->
                            stringResource(R.string.date_picker_select_start_end)
                    },
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    )

    if (showYearDialog) {
        YearPickerDialog(
            currentYear = displayedMonth.year,
            onYearSelected = { selectedYear ->
                displayedMonth = YearMonth.of(selectedYear, displayedMonth.month)
                showYearDialog = false
            },
            onDismiss = { showYearDialog = false }
        )
    }
}

@Composable
private fun DateRangePickerField(
    value: String,
    errorMessage: String?,
    onValueChange: (String) -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = stringResource(R.string.trips_field_date_range),
            fontSize = 12.sp,
            color = if (errorMessage != null) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = BorderStroke(
                1.dp,
                if (errorMessage != null) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.outline
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (value.isBlank()) stringResource(R.string.date_picker_select_range) else value,
                    color = if (value.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )

                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = stringResource(R.string.date_picker_select_range),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    if (showDialog) {
        val currentRange = parseDateRange(value)

        CustomDateRangeDialog(
            initialStartDate = currentRange?.first,
            initialEndDate = currentRange?.second,
            onDismiss = { showDialog = false },
            onConfirm = { startDate, endDate ->
                onValueChange(
                    "${startDate.format(EUROPEAN_DATE_FORMATTER)} - ${endDate.format(EUROPEAN_DATE_FORMATTER)}"
                )
                showDialog = false
            }
        )
    }
}
