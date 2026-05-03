package com.example.voyagetime.ui.viewmodels

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.annotation.StringRes
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.voyagetime.R
import com.example.voyagetime.data.local.database.VoyageTimeDatabase
import com.example.voyagetime.data.local.entity.UserEntity
import com.example.voyagetime.data.repository.FirebaseAuthRepositoryImpl
import com.example.voyagetime.data.repository.UserRepositoryImpl
import com.example.voyagetime.domain.repository.AuthRepository
import com.example.voyagetime.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private const val REGISTER_VM_TAG = "RegisterViewModel"

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val birthdate: String = "",
    val address: String = "",
    val country: String = "",
    val phone: String = "",
    val acceptEmails: Boolean = false,
    val acceptTerms: Boolean = false,
    @StringRes val usernameErrorRes: Int? = null,
    @StringRes val emailErrorRes: Int? = null,
    @StringRes val passwordErrorRes: Int? = null,
    @StringRes val confirmPasswordErrorRes: Int? = null,
    @StringRes val birthdateErrorRes: Int? = null,
    @StringRes val addressErrorRes: Int? = null,
    @StringRes val countryErrorRes: Int? = null,
    @StringRes val phoneErrorRes: Int? = null,
    @StringRes val termsErrorRes: Int? = null,
    @StringRes val infoMessageRes: Int? = null,
    @StringRes val generalErrorRes: Int? = null,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val authRepository: AuthRepository = FirebaseAuthRepositoryImpl()
    private val userRepository: UserRepository

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    init {
        val database = VoyageTimeDatabase.getDatabase(application)
        userRepository = UserRepositoryImpl(database.userDao(), database.accessLogDao())
    }

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, usernameErrorRes = null, generalErrorRes = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailErrorRes = null, generalErrorRes = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordErrorRes = null, generalErrorRes = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, confirmPasswordErrorRes = null, generalErrorRes = null) }
    }

    fun onBirthdateChange(value: String) {
        _uiState.update { it.copy(birthdate = value, birthdateErrorRes = null, generalErrorRes = null) }
    }

    fun onAddressChange(value: String) {
        _uiState.update { it.copy(address = value, addressErrorRes = null, generalErrorRes = null) }
    }

    fun onCountryChange(value: String) {
        _uiState.update { it.copy(country = value, countryErrorRes = null, generalErrorRes = null) }
    }

    fun onPhoneChange(value: String) {
        _uiState.update { it.copy(phone = value, phoneErrorRes = null, generalErrorRes = null) }
    }

    fun onAcceptEmailsChange(value: Boolean) {
        _uiState.update { it.copy(acceptEmails = value, generalErrorRes = null) }
    }

    fun onAcceptTermsChange(value: Boolean) {
        _uiState.update { it.copy(acceptTerms = value, termsErrorRes = null, generalErrorRes = null) }
    }

    fun register() {
        val current = _uiState.value
        val usernameError = validateUsername(current.username)
        val emailError = validateEmail(current.email)
        val passwordError = validatePassword(current.password)
        val confirmPasswordError = validateConfirmPassword(current.password, current.confirmPassword)
        val birthdateError = validateBirthdate(current.birthdate)
        val addressError = validateRequired(current.address, R.string.register_error_address_required)
        val countryError = validateRequired(current.country, R.string.register_error_country_required)
        val phoneError = validateRequired(current.phone, R.string.register_error_phone_required)
        val termsError = if (!current.acceptTerms) R.string.register_error_terms_required else null

        val hasErrors = listOf(
            usernameError,
            emailError,
            passwordError,
            confirmPasswordError,
            birthdateError,
            addressError,
            countryError,
            phoneError,
            termsError
        ).any { it != null }

        if (hasErrors) {
            Log.w(REGISTER_VM_TAG, "Register validation failed")
            _uiState.update {
                it.copy(
                    usernameErrorRes = usernameError,
                    emailErrorRes = emailError,
                    passwordErrorRes = passwordError,
                    confirmPasswordErrorRes = confirmPasswordError,
                    birthdateErrorRes = birthdateError,
                    addressErrorRes = addressError,
                    countryErrorRes = countryError,
                    phoneErrorRes = phoneError,
                    termsErrorRes = termsError,
                    infoMessageRes = null,
                    generalErrorRes = null
                )
            }
            return
        }

        viewModelScope.launch {
            val cleanUsername = current.username.trim()
            val cleanEmail = current.email.trim()
            val parsedBirthdate = parseBirthdate(current.birthdate)

            if (userRepository.isUsernameTaken(cleanUsername)) {
                Log.w(REGISTER_VM_TAG, "Username already taken: $cleanUsername")
                _uiState.update { it.copy(usernameErrorRes = R.string.register_error_username_taken) }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, generalErrorRes = null, infoMessageRes = null) }

            val registerResult = authRepository.register(cleanEmail, current.password)
            registerResult.fold(
                onSuccess = { uid ->
                    try {
                        userRepository.createUser(
                            UserEntity(
                                firebaseUid = uid,
                                username = cleanUsername,
                                email = cleanEmail,
                                birthdate = parsedBirthdate,
                                address = current.address.trim(),
                                country = current.country.trim(),
                                phone = current.phone.trim(),
                                acceptEmails = current.acceptEmails
                            )
                        )

                        authRepository.sendEmailVerification().onFailure { error ->
                            Log.w(REGISTER_VM_TAG, "Verification email could not be sent: ${error.message}")
                        }

                        authRepository.logout()

                        Log.i(REGISTER_VM_TAG, "User registered locally and verification email requested uid=$uid")
                        _uiState.value = RegisterUiState(
                            infoMessageRes = R.string.register_success_verify_email,
                            isSuccess = true
                        )
                    } catch (error: Exception) {
                        Log.e(REGISTER_VM_TAG, "Local user creation failed", error)
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                generalErrorRes = R.string.register_error_generic
                            )
                        }
                    }
                },
                onFailure = { error ->
                    Log.e(REGISTER_VM_TAG, "Firebase register failed", error)
                    val emailConflict = error.message?.contains("already", ignoreCase = true) == true ||
                            error.message?.contains("email address is already", ignoreCase = true) == true
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            emailErrorRes = if (emailConflict) R.string.register_error_email_taken else it.emailErrorRes,
                            generalErrorRes = if (emailConflict) null else R.string.register_error_generic
                        )
                    }
                }
            )
        }
    }

    fun resetState() {
        _uiState.value = RegisterUiState()
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

    private fun validateConfirmPassword(password: String, confirmPassword: String): Int? {
        return when {
            confirmPassword.isBlank() -> R.string.register_error_confirm_required
            confirmPassword != password -> R.string.register_error_passwords_not_match
            else -> null
        }
    }

    private fun validateBirthdate(value: String): Int? {
        val parsed = parseBirthdate(value)
        return when {
            value.trim().isBlank() -> R.string.register_error_birthdate_required
            parsed == null -> R.string.register_error_birthdate_invalid
            parsed.isAfter(LocalDate.now()) -> R.string.register_error_birthdate_future
            else -> null
        }
    }

    private fun validateRequired(value: String, @StringRes errorRes: Int): Int? {
        return if (value.trim().isBlank()) errorRes else null
    }

    private fun parseBirthdate(value: String): LocalDate? {
        return try {
            LocalDate.parse(value.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (_: DateTimeParseException) {
            null
        }
    }
}
