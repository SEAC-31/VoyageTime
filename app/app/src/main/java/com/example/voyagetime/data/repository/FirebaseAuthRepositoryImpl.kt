package com.example.voyagetime.data.repository

import android.util.Log
import com.example.voyagetime.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override fun isUserLoggedIn(): Boolean {
        val user = firebaseAuth.currentUser
        val loggedIn = user != null && user.isEmailVerified

        if (user != null && !user.isEmailVerified) {
            Log.w(TAG, "Auth state checked: user is signed in but email is not verified. uid=${user.uid}")
            firebaseAuth.signOut()
        } else {
            Log.i(TAG, "Auth state checked. loggedIn=$loggedIn uid=${user?.uid}")
        }

        return loggedIn
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    override fun currentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun isEmailVerified(): Boolean {
        val verified = firebaseAuth.currentUser?.isEmailVerified ?: false
        Log.i(TAG, "Email verification checked. verified=$verified uid=${firebaseAuth.currentUser?.uid}")
        return verified
    }

    override fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val cleanEmail = email.trim()
        Log.i(TAG, "Login requested for email=$cleanEmail")

        firebaseAuth.signInWithEmailAndPassword(cleanEmail, password)
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    val message = task.exception?.localizedMessage ?: "Firebase login failed."
                    Log.e(TAG, "Login failed for email=$cleanEmail", task.exception)
                    onError(message)
                    return@addOnCompleteListener
                }

                val user = firebaseAuth.currentUser
                if (user == null) {
                    Log.e(TAG, "Login failed for email=$cleanEmail because Firebase currentUser is null")
                    onError("User ID not found")
                    return@addOnCompleteListener
                }

                user.reload()
                    .addOnCompleteListener { reloadTask ->
                        if (!reloadTask.isSuccessful) {
                            Log.e(TAG, "Login verification reload failed for email=$cleanEmail", reloadTask.exception)
                            firebaseAuth.signOut()
                            onError(reloadTask.exception?.localizedMessage ?: "Email verification check failed.")
                            return@addOnCompleteListener
                        }

                        val refreshedUser = firebaseAuth.currentUser
                        if (refreshedUser?.isEmailVerified == true) {
                            Log.i(TAG, "Login successful for email=$cleanEmail uid=${refreshedUser.uid} emailVerified=true")
                            onSuccess()
                        } else {
                            Log.w(TAG, "Login blocked because email is not verified for email=$cleanEmail uid=${refreshedUser?.uid}")
                            firebaseAuth.signOut()
                            onError(AuthRepository.ERROR_EMAIL_NOT_VERIFIED)
                        }
                    }
            }
    }

    override suspend fun login(email: String, password: String): Result<String> {
        val cleanEmail = email.trim()
        return try {
            Log.i(TAG, "login: requested email=$cleanEmail")
            val result = firebaseAuth.signInWithEmailAndPassword(cleanEmail, password).await()
            val user = result.user ?: return Result.failure(Exception("User ID not found"))

            user.reload().await()
            val refreshedUser = firebaseAuth.currentUser ?: return Result.failure(Exception("User ID not found"))

            if (!refreshedUser.isEmailVerified) {
                Log.w(TAG, "login: blocked because email is not verified uid=${refreshedUser.uid}")
                firebaseAuth.signOut()
                return Result.failure(Exception(AuthRepository.ERROR_EMAIL_NOT_VERIFIED))
            }

            Log.i(TAG, "login: success uid=${refreshedUser.uid} emailVerified=true")
            Result.success(refreshedUser.uid)
        } catch (error: Exception) {
            Log.e(TAG, "login: failed ${error.message}", error)
            Result.failure(error)
        }
    }

    override suspend fun register(email: String, password: String): Result<String> {
        val cleanEmail = email.trim()
        return try {
            Log.i(TAG, "register: requested email=$cleanEmail")
            val result = firebaseAuth.createUserWithEmailAndPassword(cleanEmail, password).await()
            val uid = result.user?.uid ?: return Result.failure(Exception("User ID not found"))
            Log.i(TAG, "register: success uid=$uid")
            Result.success(uid)
        } catch (error: Exception) {
            Log.e(TAG, "register: failed ${error.message}", error)
            Result.failure(error)
        }
    }

    override suspend fun sendEmailVerification(): Result<Unit> {
        return try {
            val user = firebaseAuth.currentUser ?: return Result.failure(Exception("No authenticated user"))
            user.sendEmailVerification().await()
            Log.i(TAG, "sendEmailVerification: sent to ${user.email} uid=${user.uid}")
            Result.success(Unit)
        } catch (error: Exception) {
            Log.e(TAG, "sendEmailVerification: failed ${error.message}", error)
            Result.failure(error)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        val cleanEmail = email.trim()
        return try {
            Log.i(TAG, "sendPasswordResetEmail: requested email=$cleanEmail")
            firebaseAuth.sendPasswordResetEmail(cleanEmail).await()
            Log.i(TAG, "sendPasswordResetEmail: sent to $cleanEmail")
            Result.success(Unit)
        } catch (error: Exception) {
            Log.e(TAG, "sendPasswordResetEmail: failed ${error.message}", error)
            Result.failure(error)
        }
    }

    override suspend fun logout() {
        val email = firebaseAuth.currentUser?.email ?: "unknown"
        val uid = firebaseAuth.currentUser?.uid ?: "unknown"
        firebaseAuth.signOut()
        Log.i(TAG, "logout: completed email=$email uid=$uid")
    }

    companion object {
        private const val TAG = "FirebaseAuthRepository"
    }
}
