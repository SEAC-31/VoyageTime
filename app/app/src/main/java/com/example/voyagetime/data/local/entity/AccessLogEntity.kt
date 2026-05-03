package com.example.voyagetime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * T4.4 — Registra cada evento de login/logout del usuario.
 * [eventType] puede ser "LOGIN" o "LOGOUT".
 */
@Entity(
    tableName = "access_log",
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
data class AccessLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "event_type")         // "LOGIN" | "LOGOUT"
    val eventType: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: LocalDateTime = LocalDateTime.now()
)