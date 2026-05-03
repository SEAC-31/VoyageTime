package com.example.voyagetime.data.repository

import android.util.Log
import com.example.voyagetime.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) : AuthRepository {

    override fun isUserLoggedIn(): Boolean {
        val loggedIn = firebaseAuth.currentUser != null
        Log.i(TAG, "Auth state checked. loggedIn=$loggedIn")
        return loggedIn
    }

    override fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
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
                if (task.isSuccessful) {
                    Log.i(TAG, "Login successful for email=$cleanEmail")
                    onSuccess()
                } else {
                    val message = task.exception?.localizedMessage
                        ?: "Firebase login failed."

                    Log.e(TAG, "Login failed for email=$cleanEmail", task.exception)
                    onError(message)
                }
            }
    }

    override fun logout() {
        val email = firebaseAuth.currentUser?.email ?: "unknown"
        firebaseAuth.signOut()
        Log.i(TAG, "Logout completed for email=$email")
    }

    companion object {
        private const val TAG = "FirebaseAuthRepository"
    }
}