package com.example.qaphela.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_reports")
data class UserReportEntity(
    @PrimaryKey(autoGenerate = true)
    val reportId: Long = 0,
    val userId: Long? = null,
    val reportedIndicator: String,
    val description: String? = null,
    val status: String = "Pending Moderation",
    val submittedAt: Long = System.currentTimeMillis()
)
