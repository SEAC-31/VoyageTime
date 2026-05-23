package com.example.voyagetime.domain.repository

/**
 * Interfaz de autenticación. La implementación real usará Firebase Auth (T2.x, Sharon).
 * La implementación fake [FakeAuthRepositoryImpl] permite trabajar sin Firebase conectado.
 */
interface AuthRepository {

    /**
     * Registra un nuevo usuario con email y contraseña.
     * Devuelve el userId generado en caso de éxito.
     */
    suspend fun register(email: String, password: String): Result<String>

    /**
     * Envía un email de verificación al usuario actualmente autenticado.
     * Debe llamarse justo después de [register].
     */
    suspend fun sendEmailVerification(): Result<Unit>

    /**
     * Envía un email de recuperación de contraseña.
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Inicia sesión con email y contraseña.
     * Devuelve el userId en caso de éxito.
     */
    suspend fun login(email: String, password: String): Result<String>

    /**
     * Cierra la sesión del usuario actual.
     */
    suspend fun logout()

    /**
     * Devuelve el userId del usuario actualmente autenticado, o null si no hay sesión.
     */
    fun currentUserId(): String?

    /**
     * Devuelve true si el email del usuario actual está verificado.
     */
    fun isEmailVerified(): Boolean
}