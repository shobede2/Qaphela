package com.example.qaphela.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scans")
data class ScanEntity(
    @PrimaryKey(autoGenerate = true)
    val scanId: Long = 0,
    val userId: Long? = null,
    val scanType: String, // "SMS" or "URL"
    val content: String,
    val riskLevel: String, // "HIGH_RISK", "SUSPICIOUS", "NO_INDICATORS", "UNABLE"
    val scanTime: Long = System.currentTimeMillis()
)
