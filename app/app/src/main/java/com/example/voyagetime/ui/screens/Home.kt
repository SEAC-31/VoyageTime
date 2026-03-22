package com.example.voyagetime.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.voyagetime.R

data class HomeTripSummary(
    val id: String,
    val destination: String,
    val country: String,
    val startDate: String,
    val endDate: String,
    val duration: String,
    val budget: Int,
    val image: Int,
    val statusKey: Int
)

enum class HomeDialogType { TRIPS, DAYS, BUDGET }

// label is String so Trips.kt (which also uses HomeStat) stays compatible
data class HomeStat(
    val value: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun Home(
    modifier: Modifier = Modifier,
    onTripClick: (String) -> Unit,
    onDepartureCityClick: () -> Unit,
    onTravelStyleClick: () -> Unit,
    onAddNewTripClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val allTrips = remember {
        listOf(
            HomeTripSummary("paris", "Paris", "France", "12 Jun 2026", "18 Jun 2026", "6 days", 820, R.drawable.paris, R.string.status_upcoming),
            HomeTripSummary("tokyo", "Tokyo", "Japan", "02 Aug 2026", "11 Aug 2026", "9 days", 2450, R.drawable.tokyo, R.string.status_planned),
            HomeTripSummary("barcelona", "Barcelona", "Spain", "10 Mar 2026", "13 Mar 2026", "3 days", 290, R.drawable.barcelona, R.string.status_completed),
            HomeTripSummary("newyork", "New York", "United States", "04 Dec 2025", "10 Dec 2025", "6 days", 1680, R.drawable.newyork, R.string.status_completed)
        )
    }

    val totalBudget = allTrips.sumOf { it.budget }

    // stringResource must be called in composable scope, outside remember
    val labelTrips = stringResource(R.string.stat_trips)
    val labelDays = stringResource(R.string.stat_days_planned)
    val labelBudget = stringResource(R.string.stat_budget)

    val stats = remember(totalBudget, labelTrips, labelDays, labelBudget) {
        listOf(
            HomeStat(allTrips.size.toString(), labelTrips, Icons.Default.TravelExplore),
            HomeStat(allTrips.sumOf { extractDays(it.duration) }.toString(), labelDays, Icons.Default.CalendarMonth),
            HomeStat("€$totalBudget", labelBudget, Icons.Default.AttachMoney)
        )
    }

    val featuredTrips = remember(allTrips) { listOf(allTrips[1], allTrips[3]) }
    var activeDialog by remember { mutableStateOf<HomeDialogType?>(null) }

    activeDialog?.let { dialogType ->
        HomeOverviewDialog(dialogType = dialogType, trips = allTrips, onDismiss = { activeDialog = null })
    }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        HomeHeader()

        Button(onClick = onAddNewTripClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.home_add_trip), fontWeight = FontWeight.SemiBold)
        }

        HomeSection(title = stringResource(R.string.home_section_overview)) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                stats.forEachIndexed { index, stat ->
                    val dialogType = when (index) { 0 -> HomeDialogType.TRIPS; 1 -> HomeDialogType.DAYS; else -> HomeDialogType.BUDGET }
                    HomeStatCard(stat = stat, modifier = Modifier.weight(1f), onClick = { activeDialog = dialogType })
                }
            }
        }

        HomeSection(title = stringResource(R.string.home_section_next_trip)) {
            NextTripCard(trip = allTrips[0], onClick = { onTripClick(allTrips[0].id) })
        }

        HomeSection(title = stringResource(R.string.home_section_featured)) {
            featuredTrips.forEachIndexed { index, trip ->
                HomeFeaturedTripCard(trip = trip, onClick = { onTripClick(trip.id) })
                if (index != featuredTrips.lastIndex)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f))
            }
        }

        HomeSection(title = stringResource(R.string.home_section_quick_info)) {
            HomeInfoRow(icon = Icons.Default.LocationOn, title = stringResource(R.string.home_departure_city), subtitle = stringResource(R.string.home_departure_default), onClick = onDepartureCityClick)
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp), color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f))
            HomeInfoRow(icon = Icons.Default.Explore, title = stringResource(R.string.home_travel_style), subtitle = stringResource(R.string.home_style_default), onClick = onTravelStyleClick)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun extractDays(duration: String): Int = duration.substringBefore(" ").toIntOrNull() ?: 0

