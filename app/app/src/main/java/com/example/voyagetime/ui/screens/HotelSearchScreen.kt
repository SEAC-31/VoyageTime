package com.example.voyagetime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.voyagetime.data.remote.HotelDto
import com.example.voyagetime.data.remote.RoomDto
import com.example.voyagetime.ui.viewmodels.HotelBookingState
import com.example.voyagetime.ui.viewmodels.HotelBookingViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HotelSearchScreen(
    modifier: Modifier = Modifier,
    onHotelSelected: (HotelDto, String, String) -> Unit = { _, _, _ -> },
    viewModel: HotelBookingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    var selectedCity by rememberSaveable { mutableStateOf("Barcelona") }
    var startDate by rememberSaveable { mutableStateOf(LocalDate.now().plusDays(7).toString()) }
    var endDate by rememberSaveable { mutableStateOf(LocalDate.now().plusDays(10).toString()) }
    var pickingStart by remember { mutableStateOf(false) }
    var pickingEnd by remember { mutableStateOf(false) }

    val start = remember(startDate) { LocalDate.parse(startDate) }
    val end = remember(endDate) { LocalDate.parse(endDate) }
    val dateError = remember(start, end) {
        when {
            end.isBefore(start) -> "End date must be after start date"
            end == start -> "The stay needs at least one night"
            else -> null
        }
    }

    if (pickingStart) {
        HotelDatePickerDialog(
            initialDate = start,
            onDismiss = { pickingStart = false },
            onDateSelected = { picked ->
                startDate = picked.toString()
                if (!LocalDate.parse(endDate).isAfter(picked)) {
                    endDate = picked.plusDays(1).toString()
                }
                pickingStart = false
            }
        )
    }

    if (pickingEnd) {
        HotelDatePickerDialog(
            initialDate = end,
            onDismiss = { pickingEnd = false },
            onDateSelected = { picked ->
                endDate = picked.toString()
                pickingEnd = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Hotel search",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Search available hotels in London, Paris or Barcelona using city, start date and end date.",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.68f)
        )

        HotelSearchForm(
            selectedCity = selectedCity,
            onCitySelected = { selectedCity = it },
            startDate = start,
            endDate = end,
            dateError = dateError,
            onStartClick = { pickingStart = true },
            onEndClick = { pickingEnd = true },
            onSearchClick = {
                if (dateError == null) {
                    viewModel.searchHotels(selectedCity, startDate, endDate)
                }
            }
        )

        when (val currentState = state) {
            HotelBookingState.Idle -> SearchHintCard()
            HotelBookingState.Loading -> LoadingHotelsCard()
            is HotelBookingState.Error -> ErrorHotelsCard(currentState.message)
            is HotelBookingState.BookingSuccess -> SearchHintCard()
            is HotelBookingState.HotelsLoaded -> HotelsResultList(
                hotels = currentState.hotels,
                startDate = startDate,
                endDate = endDate,
                onHotelSelected = onHotelSelected
            )
        }
    }
}

@Composable
private fun HotelSearchForm(
    selectedCity: String,
    onCitySelected: (String) -> Unit,
    startDate: LocalDate,
    endDate: LocalDate,
    dateError: String?,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.38f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationCity, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("City", fontWeight = FontWeight.SemiBold)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Barcelona", "Paris", "London").forEach { city ->
                    if (selectedCity == city) {
                        Button(onClick = { onCitySelected(city) }) { Text(city) }
                    } else {
                        OutlinedButton(onClick = { onCitySelected(city) }) { Text(city) }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                DateSelectionButton(
                    label = "Start date",
                    date = startDate,
                    onClick = onStartClick,
                    modifier = Modifier.weight(1f)
                )
                DateSelectionButton(
                    label = "End date",
                    date = endDate,
                    onClick = onEndClick,
                    modifier = Modifier.weight(1f)
                )
            }

            if (dateError != null) {
                Text(
                    text = dateError,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = onSearchClick,
                enabled = dateError == null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Search availability")
            }
        }
    }
}

@Composable
private fun DateSelectionButton(
    label: String,
    date: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(label, fontSize = 12.sp)
            }
            Text(
                text = formatHotelDate(date),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HotelDatePickerDialog(
    initialDate: LocalDate,
    onDismiss: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val pickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        onDateSelected(
                            Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        )
                    }
                },
                enabled = pickerState.selectedDateMillis != null
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    ) {
        DatePicker(state = pickerState)
    }
}

@Composable
private fun SearchHintCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(Icons.Default.Hotel, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                text = "Choose a city and dates, then search to load hotel and room data from the remote API.",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            )
        }
    }
}

@Composable
private fun LoadingHotelsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 3.dp)
            Text("Loading available hotels...")
        }
    }
}

@Composable
private fun ErrorHotelsCard(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
private fun HotelsResultList(
    hotels: List<HotelDto>,
    startDate: String,
    endDate: String,
    onHotelSelected: (HotelDto, String, String) -> Unit
) {
    if (hotels.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text(
                text = "No available hotels were returned for this search.",
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
            )
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Available hotels (${hotels.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            hotels.forEach { hotel ->
                HotelResultCard(
                    hotel = hotel,
                    onOpenDetails = { onHotelSelected(hotel, startDate, endDate) }
                )
            }
        }
    }
}

@Composable
private fun HotelResultCard(
    hotel: HotelDto,
    onOpenDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            HotelImage(urls = hotel.allImages, description = hotel.name)

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = hotel.name.ifBlank { "Hotel" },
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = hotel.address.ifBlank { hotel.city.ifBlank { "Address not returned" } },
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.62f)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(formatRating(hotel.rating), fontWeight = FontWeight.SemiBold)
                    }
                }

                Text(
                    text = "Rooms returned by API (${hotel.rooms.size})",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (hotel.rooms.isEmpty()) {
                    Text(
                        text = "No rooms were returned for this hotel.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        hotel.rooms.forEach { room -> RoomResultCard(room) }
                    }
                }

                Button(
                    onClick = onOpenDetails,
                    enabled = hotel.rooms.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("View rooms and book")
                }

                Spacer(modifier = Modifier.height(6.dp))
            }
        }
    }
}

@Composable
private fun HotelImage(urls: List<String>, description: String) {
    if (urls.isNotEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AsyncImage(
                model = urls.first(),
                contentDescription = description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            if (urls.size > 1) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    urls.drop(1).take(3).forEach { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = description,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .height(70.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Hotel, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(42.dp))
        }
    }
}

@Composable
private fun RoomResultCard(room: RoomDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = room.roomType.ifBlank { "Room ${room.id}" },
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Room ID: ${room.id.ifBlank { "not returned" }}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.58f)
                    )
                }
                Text(
                    text = formatHotelPrice(room.price),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (room.allImages.isNotEmpty()) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    room.allImages.take(3).forEach { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = room.roomType,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .height(76.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                }
            }
        }
    }
}

private fun formatHotelDate(date: LocalDate): String =
    date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

private fun formatHotelPrice(price: Double): String =
    if (price <= 0.0) "Price N/A" else String.format(Locale.getDefault(), "€%.2f", price)

private fun formatRating(rating: Double): String =
    if (rating <= 0.0) "N/A" else String.format(Locale.getDefault(), "%.1f", rating)
