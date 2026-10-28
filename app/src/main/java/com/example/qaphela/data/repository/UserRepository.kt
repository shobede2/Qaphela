package com.example.qaphela.data.repository

import android.util.Patterns
import com.example.qaphela.data.local.UserDao
import com.example.qaphela.data.local.UserEntity
import com.example.qaphela.data.local.UserPreferencesRepository
import com.example.qaphela.utils.PasswordHasher

class UserRepository(
    private val userDao: UserDao,
    private val userPrefs: UserPreferencesRepository
) {

    suspend fun registerUser(fullName: String, email: String, password: String): Result<UserEntity> {
        val trimmedEmail = email.trim().lowercase()
        val trimmedName = fullName.trim()

        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your full name."))
        }
        if (trimmedEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }

        val (isValidPassword, passwordError) = PasswordHasher.validatePasswordComplexity(password)
        if (!isValidPassword) {
            return Result.failure(IllegalArgumentException(passwordError ?: "Invalid password."))
        }

        val existingUser = userDao.getUserByEmail(trimmedEmail)
        if (existingUser != null) {
            return Result.failure(IllegalArgumentException("An account with this email already exists."))
        }

        val salt = PasswordHasher.generateSalt()
        val passwordHash = PasswordHasher.hashPassword(password, salt)

        val newUser = UserEntity(
            fullName = trimmedName,
            email = trimmedEmail,
            passwordHash = passwordHash,
            salt = salt
        )

        return try {
            val userId = userDao.insertUser(newUser)
            val savedUser = newUser.copy(userId = userId)
            userPrefs.saveUserSession(userId, savedUser.fullName, savedUser.email)
            Result.success(savedUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<UserEntity> {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isEmpty() || password.isEmpty()) {
            return Result.failure(IllegalArgumentException("Please enter your email and password."))
        }

        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return Result.failure(IllegalArgumentException("No account found with this email. Please register first."))

        val isPasswordCorrect = PasswordHasher.verifyPassword(password, user.salt, user.passwordHash)
        if (!isPasswordCorrect) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        userPrefs.saveUserSession(user.userId, user.fullName, user.email)
        return Result.success(user)
    }

    suspend fun updateProfile(fullName: String, email: String): Result<Unit> {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim().lowercase()

        if (trimmedName.isEmpty()) {
            return Result.failure(IllegalArgumentException("Full Name cannot be empty."))
        }
        if (trimmedEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            return Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }

        val userId = userPrefs.loggedInUserIdFlow.toString()
        userPrefs.saveUserSession(1L, trimmedName, trimmedEmail)
        return Result.success(Unit)
    }

    suspend fun logoutUser() {
        userPrefs.clearUserSession()
    }
}
