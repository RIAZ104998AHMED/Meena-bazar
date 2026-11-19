package com.example.meenabazar

import java.util.Locale

data class RegistrationData(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

data class LoginData(val email: String = "", val password: String = "")

data class RegistrationValidationState(
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) {
    val isValid: Boolean
        get() = listOf(fullNameError, emailError, passwordError, confirmPasswordError).all { it == null }
}

data class LoginValidationState(val emailError: String? = null, val passwordError: String? = null) {
    val isValid: Boolean get() = emailError == null && passwordError == null
}

object AuthValidator {
    private val emailPattern = Regex("^[A-Za-z0-9.!#$%&'*+/=?^_`{|}~-]+@[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?(?:\\.[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?)+$")

    fun normalizeEmail(email: String): String = email.trim().lowercase(Locale.ROOT)

    fun emailError(email: String): String? = when {
        email.isBlank() -> "Email is required"
        email.trim().length > 254 || !emailPattern.matches(email.trim()) -> "Enter a valid email address"
        else -> null
    }

    fun registration(data: RegistrationData) = RegistrationValidationState(
        fullNameError = when {
            data.fullName.isBlank() -> "Full name is required"
            data.fullName.trim().length < 2 -> "Enter at least 2 characters"
            data.fullName.trim().length > 80 -> "Use 80 characters or fewer"
            else -> null
        },
        emailError = emailError(data.email),
        passwordError = when {
            data.password.isBlank() -> "Password is required"
            data.password.length < 8 -> "Use at least 8 characters"
            data.password.length > 128 -> "Use 128 characters or fewer"
            else -> null
        },
        confirmPasswordError = when {
            data.confirmPassword.isBlank() -> "Confirm your password"
            data.password != data.confirmPassword -> "Passwords do not match"
            else -> null
        }
    )

    fun login(data: LoginData) = LoginValidationState(
        emailError = emailError(data.email),
        passwordError = if (data.password.isBlank()) "Password is required" else null
    )
}

enum class ScreenState {
    AUTHORIZATION, LOGIN, SIGN_UP, SHOP, PRODUCT, CART, CHECKOUT, ORDERS, ORDER_SUCCESS, ACCOUNT
}
