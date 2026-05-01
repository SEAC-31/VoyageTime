package com.example.voyagetime.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,         // registro OK, email de verificación enviado
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val genericError: String? = null
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(email: String, password: String, confirmPassword: String) {
        // Validación local primero
        val emailErr    = validateEmail(email)
        val passErr     = validatePassword(password)
        val confirmErr  = if (password != confirmPassword) "Passwords do not match" else null

        if (emailErr != null || passErr != null || confirmErr != null) {
            _uiState.value = RegisterUiState(
                emailError           = emailErr,
                passwordError        = passErr,
                confirmPasswordError = confirmErr
            )
            return
        }

        _uiState.value = RegisterUiState(isLoading = true)

        viewModelScope.launch {
            val registerResult = authRepository.register(email.trim(), password)

            registerResult.fold(
                onSuccess = { userId ->
                    Log.i(TAG, "User registered: $userId")

                    // Enviamos email de verificación inmediatamente tras el registro
                    val verificationResult = authRepository.sendEmailVerification()
                    verificationResult.fold(
                        onSuccess = {
                            Log.i(TAG, "Verification email sent to $email")
                            _uiState.value = RegisterUiState(isSuccess = true)
                        },
                        onFailure = { error ->
                            // El usuario se creó pero el email de verificación falló.
                            // Lo marcamos como éxito igualmente; puede reenviar después.
                            Log.w(TAG, "Verification email failed: ${error.message}")
                            _uiState.value = RegisterUiState(isSuccess = true)
                        }
                    )
                },
                onFailure = { error ->
                    Log.e(TAG, "Register failed: ${error.message}")
                    val isEmailConflict = error.message?.contains("already in use", ignoreCase = true) == true
                    _uiState.value = RegisterUiState(
                        emailError   = if (isEmailConflict) "This email is already registered" else null,
                        genericError = if (!isEmailConflict) error.message else null
                    )
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState()
    }

    // ── Validadores ───────────────────────────────────────────────────────────

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Email is required"
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches())
            return "Enter a valid email address"
        return null
    }

    private fun validatePassword(password: String): String? {
        if (password.length < 6) return "Password must be at least 6 characters"
        return null
    }

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}