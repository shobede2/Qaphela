package com.example.qaphela.data.repository

import com.example.qaphela.data.local.UserReportDao
import com.example.qaphela.data.local.UserReportEntity

class ReportRepository(private val userReportDao: UserReportDao) {

    suspend fun submitReport(indicator: String, description: String?): Result<Long> {
        val trimmedIndicator = indicator.trim()
        if (trimmedIndicator.isEmpty()) {
            return Result.failure(IllegalArgumentException("Report indicator cannot be empty"))
        }

        val reportEntity = UserReportEntity(
            reportedIndicator = trimmedIndicator,
            description = description?.trim(),
            status = "Pending Moderation"
        )

        return try {
            val reportId = userReportDao.insertReport(reportEntity)
            Result.success(reportId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
