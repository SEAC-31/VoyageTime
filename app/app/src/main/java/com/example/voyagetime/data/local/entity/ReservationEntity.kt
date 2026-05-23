package com.example.voyagetime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * T2.3 — Guarda la info de una reserva de hotel vinculada a un viaje (trip_id)
 * y al usuario logueado (user_id).
 */
@Entity(
    tableName = "reservations",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["firebase_uid"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["trip_id"]),
        Index(value = ["user_id"])
    ]
)
data class ReservationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // ID de reserva devuelto por la API (6 letras mayúsculas, ej: "ABCDEF")
    @ColumnInfo(name = "api_reservation_id")
    val apiReservationId: String,

    @ColumnInfo(name = "trip_id")
    val tripId: Long,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "hotel_id")
    val hotelId: String,

    @ColumnInfo(name = "hotel_name")
    val hotelName: String,

    @ColumnInfo(name = "hotel_image_url")
    val hotelImageUrl: String,

    @ColumnInfo(name = "room_id")
    val roomId: String,

    @ColumnInfo(name = "room_type")
    val roomType: String,

    @ColumnInfo(name = "room_price")
    val roomPrice: Double,

    // Lista de URLs de imágenes de la habitación, guardada como JSON string
    @ColumnInfo(name = "room_images_json")
    val roomImagesJson: String,

    @ColumnInfo(name = "start_date")
    val startDate: String,   // "2025-06-01"

    @ColumnInfo(name = "end_date")
    val endDate: String,     // "2025-06-05"

    @ColumnInfo(name = "guest_name")
    val guestName: String,

    @ColumnInfo(name = "guest_email")
    val guestEmail: String,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)