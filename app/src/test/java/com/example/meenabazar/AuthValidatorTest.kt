package com.example.meenabazar

import org.junit.Assert.*
import org.junit.Test

class AuthValidatorTest {
    private val valid = RegistrationData("Amina Rahman", "amina@example.com", "cotton123", "cotton123")

    @Test fun validRegistrationAcceptsTrimmedNamesAndEmails() {
        assertTrue(AuthValidator.registration(valid.copy(fullName = "  Amina  ", email = " AMINA@example.com ")).isValid)
        assertEquals("amina@example.com", AuthValidator.normalizeEmail(" AMINA@example.com "))
    }

    @Test fun emptyRegistrationReportsEveryField() {
        val errors = AuthValidator.registration(RegistrationData())
        assertFalse(errors.isValid)
        assertNotNull(errors.fullNameError)
        assertNotNull(errors.emailError)
        assertNotNull(errors.passwordError)
        assertNotNull(errors.confirmPasswordError)
    }

    @Test fun malformedEmailsAreRejected() {
        listOf("no-at-sign", "a@", "@example.com", "a@example", "a b@example.com", "a@-example.com", "a@example..com").forEach {
            assertNotNull(it, AuthValidator.emailError(it))
        }
    }

    @Test fun passwordLengthAndConfirmationAreValidated() {
        assertNotNull(AuthValidator.registration(valid.copy(password = "short", confirmPassword = "short")).passwordError)
        assertNotNull(AuthValidator.registration(valid.copy(confirmPassword = "different")).confirmPasswordError)
        assertNotNull(AuthValidator.registration(valid.copy(password = " ".repeat(8), confirmPassword = " ".repeat(8))).passwordError)
        assertNotNull(AuthValidator.registration(valid.copy(password = "a".repeat(129))).passwordError)
        assertTrue(AuthValidator.registration(valid.copy(password = "a".repeat(128), confirmPassword = "a".repeat(128))).isValid)
    }

    @Test fun unicodeNamesAreAcceptedAndShortNamesRejected() {
        assertTrue(AuthValidator.registration(valid.copy(fullName = "আমিনা")).isValid)
        assertNotNull(AuthValidator.registration(valid.copy(fullName = " A ")).fullNameError)
    }

    @Test fun loginChecksBothFields() {
        assertFalse(AuthValidator.login(LoginData()).isValid)
        assertFalse(AuthValidator.login(LoginData("bad-email", "password")).isValid)
        assertTrue(AuthValidator.login(LoginData("amina@example.com", "password")).isValid)
    }
}
