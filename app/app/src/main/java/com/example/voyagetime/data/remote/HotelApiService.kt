package com.example.voyagetime.data.remote

import com.google.gson.JsonElement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HotelApiService {

    // T2.2 — Lista hoteles del grupo (contiene rooms con imágenes)
    @GET("hotels/{group_id}/hotels")
    suspend fun listHotels(
        @Path("group_id") groupId: String
    ): Response<List<HotelDto>>

    // T2.1 — Busca disponibilidad por ciudad y fechas.
    // Se usa JsonElement para soportar tanto arrays directos como respuestas envueltas.
    @GET("hotels/{group_id}/availability")
    suspend fun checkAvailability(
        @Path("group_id")    groupId: String,
        @Query("start_date") startDate: String,
        @Query("end_date")   endDate: String,
        @Query("city")       city: String? = null,
        @Query("hotel_id")   hotelId: String? = null
    ): Response<JsonElement>

    // T2.3 — Hacer una reserva
    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequest
    ): Response<JsonElement>

    // T4.1 — Listar reservas del grupo (opcional: filtrar por email)
    @GET("hotels/{group_id}/reservations")
    suspend fun listReservations(
        @Path("group_id") groupId: String,
        @Query("guest_email") guestEmail: String? = null
    ): Response<JsonElement>

    // T4.2 — Cancelar reserva via API
    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservation(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequest
    ): Response<JsonElement>

    // Extra — Obtener reserva por ID (6 letras mayúsculas, ej: "ABCDEF")
    @GET("reservations/{res_id}")
    suspend fun getReservationById(
        @Path("res_id") resId: String
    ): Response<JsonElement>

    // Extra — Cancelar reserva por ID
    @DELETE("reservations/{res_id}")
    suspend fun cancelReservationById(
        @Path("res_id") resId: String
    ): Response<JsonElement>
}
