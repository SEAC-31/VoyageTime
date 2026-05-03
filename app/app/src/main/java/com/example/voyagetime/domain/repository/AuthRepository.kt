package com.example.voyagetime.domain.repository

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserEmail(): String?
    fun currentUserId(): String?
    fun isEmailVerified(): Boolean

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(email: String, password: String): Result<String>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun logout()

    companion object {
        const val ERROR_EMAIL_NOT_VERIFIED = "ERROR_EMAIL_NOT_VERIFIED"
    }
}
