package com.example.voyagetime.data.remote

import com.google.gson.annotations.SerializedName

// ── Respuestas de la API ───────────────────────────────────────────────────

data class HotelDto(
    @SerializedName("id")         val id: String,
    @SerializedName("name")       val name: String,
    @SerializedName("address")    val address: String,
    @SerializedName("rating")     val rating: Int,
    @SerializedName("rooms")      val rooms: List<RoomDto>,
    @SerializedName("image_url")  val imageUrl: String
)

data class RoomDto(
    @SerializedName("id")         val id: String,
    @SerializedName("room_type")  val roomType: String,
    @SerializedName("price")      val price: Double,
    @SerializedName("images")     val images: List<String>
)

// ── Request body para reservar y cancelar ─────────────────────────────────

data class ReserveRequest(
    @SerializedName("hotel_id")     val hotelId: String,
    @SerializedName("room_id")      val roomId: String,
    @SerializedName("start_date")   val startDate: String,   // formato: "2025-06-01"
    @SerializedName("end_date")     val endDate: String,     // formato: "2025-06-05"
    @SerializedName("guest_name")   val guestName: String,
    @SerializedName("guest_email")  val guestEmail: String
)