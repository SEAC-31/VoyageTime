package com.example.voyagetime.domain.repository

import com.example.voyagetime.data.local.entity.UserEntity

interface UserRepository {

    /** Crea el usuario local tras el registro en Firebase. */
    suspend fun createUser(user: UserEntity)

    /** Actualiza los datos de perfil del usuario. */
    suspend fun updateUser(user: UserEntity)

    /** Obtiene el usuario por su Firebase UID. */
    suspend fun getUserById(uid: String): UserEntity?

    /**
     * Comprueba si el username ya está en uso por otro usuario.
     * [excludeUid] permite excluir al propio usuario al editar su perfil.
     */
    suspend fun isUsernameTaken(username: String, excludeUid: String = ""): Boolean

    /** Registra un evento LOGIN o LOGOUT en el log de accesos (T4.4). */
    suspend fun logAccess(userId: String, eventType: String)
}