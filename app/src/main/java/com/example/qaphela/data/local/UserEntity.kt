package com.example.qaphela.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val salt: String,
    val preferredLanguage: String = "en",
    val createdAt: Long = System.currentTimeMillis()
)
