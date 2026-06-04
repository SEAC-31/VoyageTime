package com.example.voyagetime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * T3.1/T3.2 — Image attached to a concrete trip.
 * The file itself remains in Android storage through a persistable URI permission,
 * and the URI is stored locally in Room linked to trip_id and user_id.
 */
@Entity(
    tableName = "trip_images",
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
        Index(value = ["user_id"]),
        Index(value = ["trip_id", "image_uri"], unique = true)
    ]
)
data class TripImageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "trip_id")
    val tripId: Long,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "image_uri")
    val imageUri: String,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)
