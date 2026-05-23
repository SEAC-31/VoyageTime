package com.example.voyagetime.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.data.local.dao.ReservationDao
import com.example.voyagetime.data.local.entity.ReservationEntity
import com.example.voyagetime.data.remote.HotelApiService
import com.example.voyagetime.di.NetworkModule
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ReservationsState {
    object Loading : ReservationsState()
    data class Success(val reservations: List<ReservationEntity>) : ReservationsState()
    data class Error(val message: String) : ReservationsState()
}

@HiltViewModel
class ReservationsViewModel @Inject constructor(
    private val reservationDao: ReservationDao,
    private val apiService: HotelApiService
) : ViewModel() {

    private val _state = MutableStateFlow<ReservationsState>(ReservationsState.Loading)
    val state: StateFlow<ReservationsState> = _state.asStateFlow()

    private val _deleteResult = MutableStateFlow<String?>(null)
    val deleteResult: StateFlow<String?> = _deleteResult.asStateFlow()

    init {
        loadReservations()
    }

    // T4.1 — Cargar todas las reservas del usuario logueado
    fun loadReservations() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            reservationDao.getAllReservations(userId).collect { list ->
                Log.d(TAG, "loadReservations: ${list.size} reservations")
                _state.value = ReservationsState.Success(list)
            }
        }
    }

    // T4.2 — Eliminar reserva local + cancelar vía API
    fun deleteReservation(reservation: ReservationEntity) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                // 1. Cancelar vía API si tiene ID válido
                if (reservation.apiReservationId != "UNKNOWN") {
                    val response = apiService.cancelReservationById(reservation.apiReservationId)
                    if (response.isSuccessful) {
                        Log.i(TAG, "deleteReservation: cancelled on API ${reservation.apiReservationId}")
                    } else {
                        Log.w(TAG, "deleteReservation: API returned ${response.code()}, deleting locally anyway")
                    }
                }
                // 2. Borrar localmente
                reservationDao.deleteReservation(reservation.id, userId)
                Log.i(TAG, "deleteReservation: deleted locally id=${reservation.id}")
                _deleteResult.value = "Reservation cancelled"
            } catch (e: Exception) {
                Log.e(TAG, "deleteReservation: exception", e)
                // Borramos igualmente en local aunque falle la API
                reservationDao.deleteReservation(reservation.id, userId)
                _deleteResult.value = "Cancelled locally (API error)"
            }
        }
    }

    fun clearDeleteResult() { _deleteResult.value = null }

    companion object { private const val TAG = "ReservationsViewModel" }
}