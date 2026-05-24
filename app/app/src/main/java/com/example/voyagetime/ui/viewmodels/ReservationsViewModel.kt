package com.example.voyagetime.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.data.local.dao.ReservationDao
import com.example.voyagetime.data.local.entity.ReservationEntity
import com.example.voyagetime.domain.repository.HotelRepository
import com.example.voyagetime.data.remote.ReserveRequest
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
    private val hotelRepository: HotelRepository
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
        val userId = currentUserId()
        viewModelScope.launch {
            try {
                reservationDao.getAllReservations(userId).collect { list ->
                    Log.d(TAG, "loadReservations: ${list.size} reservations")
                    _state.value = ReservationsState.Success(list)
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadReservations: exception", e)
                _state.value = ReservationsState.Error(e.message ?: "Could not load reservations")
            }
        }
    }

    // T4.2 — Eliminar reserva local + cancelar vía API
    fun deleteReservation(reservation: ReservationEntity) {
        val userId = currentUserId()
        viewModelScope.launch {
            try {
                // 1. Cancelar vía API siempre a través del repositorio.
                // Usamos primero el endpoint del grupo (/hotels/{group_id}/cancel), que es el
                // que forma parte de la API del Sprint 04. Si ese endpoint fallara y la API
                // devolvió un ID real, intentamos el endpoint por ID como respaldo.
                val cancelRequest = ReserveRequest(
                    hotelId = reservation.hotelId,
                    roomId = reservation.roomId,
                    startDate = reservation.startDate,
                    endDate = reservation.endDate,
                    guestName = reservation.guestName,
                    guestEmail = reservation.guestEmail
                )

                val cancelResult = hotelRepository.cancelReservation(cancelRequest)
                if (cancelResult.isSuccess) {
                    Log.i(TAG, "deleteReservation: cancelled on group API ${reservation.apiReservationId}")
                } else if (reservation.apiReservationId != "UNKNOWN") {
                    hotelRepository.cancelReservationById(reservation.apiReservationId)
                        .onSuccess {
                            Log.i(TAG, "deleteReservation: cancelled on API by id ${reservation.apiReservationId}")
                        }
                        .onFailure { error ->
                            Log.w(TAG, "deleteReservation: API cancel failed, deleting locally anyway", error)
                        }
                } else {
                    Log.w(TAG, "deleteReservation: group cancel failed, deleting locally anyway", cancelResult.exceptionOrNull())
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

    private fun currentUserId(): String = FirebaseAuth.getInstance().currentUser?.uid ?: LOCAL_USER_ID

    companion object {
        private const val TAG = "ReservationsViewModel"
        private const val LOCAL_USER_ID = "local_user"
    }
}