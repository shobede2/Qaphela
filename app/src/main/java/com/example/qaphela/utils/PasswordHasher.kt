package com.example.qaphela.utils

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

object PasswordHasher {

    private val SPECIAL_CHAR_REGEX = Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]")

    fun validatePasswordComplexity(password: String): Pair<Boolean, String?> {
        if (password.length < 6) {
            return Pair(false, "Password must be at least 6 characters long.")
        }
        if (!password.contains(SPECIAL_CHAR_REGEX)) {
            return Pair(false, "Password must contain at least one special character (e.g. @, #, $, !).")
        }
        return Pair(true, null)
    }

    fun generateSalt(): String {
        val random = SecureRandom()
        val saltBytes = ByteArray(16)
        random.nextBytes(saltBytes)
        return Base64.getEncoder().encodeToString(saltBytes)
    }

    fun hashPassword(password: String, salt: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt.toByteArray(Charsets.UTF_8))
        val hashedBytes = md.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hashedBytes)
    }

    fun verifyPassword(password: String, salt: String, expectedHash: String): Boolean {
        val computedHash = hashPassword(password, salt)
        return computedHash == expectedHash
    }
}
