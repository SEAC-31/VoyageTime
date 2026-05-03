package com.example.voyagetime.domain.repository

interface AuthRepository {
    fun isUserLoggedIn(): Boolean
    fun getCurrentUserEmail(): String?

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun logout()
}
