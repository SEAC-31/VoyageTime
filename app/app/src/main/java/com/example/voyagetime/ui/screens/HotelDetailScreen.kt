package com.example.voyagetime.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.voyagetime.data.remote.HotelDto
import com.example.voyagetime.data.remote.RoomDto
import com.example.voyagetime.ui.viewmodels.HotelBookingState
import com.example.voyagetime.ui.viewmodels.HotelBookingViewModel

/**
 * T2.4 — Pantalla de detalle de hotel: muestra imágenes del hotel y habitaciones,
 * y permite al usuario reservar una habitación (T2.3).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailScreen(
    hotel: HotelDto,
    tripId: Long,
    startDate: String,
    endDate: String,
    onBack: () -> Unit,
    onBookingSuccess: () -> Unit,
    viewModel: HotelBookingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedRoomId by remember { mutableStateOf<String?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Reaccionar al estado del ViewModel
    LaunchedEffect(state) {
        if (state is HotelBookingState.BookingSuccess) {
            showSuccessDialog = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(hotel.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Imágenes del hotel ──────────────────────────────────────
            val hotelImages = hotel.allImages
            if (hotelImages.isNotEmpty()) {
                AsyncImage(
                    model = hotelImages.first(),
                    contentDescription = hotel.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                )

                if (hotelImages.size > 1) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        items(hotelImages.drop(1)) { imageUrl ->
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = hotel.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(120.dp, 80.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Hotel,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                // ── Info del hotel ────────────────────────────────────────
                Text(
                    text = hotel.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(hotel.address, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(hotel.rating.toInt()) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Hotel, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "$startDate → $endDate",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                // ── Habitaciones ──────────────────────────────────────────
                Text(
                    text = "Available Rooms",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))

                hotel.rooms.forEach { room ->
                    RoomCard(
                        room = room,
                        isSelected = selectedRoomId == room.id,
                        onSelect = { selectedRoomId = room.id }
                    )
                    Spacer(Modifier.height(12.dp))
                }

                // ── Botón reservar ────────────────────────────────────────
                val isLoading = state is HotelBookingState.Loading
                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = selectedRoomId != null && !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Book Selected Room")
                    }
                }

                // Error
                if (state is HotelBookingState.Error) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = (state as HotelBookingState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    // ── Diálogo confirmación ──────────────────────────────────────────────
    if (showConfirmDialog && selectedRoomId != null) {
        val room = hotel.rooms.find { it.id == selectedRoomId }
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Booking") },
            text = {
                Text("Book ${room?.roomType} at ${hotel.name} for €${room?.price}/night?")
            },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    viewModel.bookRoom(
                        tripId    = tripId,
                        hotel     = hotel,
                        roomId    = selectedRoomId!!,
                        startDate = startDate,
                        endDate   = endDate
                    )
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ── Diálogo éxito ─────────────────────────────────────────────────────
    if (showSuccessDialog) {
        val resId = (state as? HotelBookingState.BookingSuccess)?.reservationId ?: ""
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Booking Confirmed!") },
            text = { Text("Your reservation ID is: $resId\nSaved to your trip.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    viewModel.resetState()
                    onBookingSuccess()
                }) { Text("OK") }
            }
        )
    }
}

// ── Componente tarjeta de habitación ─────────────────────────────────────────

@Composable
private fun RoomCard(
    room: RoomDto,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        shape = RoundedCornerShape(12.dp),
        border = if (isSelected)
            androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // Galería de imágenes de la habitación (T2.4)
            val roomImages = room.allImages
            if (roomImages.isNotEmpty()) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(roomImages) { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = room.roomType,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp, 80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surface)
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = room.roomType,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "€${"%.2f".format(room.price)}/night",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isSelected) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "✓ Selected",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}