@Composable
fun HomeOverviewDialog(dialogType: HomeDialogType, trips: List<HomeTripSummary>, onDismiss: () -> Unit) {
    val title: String
    val text: String
    when (dialogType) {
        HomeDialogType.TRIPS -> {
            title = stringResource(R.string.dialog_trips_title)
            text = buildString {
                appendLine(stringResource(R.string.dialog_trips_header, trips.size))
                appendLine()
                trips.forEach { appendLine("• ${it.destination} (${it.country})") }
            }
        }
        HomeDialogType.DAYS -> {
            title = stringResource(R.string.dialog_days_title)
            text = buildString {
                appendLine(stringResource(R.string.dialog_days_header))
                appendLine()
                trips.forEach { appendLine("• ${it.destination}: ${it.startDate} - ${it.endDate}") }
            }
        }
        HomeDialogType.BUDGET -> {
            val total = trips.sumOf { it.budget }
            title = stringResource(R.string.dialog_budget_title)
            text = buildString {
                appendLine(stringResource(R.string.dialog_budget_header))
                appendLine()
                trips.forEach { appendLine("• ${it.destination}: €${it.budget}") }
                appendLine()
                append(stringResource(R.string.dialog_budget_total, total))
            }
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_close)) } },
        title = { Text(title) },
        text = { Text(text) }
    )
}

@Composable
fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.90f)).padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.size(100.dp).clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface).padding(10.dp),
            contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = R.drawable.logo_no_background),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(80.dp), contentScale = ContentScale.FillHeight)
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = stringResource(R.string.app_name), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = stringResource(R.string.home_dashboard_subtitle), fontSize = 14.sp, lineHeight = 20.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f))
        }
    }
}

@Composable
fun HomeSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(text = title.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 4.dp, bottom = 6.dp))
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.50f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)) {
            Column(modifier = Modifier.fillMaxWidth()) { content() }
        }
    }
}

@Composable
fun HomeStatCard(stat: HomeStat, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier = modifier.clip(RoundedCornerShape(16.dp))
        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
        .clickable { onClick() }.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)).padding(8.dp)) {
            Icon(imageVector = stat.icon, contentDescription = stat.label, tint = MaterialTheme.colorScheme.primary)
        }
        Text(text = stat.value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(text = stat.label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
    }
}

@Composable
fun NextTripCard(trip: HomeTripSummary, onClick: () -> Unit) {
    val status = stringResource(trip.statusKey)
    Column(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Image(painter = painterResource(id = trip.image), contentDescription = trip.destination,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(18.dp)))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)).padding(10.dp), contentAlignment = Alignment.Center) {
                Icon(imageVector = Icons.Default.FlightTakeoff, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "${trip.destination}, ${trip.country}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(text = "${trip.startDate} - ${trip.endDate}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
            }
            Text(text = status, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeMiniInfo(icon = Icons.Default.CalendarMonth, text = trip.duration)
            HomeMiniInfo(icon = Icons.Default.AttachMoney, text = "€${trip.budget}")
        }
        Text(text = stringResource(R.string.home_tap_itinerary), fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f))
    }
}

@Composable
fun HomeFeaturedTripCard(trip: HomeTripSummary, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(painter = painterResource(id = trip.image), contentDescription = trip.destination,
            contentScale = ContentScale.Crop, modifier = Modifier.width(92.dp).height(92.dp).clip(RoundedCornerShape(16.dp)))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = trip.destination, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = trip.country, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
            HomeMiniInfo(icon = Icons.Default.CalendarMonth, text = "${trip.startDate} - ${trip.endDate}")
            HomeMiniInfo(icon = Icons.Default.AttachMoney, text = "€${trip.budget}")
        }
    }
}

@Composable
fun HomeMiniInfo(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f), modifier = Modifier.padding(end = 4.dp))
        Text(text = text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f))
    }
}

@Composable
fun HomeInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)).padding(10.dp), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = title, tint = MaterialTheme.colorScheme.primary)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            Text(text = subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}