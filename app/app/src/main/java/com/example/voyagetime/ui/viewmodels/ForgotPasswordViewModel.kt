package com.example.voyagetime.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val emailError: String? = null,
    val genericError: String? = null
)

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun sendResetEmail(email: String) {
        val emailErr = if (email.isBlank()) "Email is required" else null
        if (emailErr != null) {
            _uiState.value = ForgotPasswordUiState(emailError = emailErr)
            return
        }

        _uiState.value = ForgotPasswordUiState(isLoading = true)

        viewModelScope.launch {
            authRepository.sendPasswordResetEmail(email.trim()).fold(
                onSuccess = {
                    Log.i(TAG, "Password reset email sent to $email")
                    _uiState.value = ForgotPasswordUiState(isSuccess = true)
                },
                onFailure = { error ->
                    Log.e(TAG, "Password reset failed: ${error.message}")
                    _uiState.value = ForgotPasswordUiState(genericError = error.message)
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = ForgotPasswordUiState()
    }

    companion object {
        private const val TAG = "ForgotPasswordViewModel"
    }
}