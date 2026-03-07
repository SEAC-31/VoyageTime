package com.example.voyagetime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TripItem(
    val id: String,
    val destination: String,
    val country: String,
    val dateRange: String,
    val duration: String,
    val budget: String,
    val status: String
)

data class TripStat(
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
                status = "Upcoming"
            ),
            TripItem(
                id = "tokyo",
                destination = "Tokyo",
                country = "Japan",
                dateRange = "02 Aug - 11 Aug 2026",
                duration = "9 days",
                budget = "€2,450",
                status = "Planned"
            ),
            TripItem(
                id = "amsterdam",
                destination = "Amsterdam",
                country = "Netherlands",
                dateRange = "21 Sep - 25 Sep 2026",
                duration = "4 days",
                budget = "€680",
                status = "Upcoming"
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
                status = "Completed"
            ),
            TripItem(
                id = "rome",
                destination = "Rome",
                country = "Italy",
                dateRange = "15 Jan - 20 Jan 2026",
                duration = "5 days",
                budget = "€740",
                status = "Completed"
            )
        )
    }

    val stats = remember {
        listOf(
            TripStat("5", "Total Trips", Icons.Default.TravelExplore),
            TripStat("27", "Days Planned", Icons.Default.CalendarMonth),
            TripStat("€4,980", "Estimated Budget", Icons.Default.AttachMoney)
        )
    }

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

        TripsOverviewSection(stats = stats)

        TripCategory(title = "Upcoming Trips") {
            upcomingTrips.forEachIndexed { index, trip ->
                TripCardItem(
                    trip = trip,
                    onItineraryClick = { onTripClick(trip.id) }
                )
                if (index != upcomingTrips.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }

        TripCategory(title = "Past Trips") {
            pastTrips.forEachIndexed { index, trip ->
                TripCardItem(
                    trip = trip,
                    onItineraryClick = { onTripClick(trip.id) }
                )
                if (index != pastTrips.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }

        TripCategory(title = "Travel Insights") {
            InfoRow(
                icon = Icons.Default.Explore,
                title = "Favorite Region",
                subtitle = "Europe"
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            InfoRow(
                icon = Icons.Default.FlightTakeoff,
                title = "Next Departure",
                subtitle = "Paris — 12 Jun 2026"
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            InfoRow(
                icon = Icons.Default.TrendingUp,
                title = "Travel Goal",
                subtitle = "Visit 3 new cities this year"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TripsOverviewSection(stats: List<TripStat>) {
    Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
        Text(
            text = "OVERVIEW",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.2.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                stats.forEach { stat ->
                    StatBox(
                        stat = stat,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatBox(
    stat: TripStat,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .padding(8.dp)
        ) {
            Icon(
                imageVector = stat.icon,
                contentDescription = stat.label,
                tint = MaterialTheme.colorScheme.primary
            )
        }

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

@Composable
fun TripCategory(
    title: String,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
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
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
fun TripCardItem(
    trip: TripItem,
    onItineraryClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = trip.destination,
                tint = MaterialTheme.colorScheme.primary
            )
        }

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

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniInfo(
                    icon = Icons.Default.CalendarMonth,
                    text = trip.dateRange
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MiniInfo(
                    icon = Icons.Default.CalendarMonth,
                    text = trip.duration
                )
                MiniInfo(
                    icon = Icons.Default.AttachMoney,
                    text = trip.budget
                )
            }

            Text(
                text = trip.status,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open itinerary",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onItineraryClick() }
        )
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
fun InfoRow(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
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
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}