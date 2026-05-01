package com.example.voyagetime.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Tabla de usuarios locales. El [firebaseUid] es el userId que devuelve Firebase Auth
 * y actúa como clave de negocio — la PrimaryKey es String para evitar autogenerar
 * un id separado del de Firebase.
 *
 * [username] debe ser único (ver índice). Se valida en [UserDao.isUsernameTaken].
 */
@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["firebase_uid"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "firebase_uid")
    val firebaseUid: String,                    // userId de Firebase Auth

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "birthdate")
    val birthdate: LocalDate? = null,

    @ColumnInfo(name = "address")
    val address: String = "",

    @ColumnInfo(name = "country")
    val country: String = "",

    @ColumnInfo(name = "phone")
    val phone: String = "",

    @ColumnInfo(name = "accept_emails")
    val acceptEmails: Boolean = false,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)