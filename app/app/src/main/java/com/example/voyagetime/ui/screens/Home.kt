package com.example.voyagetime.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.voyagetime.R
import com.example.voyagetime.ui.viewmodels.HomeViewModel

data class HomeTripSummary(
    val id: String,
    val destination: String,
    val country: String,
    val startDate: String,
    val endDate: String,
    val duration: String,
    val budget: Int,
    val image: Int,
    val coverImageUri: String? = null,
    val status: String
)

enum class HomeDialogType {
    TRIPS, DAYS, BUDGET
}

@Composable
private fun localizedDuration(rawDuration: String): String {
    val days = rawDuration.substringBefore(" ").trim().toIntOrNull() ?: return rawDuration
    return if (days == 1) {
        stringResource(R.string.duration_single_day)
    } else {
        stringResource(R.string.duration_multiple_days, days)
    }
}

@Composable
private fun localizedTripStatus(rawStatus: String): String {
    return when {
        rawStatus.equals("Upcoming", ignoreCase = true) -> stringResource(R.string.status_upcoming)
        rawStatus.equals("Planned", ignoreCase = true) -> stringResource(R.string.status_planned)
        rawStatus.equals("Completed", ignoreCase = true) -> stringResource(R.string.status_completed)
        else -> rawStatus
    }
}

@Composable
fun Home(
    modifier: Modifier = Modifier,
    onTripClick: (String) -> Unit,
    onDepartureCityClick: () -> Unit,
    onTravelStyleClick: () -> Unit,
    onAddNewTripClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.reload()
    }
    var activeDialog by remember { mutableStateOf<HomeDialogType?>(null) }

    activeDialog?.let { dialogType ->
        HomeOverviewDialog(
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
        HomeHeader()

        Button(
            onClick = onAddNewTripClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.home_add_trip)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.home_add_trip),
                fontWeight = FontWeight.SemiBold
            )
        }

        HomeSection(title = stringResource(R.string.home_section_overview)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.stats.forEachIndexed { index, stat ->
                    val dialogType = when (index) {
                        0 -> HomeDialogType.TRIPS
                        1 -> HomeDialogType.DAYS
                        else -> HomeDialogType.BUDGET
                    }

                    HomeStatCard(
                        stat = stat,
                        modifier = Modifier.weight(1f),
                        onClick = { activeDialog = dialogType }
                    )
                }
            }
        }

        uiState.nextTrip?.let { nextTrip ->
            HomeSection(title = stringResource(R.string.home_section_next_trip)) {
                NextTripCard(
                    trip = nextTrip,
                    onClick = { onTripClick(nextTrip.id) }
                )
            }
        }

        if (uiState.featuredTrips.isNotEmpty()) {
            HomeSection(title = stringResource(R.string.home_section_featured)) {
                uiState.featuredTrips.forEachIndexed { index, trip ->
                    HomeFeaturedTripCard(
                        trip = trip,
                        onClick = { onTripClick(trip.id) }
                    )
                    if (index != uiState.featuredTrips.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
                        )
                    }
                }
            }
        }

        HomeSection(title = stringResource(R.string.home_section_quick_info)) {
            HomeInfoRow(
                icon = Icons.Default.LocationOn,
                title = stringResource(R.string.home_departure_city),
                subtitle = stringResource(R.string.home_departure_default),
                onClick = onDepartureCityClick
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
            )
            HomeInfoRow(
                icon = Icons.Default.Explore,
                title = stringResource(R.string.home_travel_style),
                subtitle = stringResource(R.string.home_style_default),
                onClick = onTravelStyleClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun HomeOverviewDialog(
    dialogType: HomeDialogType,
    trips: List<HomeTripSummary>,
    onDismiss: () -> Unit
) {
    val title: String
    val text: String

    when (dialogType) {
        HomeDialogType.TRIPS -> {
            title = stringResource(R.string.dialog_trips_title)
            text = buildString {
                appendLine(stringResource(R.string.dialog_trips_header, trips.size))
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination} (${trip.country})")
                }
            }
        }

        HomeDialogType.DAYS -> {
            title = stringResource(R.string.dialog_days_title)
            text = buildString {
                appendLine(stringResource(R.string.dialog_days_header))
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination}: ${trip.startDate} - ${trip.endDate}")
                }
            }
        }

        HomeDialogType.BUDGET -> {
            val total = trips.sumOf { it.budget }
            title = stringResource(R.string.dialog_budget_title)
            text = buildString {
                appendLine(stringResource(R.string.dialog_budget_header))
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination}: €${trip.budget}")
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
        title = {
            Text(text = title)
        },
        text = {
            Text(text = text)
        }
    )
}

@Composable
fun HomeHeader() {
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
                contentScale = ContentScale.FillHeight
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.app_name),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = stringResource(R.string.home_dashboard_subtitle),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
            )
        }
    }
}

@Composable
fun HomeSection(
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
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f)
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
fun NextTripCard(
    trip: HomeTripSummary,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        TripCoverImage(
            imageRes = trip.image,
            imageUri = trip.coverImageUri,
            contentDescription = trip.destination,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(18.dp))
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FlightTakeoff,
                    contentDescription = stringResource(R.string.home_section_next_trip),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${trip.destination}, ${trip.country}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${trip.startDate} - ${trip.endDate}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
            }

            Text(
                text = localizedTripStatus(trip.status),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeMiniInfo(
                icon = Icons.Default.CalendarMonth,
                text = localizedDuration(trip.duration)
            )
            HomeMiniInfo(
                icon = Icons.Default.AttachMoney,
                text = "€${trip.budget}"
            )
        }

        Text(
            text = stringResource(R.string.home_tap_itinerary),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
        )
    }
}

@Composable
fun HomeFeaturedTripCard(
    trip: HomeTripSummary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TripCoverImage(
            imageRes = trip.image,
            imageUri = trip.coverImageUri,
            contentDescription = trip.destination,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(92.dp)
                .height(92.dp)
                .clip(RoundedCornerShape(16.dp))
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

            HomeMiniInfo(
                icon = Icons.Default.CalendarMonth,
                text = "${trip.startDate} - ${trip.endDate}"
            )

            HomeMiniInfo(
                icon = Icons.Default.AttachMoney,
                text = "€${trip.budget}"
            )
        }
    }
}

@Composable
fun HomeMiniInfo(
    icon: ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )
    }
}

@Composable
fun HomeInfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
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
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = stringResource(R.string.trips_btn_edit),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}