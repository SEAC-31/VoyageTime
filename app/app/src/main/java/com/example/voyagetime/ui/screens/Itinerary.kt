package com.example.voyagetime.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tour
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
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
    val date: String,
    val totalActivities: String,
    val estimatedBudget: String
)

@Composable
fun Itinerary(
    tripId: String,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val summary: ItinerarySummary
    val morningPlan: List<ItineraryEvent>
    val afternoonPlan: List<ItineraryEvent>
    val eveningPlan: List<ItineraryEvent>

    when (tripId) {
        "tokyo" -> {
            summary = ItinerarySummary(
                destination = "Tokyo, Japan",
                date = "03 Aug 2026",
                totalActivities = "5 activities",
                estimatedBudget = "€120"
            )

            morningPlan = listOf(
                ItineraryEvent(
                    time = "08:30",
                    title = "Breakfast in Shibuya",
                    location = "Shibuya Station Area",
                    cost = "€12",
                    icon = Icons.Default.Restaurant
                ),
                ItineraryEvent(
                    time = "10:30",
                    title = "Meiji Shrine Visit",
                    location = "Shibuya",
                    cost = "Free",
                    icon = Icons.Default.Tour
                )
            )

            afternoonPlan = listOf(
                ItineraryEvent(
                    time = "13:00",
                    title = "Lunch in Harajuku",
                    location = "Takeshita Street",
                    cost = "€18",
                    icon = Icons.Default.Restaurant
                ),
                ItineraryEvent(
                    time = "15:00",
                    title = "Tokyo Skytree",
                    location = "Sumida",
                    cost = "€24",
                    icon = Icons.Default.Place
                )
            )

            eveningPlan = listOf(
                ItineraryEvent(
                    time = "19:30",
                    title = "Dinner in Akihabara",
                    location = "Akihabara",
                    cost = "€32",
                    icon = Icons.Default.Map
                )
            )
        }

        "amsterdam" -> {
            summary = ItinerarySummary(
                destination = "Amsterdam, Netherlands",
                date = "22 Sep 2026",
                totalActivities = "5 activities",
                estimatedBudget = "€88"
            )

            morningPlan = listOf(
                ItineraryEvent(
                    time = "08:00",
                    title = "Breakfast by the canal",
                    location = "Jordaan",
                    cost = "€11",
                    icon = Icons.Default.Restaurant
                ),
                ItineraryEvent(
                    time = "10:00",
                    title = "Anne Frank House",
                    location = "Prinsengracht",
                    cost = "€16",
                    icon = Icons.Default.Tour
                )
            )

            afternoonPlan = listOf(
                ItineraryEvent(
                    time = "13:00",
                    title = "Lunch in city center",
                    location = "Dam Square",
                    cost = "€17",
                    icon = Icons.Default.Restaurant
                ),
                ItineraryEvent(
                    time = "15:00",
                    title = "Canal Cruise",
                    location = "Central Amsterdam",
                    cost = "€21",
                    icon = Icons.Default.Map
                )
            )

            eveningPlan = listOf(
                ItineraryEvent(
                    time = "19:00",
                    title = "Museumplein Walk",
                    location = "Museumplein",
                    cost = "Free",
                    icon = Icons.Default.Place
                )
            )
        }

        "barcelona" -> {
            summary = ItinerarySummary(
                destination = "Barcelona, Spain",
                date = "11 Mar 2026",
                totalActivities = "4 activities",
                estimatedBudget = "€54"
            )

            morningPlan = listOf(
                ItineraryEvent(
                    time = "09:00",
                    title = "Breakfast near Plaça Catalunya",
                    location = "City Center",
                    cost = "€9",
                    icon = Icons.Default.Restaurant
                ),
                ItineraryEvent(
                    time = "11:00",
                    title = "Sagrada Família Visit",
                    location = "Eixample",
                    cost = "€26",
                    icon = Icons.Default.Tour
                )
            )

            afternoonPlan = listOf(
                ItineraryEvent(
                    time = "14:00",
                    title = "Lunch in El Born",
                    location = "El Born",
                    cost = "€19",
                    icon = Icons.Default.Restaurant
                )
            )

            eveningPlan = listOf(
                ItineraryEvent(
                    time = "18:00",
                    title = "Walk at Barceloneta",
                    location = "Barceloneta",
                    cost = "Free",
                    icon = Icons.Default.Map
                )
            )
        }

        "rome" -> {
            summary = ItinerarySummary(
                destination = "Rome, Italy",
                date = "16 Jan 2026",
                totalActivities = "4 activities",
                estimatedBudget = "€73"
            )

            morningPlan = listOf(
                ItineraryEvent(
                    time = "08:30",
                    title = "Breakfast near Termini",
                    location = "Rome Center",
                    cost = "€10",
                    icon = Icons.Default.Restaurant
                ),
                ItineraryEvent(
                    time = "10:30",
                    title = "Colosseum Visit",
                    location = "Piazza del Colosseo",
                    cost = "€24",
                    icon = Icons.Default.Tour
                )
            )

            afternoonPlan = listOf(
                ItineraryEvent(
                    time = "13:30",
                    title = "Lunch in Monti",
                    location = "Monti District",
                    cost = "€21",
                    icon = Icons.Default.Restaurant
                )
            )

            eveningPlan = listOf(
                ItineraryEvent(
                    time = "18:30",
                    title = "Trevi Fountain Walk",
                    location = "Trevi",
                    cost = "Free",
                    icon = Icons.Default.Place
                )
            )
        }

        else -> {
            summary = ItinerarySummary(
                destination = "Paris, France",
                date = "14 Jun 2026",
                totalActivities = "5 activities",
                estimatedBudget = "€97"
            )

            morningPlan = listOf(
                ItineraryEvent(
                    time = "08:00",
                    title = "Breakfast at Café de Flore",
                    location = "Saint-Germain-des-Prés",
                    cost = "€14",
                    icon = Icons.Default.Restaurant
                ),
                ItineraryEvent(
                    time = "10:00",
                    title = "Visit Louvre Museum",
                    location = "Rue de Rivoli",
                    cost = "€22",
                    icon = Icons.Default.Tour
                )
            )

            afternoonPlan = listOf(
                ItineraryEvent(
                    time = "13:00",
                    title = "Lunch near Tuileries",
                    location = "1st arrondissement",
                    cost = "€18",
                    icon = Icons.Default.Restaurant
                ),
                ItineraryEvent(
                    time = "15:30",
                    title = "Seine River Walk",
                    location = "Pont Neuf",
                    cost = "Free",
                    icon = Icons.Default.Map
                )
            )

            eveningPlan = listOf(
                ItineraryEvent(
                    time = "19:00",
                    title = "Eiffel Tower Visit",
                    location = "Champ de Mars",
                    cost = "€26",
                    icon = Icons.Default.Place
                )
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            text = "Itinerary",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        ItineraryOverviewCard(summary = summary)

        ItinerarySection(title = "Morning Plan") {
            morningPlan.forEachIndexed { index, event ->
                ItineraryEventItem(event = event)
                if (index != morningPlan.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }

        ItinerarySection(title = "Afternoon Plan") {
            afternoonPlan.forEachIndexed { index, event ->
                ItineraryEventItem(event = event)
                if (index != afternoonPlan.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }

        ItinerarySection(title = "Evening Plan") {
            eveningPlan.forEachIndexed { index, event ->
                ItineraryEventItem(event = event)
                if (index != eveningPlan.lastIndex) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
                }
            }
        }

        ItinerarySection(title = "Trip Notes") {
            InfoLine(
                icon = Icons.Default.Train,
                title = "Transport",
                subtitle = "Metro day pass recommended"
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            InfoLine(
                icon = Icons.Default.LocationOn,
                title = "Meeting Point",
                subtitle = "Hotel lobby at 07:45"
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            InfoLine(
                icon = Icons.Default.CalendarMonth,
                title = "Weather Plan",
                subtitle = "Carry a light jacket for the evening"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun ItineraryOverviewCard(summary: ItinerarySummary) {
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = summary.destination,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                InfoMiniRow(
                    icon = Icons.Default.CalendarMonth,
                    text = summary.date
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OverviewPill(
                        icon = Icons.Default.Schedule,
                        value = summary.totalActivities
                    )
                    OverviewPill(
                        icon = Icons.Default.AttachMoney,
                        value = summary.estimatedBudget
                    )
                }
            }
        }
    }
}

@Composable
fun OverviewPill(
    icon: ImageVector,
    value: String
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.10f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ItinerarySection(
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
fun ItineraryEventItem(event: ItineraryEvent) {
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
                imageVector = event.icon,
                contentDescription = event.title,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = event.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = event.location,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.60f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                InfoMiniRow(
                    icon = Icons.Default.Schedule,
                    text = event.time
                )
                InfoMiniRow(
                    icon = Icons.Default.AttachMoney,
                    text = event.cost
                )
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
    }
}

@Composable
fun InfoMiniRow(
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
fun InfoLine(
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