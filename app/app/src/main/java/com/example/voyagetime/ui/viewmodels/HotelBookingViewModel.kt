package com.example.voyagetime.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.data.local.dao.ReservationDao
import com.example.voyagetime.data.local.entity.ReservationEntity
import com.example.voyagetime.data.remote.HotelApiService
import com.example.voyagetime.data.remote.HotelDto
import com.example.voyagetime.data.remote.ReserveRequest
import com.example.voyagetime.di.NetworkModule
import com.example.voyagetime.domain.repository.HotelRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
    private val apiService: HotelApiService,
    private val hotelRepository: HotelRepository,
    private val reservationDao: ReservationDao
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
        if (currentUser == null) {
            _state.value = HotelBookingState.Error("User not logged in")
            return
        }

        val room = hotel.rooms.find { it.id == roomId }
        if (room == null) {
            _state.value = HotelBookingState.Error("Room not found")
            return
        }

        Log.d(TAG, "bookRoom: hotel=${hotel.id}, room=${room.id}, trip=$tripId")
        _state.value = HotelBookingState.Loading

        viewModelScope.launch {
            try {
                val request = ReserveRequest(
                    hotelId = hotel.id,
                    roomId = room.id,
                    startDate = startDate,
                    endDate = endDate,
                    guestName = currentUser.displayName ?: currentUser.email ?: "Guest",
                    guestEmail = currentUser.email ?: ""
                )
                val response = apiService.reserveRoom(NetworkModule.GROUP_ID, request)

                if (!response.isSuccessful) {
                    Log.e(TAG, "bookRoom: API error ${response.code()}")
                    _state.value = HotelBookingState.Error("Booking failed: ${response.code()}")
                    return@launch
                }

                val apiResId = extractReservationId(response.body())
                Log.i(TAG, "bookRoom: reservation created with id=$apiResId")

                val entity = ReservationEntity(
                    apiReservationId = apiResId,
                    tripId = tripId,
                    userId = currentUser.uid,
                    hotelId = hotel.id,
                    hotelName = hotel.name,
                    hotelImageUrl = hotel.imageUrl,
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
    }
}
