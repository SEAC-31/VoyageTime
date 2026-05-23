package com.example.voyagetime.data.remote

import retrofit2.Response
import retrofit2.http.*

interface HotelApiService {

    // T2.2 — Lista hoteles del grupo (contiene rooms con imágenes)
    @GET("hotels/{group_id}/hotels")
    suspend fun listHotels(
        @Path("group_id") groupId: String
    ): Response<List<HotelDto>>

    // T2.1 — Busca disponibilidad por ciudad y fechas
    @GET("hotels/{group_id}/availability")
    suspend fun checkAvailability(
        @Path("group_id")          groupId: String,
        @Query("start_date")       startDate: String,   // "2025-06-01"
        @Query("end_date")         endDate: String,     // "2025-06-05"
        @Query("city")             city: String? = null,
        @Query("hotel_id")         hotelId: String? = null
    ): Response<Any>

    // T2.3 — Hacer una reserva
    @POST("hotels/{group_id}/reserve")
    suspend fun reserveRoom(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequest
    ): Response<Any>

    // T4.1 — Listar reservas del grupo (opcional: filtrar por email)
    @GET("hotels/{group_id}/reservations")
    suspend fun listReservations(
        @Path("group_id")    groupId: String,
        @Query("guest_email") guestEmail: String? = null
    ): Response<Any>

    // T4.2 — Cancelar reserva via API (por body, mismo endpoint que reserve)
    @POST("hotels/{group_id}/cancel")
    suspend fun cancelReservation(
        @Path("group_id") groupId: String,
        @Body request: ReserveRequest
    ): Response<Any>

    // Extra — Obtener reserva por ID (6 letras mayúsculas, ej: "ABCDEF")
    @GET("reservations/{res_id}")
    suspend fun getReservationById(
        @Path("res_id") resId: String
    ): Response<Any>

    // Extra — Cancelar reserva por ID
    @DELETE("reservations/{res_id}")
    suspend fun cancelReservationById(
        @Path("res_id") resId: String
    ): Response<Any>
}