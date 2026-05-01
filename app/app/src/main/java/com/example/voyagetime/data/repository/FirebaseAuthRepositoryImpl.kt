package com.example.voyagetime.data.repository

import android.util.Log
import com.example.voyagetime.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

/**
 * Implementación real de [AuthRepository] usando Firebase Authentication.
 * Requiere que el proyecto tenga google-services.json y las dependencias de Firebase (T2.1).
 */
class FirebaseAuthRepositoryImpl : AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override suspend fun register(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email.trim(), password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("User ID not found"))
            Log.i(TAG, "register: success — uid=$userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "register: failed — ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
                ?: return Result.failure(Exception("No authenticated user"))
            Log.i(TAG, "sendEmailVerification: sent to ${auth.currentUser?.email}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "sendEmailVerification: failed — ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email.trim()).await()
            Log.i(TAG, "sendPasswordResetEmail: sent to $email")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "sendPasswordResetEmail: failed — ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email.trim(), password).await()
            val userId = result.user?.uid ?: return Result.failure(Exception("User ID not found"))
            Log.i(TAG, "login: success — uid=$userId")
            Result.success(userId)
        } catch (e: Exception) {
            Log.e(TAG, "login: failed — ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        Log.i(TAG, "logout: uid=${auth.currentUser?.uid}")
        auth.signOut()
    }

    override fun currentUserId(): String? = auth.currentUser?.uid

    override fun isEmailVerified(): Boolean = auth.currentUser?.isEmailVerified ?: false

    companion object {
        private const val TAG = "FirebaseAuthRepository"
    }
}