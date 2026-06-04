package com.example.voyagetime.domain.repository

import com.example.voyagetime.data.remote.HotelDto
import com.example.voyagetime.data.remote.ReserveRequest
import com.google.gson.JsonElement

interface HotelRepository {
    suspend fun searchAvailableHotels(
        city: String,
        startDate: String,
        endDate: String
    ): Result<List<HotelDto>>

    suspend fun reserveRoom(request: ReserveRequest): Result<JsonElement?>

    suspend fun cancelReservation(request: ReserveRequest): Result<Unit>

    suspend fun cancelReservationById(reservationId: String): Result<Unit>
}
