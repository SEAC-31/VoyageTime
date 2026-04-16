package com.example.voyagetime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

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
