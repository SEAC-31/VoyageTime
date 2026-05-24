package com.example.voyagetime.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.data.local.dao.ReservationDao
import com.example.voyagetime.data.local.dao.TripDao
import com.example.voyagetime.data.local.dao.UserDao
import com.example.voyagetime.data.local.entity.TripEntity
import com.example.voyagetime.data.local.entity.ReservationEntity
import com.example.voyagetime.data.local.entity.UserEntity
import com.example.voyagetime.data.remote.HotelDto
import com.example.voyagetime.data.remote.ReserveRequest
import com.example.voyagetime.domain.repository.HotelRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject

// ── UI State ──────────────────────────────────────────────────────────────

sealed class HotelBookingState {
    object Idle : HotelBookingState()
    object Loading : HotelBookingState()
    data class HotelsLoaded(val hotels: List<HotelDto>) : HotelBookingState()
    data class BookingSuccess(val reservationId: String) : HotelBookingState()
    data class Error(val message: String) : HotelBookingState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────

@HiltViewModel
class HotelBookingViewModel @Inject constructor(
    private val hotelRepository: HotelRepository,
    private val reservationDao: ReservationDao,
    private val tripDao: TripDao,
    private val userDao: UserDao
) : ViewModel() {

    private val _state = MutableStateFlow<HotelBookingState>(HotelBookingState.Idle)
    val state: StateFlow<HotelBookingState> = _state.asStateFlow()

    private val gson = Gson()

    // T2.1 — Buscar hoteles disponibles por ciudad y fechas
    fun searchHotels(city: String, startDate: String, endDate: String) {
        Log.d(TAG, "searchHotels: city=$city, start=$startDate, end=$endDate")
        _state.value = HotelBookingState.Loading

        viewModelScope.launch {
            try {
                val result = hotelRepository.searchAvailableHotels(
                    city = city,
                    startDate = startDate,
                    endDate = endDate
                )

                result.fold(
                    onSuccess = { hotels ->
                        Log.i(TAG, "searchHotels: ${hotels.size} hotels found")
                        _state.value = HotelBookingState.HotelsLoaded(hotels)
                    },
                    onFailure = { error ->
                        Log.e(TAG, "searchHotels: repository error", error)
                        _state.value = HotelBookingState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "searchHotels: exception", e)
                _state.value = HotelBookingState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // T2.3 — Reservar habitación y guardar localmente en Room
    fun bookRoom(
        tripId: Long,
        hotel: HotelDto,
        roomId: String,
        startDate: String,
        endDate: String
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: LOCAL_USER_ID
        val userEmail = currentUser?.email ?: "guest@local.voyagetime"
        val guestName = currentUser?.displayName ?: userEmail.substringBefore("@").ifBlank { "Guest" }

        val room = hotel.rooms.find { it.id == roomId }
        if (room == null) {
            _state.value = HotelBookingState.Error("Room not found")
            return
        }

        Log.d(TAG, "bookRoom: hotel=${hotel.id}, room=${room.id}, trip=$tripId")
        _state.value = HotelBookingState.Loading

        viewModelScope.launch {
            try {
                ensureLocalUser(userId, userEmail)

                val request = ReserveRequest(
                    hotelId = hotel.id,
                    roomId = room.id,
                    startDate = startDate,
                    endDate = endDate,
                    guestName = guestName,
                    guestEmail = userEmail
                )
                val bookingResult = hotelRepository.reserveRoom(request)
                if (bookingResult.isFailure) {
                    val error = bookingResult.exceptionOrNull()
                    Log.e(TAG, "bookRoom: repository error", error)
                    _state.value = HotelBookingState.Error(error?.message ?: "Booking failed")
                    return@launch
                }

                val apiResId = extractReservationId(bookingResult.getOrNull())
                Log.i(TAG, "bookRoom: reservation created with id=$apiResId")

                val savedTripId = ensureTripForReservation(
                    requestedTripId = tripId,
                    hotel = hotel,
                    roomPrice = room.price,
                    startDate = startDate,
                    endDate = endDate,
                    userId = userId
                )

                val entity = ReservationEntity(
                    apiReservationId = apiResId,
                    tripId = savedTripId,
                    userId = userId,
                    hotelId = hotel.id,
                    hotelName = hotel.name,
                    hotelImageUrl = hotel.allImages.firstOrNull().orEmpty(),
                    roomId = room.id,
                    roomType = room.roomType,
                    roomPrice = room.price,
                    roomImagesJson = gson.toJson(room.allImages),
                    startDate = startDate,
                    endDate = endDate,
                    guestName = request.guestName,
                    guestEmail = request.guestEmail
                )
                reservationDao.insertReservation(entity)
                Log.i(TAG, "bookRoom: reservation saved locally")

                _state.value = HotelBookingState.BookingSuccess(apiResId)
            } catch (e: Exception) {
                Log.e(TAG, "bookRoom: exception", e)
                _state.value = HotelBookingState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun resetState() {
        _state.value = HotelBookingState.Idle
    }



    private suspend fun ensureLocalUser(uid: String, email: String?) {
        if (userDao.getUserById(uid) != null) return

        userDao.insertUserIfMissing(
            UserEntity(
                firebaseUid = uid,
                username = "user_${uid.take(12)}",
                email = email ?: "$uid@local.voyagetime"
            )
        )
    }

    private suspend fun ensureTripForReservation(
        requestedTripId: Long,
        hotel: HotelDto,
        roomPrice: Double,
        startDate: String,
        endDate: String,
        userId: String
    ): Long {
        if (requestedTripId > 0L) return requestedTripId

        val start = LocalDate.parse(startDate)
        val end = LocalDate.parse(endDate)
        val nights = ChronoUnit.DAYS.between(start, end).coerceAtLeast(1).toInt()
        val budget = (roomPrice * nights).toInt().coerceAtLeast(0)
        val city = hotel.city.ifBlank { hotel.address.substringBefore(",").ifBlank { hotel.name } }

        val trip = TripEntity(
            userId = userId,
            destination = city,
            country = countryForCity(city),
            startDateTime = start.atStartOfDay(),
            endDateTime = end.atStartOfDay(),
            durationDays = nights,
            budgetAmount = budget,
            statusLabel = "PLANNED",
            imageRes = imageForCity(city)
        )
        return tripDao.insertTrip(trip)
    }

    private fun countryForCity(city: String): String = when (city.trim().lowercase()) {
        "barcelona" -> "Spain"
        "paris" -> "France"
        "london" -> "United Kingdom"
        else -> ""
    }

    private fun imageForCity(city: String): Int = when (city.trim().lowercase()) {
        "barcelona" -> R.drawable.barcelona
        "paris" -> R.drawable.paris
        else -> R.drawable.logo_no_background
    }

    private fun extractReservationId(body: JsonElement?): String {
        return try {
            val obj = body?.asJsonObject ?: return "UNKNOWN"
            obj.get("reservation_id")?.asString
                ?: obj.get("reservationId")?.asString
                ?: obj.get("id")?.asString
                ?: "UNKNOWN"
        } catch (e: Exception) {
            Log.w(TAG, "extractReservationId: could not parse id from $body")
            "UNKNOWN"
        }
    }

    companion object {
        private const val TAG = "HotelBookingViewModel"
        private const val LOCAL_USER_ID = "local_user"
    }
}
