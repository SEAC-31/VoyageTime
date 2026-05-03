package com.example.voyagetime.ui.viewmodels

import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.data.repository.FirebaseAuthRepositoryImpl
import com.example.voyagetime.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val FORGOT_VM_TAG = "ForgotPasswordViewModel"

data class ForgotPasswordUiState(
    val email: String = "",
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val infoMessageRes: Int? = null,
    @StringRes val generalErrorRes: Int? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                emailErrorRes = null,
                generalErrorRes = null,
                infoMessageRes = null
            )
        }
    }

    fun sendPasswordReset() {
        val current = _uiState.value
        val emailError = validateEmail(current.email)

        if (emailError != null) {
            Log.w(FORGOT_VM_TAG, "Recover password validation failed")
            _uiState.update { it.copy(emailErrorRes = emailError) }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    emailErrorRes = null,
                    generalErrorRes = null,
                    infoMessageRes = null
                )
            }

            authRepository.sendPasswordResetEmail(current.email).fold(
                onSuccess = {
                    Log.i(FORGOT_VM_TAG, "Password reset email sent")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            infoMessageRes = R.string.forgot_password_success
                        )
                    }
                },
                onFailure = { error ->
                    Log.e(FORGOT_VM_TAG, "Password reset failed", error)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            generalErrorRes = R.string.forgot_password_error_generic
                        )
                    }
                }
            )
        }
    }

    private fun validateEmail(value: String): Int? {
        val cleanValue = value.trim()
        return when {
            cleanValue.isBlank() -> R.string.login_error_email_required
            !Patterns.EMAIL_ADDRESS.matcher(cleanValue).matches() -> R.string.login_error_email_invalid
            else -> null
        }
    }
}
