package com.example.voyagetime.ui.viewmodels

import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import com.example.voyagetime.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val REGISTER_VM_TAG = "RegisterViewModel"

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val acceptTerms: Boolean = false,
    @StringRes val usernameErrorRes: Int? = null,
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val passwordErrorRes: Int? = null,
    @StringRes val confirmPasswordErrorRes: Int? = null,
    @StringRes val termsErrorRes: Int? = null,
    @StringRes val infoMessageRes: Int? = null
)

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { current ->
            current.copy(
                username = value,
                usernameErrorRes = null,
                infoMessageRes = null
            )
        }
    }

    fun onEmailChange(value: String) {
        _uiState.update { current ->
            current.copy(
                email = value,
                emailErrorRes = null,
                infoMessageRes = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { current ->
            current.copy(
                password = value,
                passwordErrorRes = null,
                infoMessageRes = null
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { current ->
            current.copy(
                confirmPassword = value,
                confirmPasswordErrorRes = null,
                infoMessageRes = null
            )
        }
    }

    fun onAcceptTermsChange(value: Boolean) {
        _uiState.update { current ->
            current.copy(
                acceptTerms = value,
                termsErrorRes = null,
                infoMessageRes = null
            )
        }
    }

    fun validateDesignForm() {
        val current = _uiState.value

        val usernameError = validateUsername(current.username)
        val emailError = validateEmail(current.email)
        val passwordError = validatePassword(current.password)
        val confirmPasswordError = validateConfirmPassword(
            password = current.password,
            confirmPassword = current.confirmPassword
        )
        val termsError = if (!current.acceptTerms) {
            R.string.register_error_terms_required
        } else {
            null
        }

        val formIsValid =
            usernameError == null &&
                    emailError == null &&
                    passwordError == null &&
                    confirmPasswordError == null &&
                    termsError == null

        if (formIsValid) {
            Log.i(
                REGISTER_VM_TAG,
                "Register form validation passed. T3.1 register form design is ready."
            )

            _uiState.update { oldState ->
                oldState.copy(
                    usernameErrorRes = null,
                    emailErrorRes = null,
                    passwordErrorRes = null,
                    confirmPasswordErrorRes = null,
                    termsErrorRes = null,
                    infoMessageRes = R.string.register_info_t3_2_pending
                )
            }
        } else {
            Log.w(REGISTER_VM_TAG, "Register form validation failed.")

            _uiState.update { oldState ->
                oldState.copy(
                    usernameErrorRes = usernameError,
                    emailErrorRes = emailError,
                    passwordErrorRes = passwordError,
                    confirmPasswordErrorRes = confirmPasswordError,
                    termsErrorRes = termsError,
                    infoMessageRes = null
                )
            }
        }
    }

    private fun validateUsername(value: String): Int? {
        val cleanValue = value.trim()

        return when {
            cleanValue.isBlank() -> R.string.register_error_username_required
            cleanValue.length < 2 -> R.string.register_error_username_short
            else -> null
        }
    }

    private fun validateEmail(value: String): Int? {
        val cleanValue = value.trim()

        return when {
            cleanValue.isBlank() -> R.string.register_error_email_required
            !Patterns.EMAIL_ADDRESS.matcher(cleanValue).matches() -> R.string.register_error_email_invalid
            else -> null
        }
    }

    private fun validatePassword(value: String): Int? {
        return when {
            value.isBlank() -> R.string.register_error_password_required
            value.length < 6 -> R.string.register_error_password_short
            else -> null
        }
    }

    private fun validateConfirmPassword(
        password: String,
        confirmPassword: String
    ): Int? {
        return when {
            confirmPassword.isBlank() -> R.string.register_error_confirm_required
            confirmPassword != password -> R.string.register_error_passwords_not_match
            else -> null
        }
    }
}