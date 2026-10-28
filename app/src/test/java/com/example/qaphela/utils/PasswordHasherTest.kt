package com.example.qaphela.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHasherTest {

    @Test
    fun validatePasswordComplexity_shortPassword_returnsFalse() {
        val (isValid, error) = PasswordHasher.validatePasswordComplexity("a@1")
        assertFalse(isValid)
        assertTrue(error?.contains("at least 6 characters") == true)
    }

    @Test
    fun validatePasswordComplexity_noSpecialChar_returnsFalse() {
        val (isValid, error) = PasswordHasher.validatePasswordComplexity("password123")
        assertFalse(isValid)
        assertTrue(error?.contains("special character") == true)
    }

    @Test
    fun validatePasswordComplexity_validPassword_returnsTrue() {
        val (isValid, error) = PasswordHasher.validatePasswordComplexity("SecurePass#2025")
        assertTrue(isValid)
        assertTrue(error == null)
    }

    @Test
    fun hashAndVerifyPassword_correctPassword_returnsTrue() {
        val password = "MySecretPass!99"
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword(password, salt)

        val isVerified = PasswordHasher.verifyPassword(password, salt, hash)
        assertTrue(isVerified)
    }

    @Test
    fun verifyPassword_wrongPassword_returnsFalse() {
        val password = "MySecretPass!99"
        val salt = PasswordHasher.generateSalt()
        val hash = PasswordHasher.hashPassword(password, salt)

        val isVerified = PasswordHasher.verifyPassword("WrongPass!99", salt, hash)
        assertFalse(isVerified)
    }
}
