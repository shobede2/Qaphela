package com.example.qaphela.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "languages")
data class LanguageEntity(
    @PrimaryKey(autoGenerate = true)
    val languageId: Long = 0,
    val languageName: String,
    val languageCode: String,
    val isEnabled: Boolean = true
)
