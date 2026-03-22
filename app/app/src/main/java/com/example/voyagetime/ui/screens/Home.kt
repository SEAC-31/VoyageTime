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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
    val status: String
)

enum class HomeDialogType {
    TRIPS, DAYS, BUDGET
}

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
            HomeTripSummary(
                id = "paris",
                destination = "Paris",
                country = "France",
                startDate = "12 Jun 2026",
                endDate = "18 Jun 2026",
                duration = "6 days",
                budget = 820,
                image = R.drawable.paris,
                status = "Upcoming"
            ),
            HomeTripSummary(
                id = "tokyo",
                destination = "Tokyo",
                country = "Japan",
                startDate = "02 Aug 2026",
                endDate = "11 Aug 2026",
                duration = "9 days",
                budget = 2450,
                image = R.drawable.tokyo,
                status = "Planned"
            ),
            HomeTripSummary(
                id = "barcelona",
                destination = "Barcelona",
                country = "Spain",
                startDate = "10 Mar 2026",
                endDate = "13 Mar 2026",
                duration = "3 days",
                budget = 290,
                image = R.drawable.barcelona,
                status = "Completed"
            ),
            HomeTripSummary(
                id = "newyork",
                destination = "New York",
                country = "United States",
                startDate = "04 Dec 2025",
                endDate = "10 Dec 2025",
                duration = "6 days",
                budget = 1680,
                image = R.drawable.newyork,
                status = "Completed"
            )
        )
    }

    val totalBudget = allTrips.sumOf { it.budget }

    val stats = remember(totalBudget) {
        listOf(
            HomeStat(allTrips.size.toString(), "Trips", Icons.Default.TravelExplore),
            HomeStat(allTrips.sumOf { extractDays(it.duration) }.toString(), "Days Planned", Icons.Default.CalendarMonth),
            HomeStat("€$totalBudget", "Budget", Icons.Default.AttachMoney)
        )
    }

    val featuredTrips = remember(allTrips) {
        listOf(allTrips[1], allTrips[3]) // Tokyo + New York
    }

    var activeDialog by remember { mutableStateOf<HomeDialogType?>(null) }

    activeDialog?.let { dialogType ->
        HomeOverviewDialog(
            dialogType = dialogType,
            trips = allTrips,
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
                contentDescription = "Add new trip"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Add New Trip",
                fontWeight = FontWeight.SemiBold
            )
        }

        HomeSection(title = "Overview") {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stats.forEachIndexed { index, stat ->
                    val dialogType = when (index) {
                        0 -> HomeDialogType.TRIPS
                        1 -> HomeDialogType.DAYS
                        else -> HomeDialogType.BUDGET
                    }

                }
            }
        }

        HomeSection(title = "Next Trip") {
            NextTripCard(
                trip = allTrips[0],
                onClick = { onTripClick(allTrips[0].id) }
            )
        }

        HomeSection(title = "Featured Trips") {
            featuredTrips.forEachIndexed { index, trip ->
                HomeFeaturedTripCard(
                    trip = trip,
                    onClick = { onTripClick(trip.id) }
                )
                if (index != featuredTrips.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
                    )
                }
            }
        }

        HomeSection(title = "Quick Info") {
            HomeInfoRow(
                icon = Icons.Default.LocationOn,
                title = "Departure City",
                subtitle = "Barcelona",
                onClick = onDepartureCityClick
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
            )
            HomeInfoRow(
                icon = Icons.Default.Explore,
                title = "Travel Style",
                subtitle = "City break, culture and food",
                onClick = onTravelStyleClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

private fun extractDays(duration: String): Int {
    return duration.substringBefore(" ").toIntOrNull() ?: 0
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
            title = "Trips Overview"
            text = buildString {
                appendLine("You currently have ${trips.size} trips:")
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination} (${trip.country})")
                }
            }
        }

        HomeDialogType.DAYS -> {
            title = "Days Planned"
            text = buildString {
                appendLine("Trip dates:")
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination}: ${trip.startDate} - ${trip.endDate}")
                }
            }
        }

        HomeDialogType.BUDGET -> {
            val total = trips.sumOf { it.budget }
            title = "Budget Details"
            text = buildString {
                appendLine("Estimated costs by trip:")
                appendLine()
                trips.forEach { trip ->
                    appendLine("• ${trip.destination}: €${trip.budget}")
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
fun HomeHeader() {
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
                contentDescription = "VoyageTime logo",
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.FillHeight
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "VoyageTime",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Your travel dashboard for plans, itineraries and memorable trips.",
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
        Image(
            painter = painterResource(id = trip.image),
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
                    contentDescription = "Next trip",
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
                text = trip.status,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeMiniInfo(
                icon = Icons.Default.CalendarMonth,
                text = trip.duration
            )
            HomeMiniInfo(
                icon = Icons.Default.AttachMoney,
                text = "€${trip.budget}"
            )
        }

        Text(
            text = "Tap to open the itinerary of this trip.",
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
        Image(
            painter = painterResource(id = trip.image),
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
                text = trip.destination,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = trip.country,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
            contentDescription = "Edit",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}