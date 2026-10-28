package com.example.qaphela.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "scan_results",
    foreignKeys = [
        ForeignKey(
            entity = ScanEntity::class,
            parentColumns = ["scanId"],
            childColumns = ["scanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["scanId"])]
)
data class ScanResultEntity(
    @PrimaryKey(autoGenerate = true)
    val resultId: Long = 0,
    val scanId: Long,
    val threatCategory: String,
    val explanation: String,
    val recommendedAction: String
)
