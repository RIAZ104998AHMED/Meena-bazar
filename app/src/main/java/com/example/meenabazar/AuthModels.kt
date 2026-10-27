package com.example.meenabazar

data class RegistrationData(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

data class LoginData(
    val email: String = "",
    val password: String = ""
)

data class RegistrationValidationState(
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

enum class ScreenState {
    AUTHORIZATION,
    LOGIN,
    SIGN_UP
}