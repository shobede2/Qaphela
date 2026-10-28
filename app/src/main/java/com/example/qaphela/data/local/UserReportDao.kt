package com.example.qaphela.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserReportDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: UserReportEntity): Long

    @Query("SELECT * FROM user_reports ORDER BY submittedAt DESC")
    fun getAllReports(): Flow<List<UserReportEntity>>
}
