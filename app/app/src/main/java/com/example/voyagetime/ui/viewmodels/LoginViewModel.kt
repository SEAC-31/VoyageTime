package com.example.voyagetime.ui.viewmodels

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.domain.repository.AuthRepository
import com.example.voyagetime.ui.screens.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOGIN_VM_TAG = "LoginViewModel"

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val passwordErrorRes: Int? = null,
    @StringRes val generalErrorRes: Int? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun prepareLoginForm(context: Context) {
        val rememberLogin = PreferencesManager.getRememberLogin(context)
        val rememberedEmail = if (rememberLogin) PreferencesManager.getRememberedEmail(context) else ""
        Log.i(LOGIN_VM_TAG, "Login form prepared. rememberLogin=$rememberLogin")
        _uiState.value = LoginUiState(email = rememberedEmail, rememberMe = rememberLogin)
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailErrorRes = null, generalErrorRes = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordErrorRes = null, generalErrorRes = null) }
    }

    fun onRememberMeChange(value: Boolean) {
        _uiState.update { it.copy(rememberMe = value, generalErrorRes = null) }
    }

    fun login(context: Context) {
        val current = _uiState.value
        val emailError = validateEmail(current.email)
        val passwordError = validatePassword(current.password)

        if (emailError != null || passwordError != null) {
            _uiState.update { it.copy(emailErrorRes = emailError, passwordErrorRes = passwordError) }
            return
        }

        _uiState.update { it.copy(isLoading = true, emailErrorRes = null, passwordErrorRes = null, generalErrorRes = null) }

        viewModelScope.launch {
            authRepository.login(current.email.trim(), current.password).fold(
                onSuccess = { userId ->
                    Log.i(LOGIN_VM_TAG, "Login success: $userId")

                    if (!authRepository.isEmailVerified()) {
                        Log.w(LOGIN_VM_TAG, "Email not verified")
                        _uiState.update { it.copy(isLoading = false, generalErrorRes = R.string.login_error_email_not_verified) }
                        authRepository.logout()
                        return@launch
                    }

                    if (current.rememberMe) {
                        PreferencesManager.saveRememberedLogin(context, current.email.trim())
                    } else {
                        PreferencesManager.clearRememberedLogin(context)
                    }

                    _uiState.update { it.copy(isLoading = false, password = "", isSuccess = true) }
                },
                onFailure = { error ->
                    Log.e(LOGIN_VM_TAG, "Login failed: ${error.message}")
                    _uiState.update { it.copy(isLoading = false, generalErrorRes = R.string.login_error_invalid_credentials) }
                }
            )
        }
    }

    fun resetState() {
        _uiState.update { it.copy(isSuccess = false, generalErrorRes = null) }
    }

    private fun validateEmail(value: String): Int? = when {
        value.trim().isBlank() -> R.string.login_error_email_required
        !Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches() -> R.string.login_error_email_invalid
        else -> null
    }

    private fun validatePassword(value: String): Int? = when {
        value.isBlank() -> R.string.login_error_password_required
        value.length < 6 -> R.string.login_error_password_short
        else -> null
    }
}