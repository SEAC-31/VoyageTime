package com.example.voyagetime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "itinerary_items",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["trip_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["trip_id"])]
)
data class ItineraryItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "trip_id")
    val tripId: Long,

    @ColumnInfo(name = "day_number")
    val dayNumber: Int,

    @ColumnInfo(name = "section")
    val section: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "location")
    val location: String,

    @ColumnInfo(name = "cost_amount")
    val costAmount: Int,

    @ColumnInfo(name = "scheduled_at")
    val scheduledAt: LocalDateTime,

    @ColumnInfo(name = "notes")
    val notes: String? = null
)
