package com.example.voyagetime.ui.viewmodels

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.voyagetime.R
import com.example.voyagetime.data.repository.FirebaseAuthRepositoryImpl
import com.example.voyagetime.domain.repository.AuthRepository
import com.example.voyagetime.ui.screens.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val LOGIN_VM_TAG = "LoginViewModel"

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val passwordErrorRes: Int? = null,
    @StringRes val generalErrorRes: Int? = null,
    val isLoading: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun prepareLoginForm(context: Context) {
        val rememberLogin = PreferencesManager.getRememberLogin(context)
        val rememberedEmail = if (rememberLogin) {
            PreferencesManager.getRememberedEmail(context)
        } else {
            ""
        }

        Log.i(
            LOGIN_VM_TAG,
            "Login form prepared. rememberLogin=$rememberLogin rememberedEmail=$rememberedEmail"
        )

        _uiState.value = LoginUiState(
            email = rememberedEmail,
            password = "",
            rememberMe = rememberLogin
        )
    }

    fun onEmailChange(value: String) {
        _uiState.update { current ->
            current.copy(
                email = value,
                emailErrorRes = null,
                generalErrorRes = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { current ->
            current.copy(
                password = value,
                passwordErrorRes = null,
                generalErrorRes = null
            )
        }
    }

    fun onRememberMeChange(value: Boolean) {
        Log.i(LOGIN_VM_TAG, "Remember me changed: $value")

        _uiState.update { current ->
            current.copy(
                rememberMe = value,
                generalErrorRes = null
            )
        }
    }

    fun login(
        context: Context,
        onSuccess: () -> Unit
    ) {
        val current = _uiState.value

        val emailValidation = validateEmail(current.email)
        val passwordValidation = validatePassword(current.password)

        if (emailValidation != null || passwordValidation != null) {
            Log.w(
                LOGIN_VM_TAG,
                "Login validation failed. emailError=$emailValidation passwordError=$passwordValidation"
            )

            _uiState.update { oldState ->
                oldState.copy(
                    emailErrorRes = emailValidation,
                    passwordErrorRes = passwordValidation,
                    generalErrorRes = null
                )
            }
            return
        }

        _uiState.update { oldState ->
            oldState.copy(
                isLoading = true,
                emailErrorRes = null,
                passwordErrorRes = null,
                generalErrorRes = null
            )
        }

        authRepository.login(
            email = current.email,
            password = current.password,
            onSuccess = {
                Log.i(LOGIN_VM_TAG, "Login success callback received")

                if (current.rememberMe) {
                    PreferencesManager.saveRememberedLogin(context, current.email)
                } else {
                    PreferencesManager.clearRememberedLogin(context)
                }

                _uiState.update { oldState ->
                    oldState.copy(
                        isLoading = false,
                        password = ""
                    )
                }

                onSuccess()
            },
            onError = { message ->
                Log.e(LOGIN_VM_TAG, "Login failed: $message")

                _uiState.update { oldState ->
                    oldState.copy(
                        isLoading = false,
                        generalErrorRes = R.string.login_error_invalid_credentials
                    )
                }
            }
        )
    }

    private fun validateEmail(value: String): Int? {
        val cleanValue = value.trim()

        return when {
            cleanValue.isBlank() -> R.string.login_error_email_required
            !Patterns.EMAIL_ADDRESS.matcher(cleanValue).matches() -> R.string.login_error_email_invalid
            else -> null
        }
    }

    private fun validatePassword(value: String): Int? {
        return when {
            value.isBlank() -> R.string.login_error_password_required
            value.length < 6 -> R.string.login_error_password_short
            else -> null
        }
    }
}