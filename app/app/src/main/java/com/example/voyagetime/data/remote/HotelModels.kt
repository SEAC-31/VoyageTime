package com.example.voyagetime.data.remote

import com.google.gson.annotations.SerializedName

// ── Respuestas de la API ───────────────────────────────────────────────────

data class HotelDto(
    @SerializedName(value = "id", alternate = ["hotel_id"])
    val id: String = "",

    @SerializedName(value = "name", alternate = ["hotel_name"])
    val name: String = "",

    @SerializedName(value = "address", alternate = ["location"])
    val address: String = "",

    @SerializedName(value = "city")
    val city: String = "",

    @SerializedName(value = "rating", alternate = ["stars"])
    val rating: Double = 0.0,

    @SerializedName(value = "rooms")
    val rooms: List<RoomDto> = emptyList(),

    @SerializedName(value = "images")
    val images: List<String> = emptyList(),

    @SerializedName(value = "image_url", alternate = ["imageUrl", "image"])
    val imageUrl: String = ""
) {
    val allImages: List<String>
        get() = (images + imageUrl).filter { it.isNotBlank() }.distinct()
}

data class RoomDto(
    @SerializedName(value = "id", alternate = ["room_id"])
    val id: String = "",

    @SerializedName(value = "room_type", alternate = ["roomType", "type", "name"])
    val roomType: String = "",

    @SerializedName(value = "price", alternate = ["price_per_night"])
    val price: Double = 0.0,

    @SerializedName(value = "images")
    val images: List<String> = emptyList(),

    @SerializedName(value = "image_url", alternate = ["imageUrl", "image"])
    val imageUrl: String = ""
) {
    val allImages: List<String>
        get() = (images + imageUrl).filter { it.isNotBlank() }
}

// ── Request body para reservar y cancelar ─────────────────────────────────

data class ReserveRequest(
    @SerializedName("hotel_id")     val hotelId: String,
    @SerializedName("room_id")      val roomId: String,
    @SerializedName("start_date")   val startDate: String,   // formato: "2025-06-01"
    @SerializedName("end_date")     val endDate: String,     // formato: "2025-06-05"
    @SerializedName("guest_name")   val guestName: String,
    @SerializedName("guest_email")  val guestEmail: String
)
