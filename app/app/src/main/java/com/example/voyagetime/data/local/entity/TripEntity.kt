package com.example.voyagetime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * T4.2 — Se añade [userId] (FK a users.firebase_uid) para vincular cada viaje
 * al usuario que lo creó. Los viajes se filtran por usuario logueado en los DAOs.
 *
 * Migración: versión 2 de la BD. El campo [userId] admite null temporalmente
 * para no romper datos existentes durante el desarrollo.
 */
@Entity(
    tableName = "trips",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["firebase_uid"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["user_id"])]
)
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String? = null,             // null -> firebase data

    @ColumnInfo(name = "destination")
    val destination: String,

    @ColumnInfo(name = "country")
    val country: String,

    @ColumnInfo(name = "start_datetime")
    val startDateTime: LocalDateTime,

    @ColumnInfo(name = "end_datetime")
    val endDateTime: LocalDateTime,

    @ColumnInfo(name = "duration_days")
    val durationDays: Int,

    @ColumnInfo(name = "budget_amount")
    val budgetAmount: Int,

    @ColumnInfo(name = "status_label")
    val statusLabel: String,

    @ColumnInfo(name = "image_res")
    val imageRes: Int,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)