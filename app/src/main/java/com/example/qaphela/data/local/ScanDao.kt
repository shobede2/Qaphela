package com.example.qaphela.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: ScanEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScanResult(result: ScanResultEntity): Long

    @Query("SELECT * FROM scans ORDER BY scanTime DESC")
    fun getAllScans(): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scans ORDER BY scanTime DESC LIMIT :limit")
    fun getRecentScans(limit: Int): Flow<List<ScanEntity>>

    @Query("SELECT * FROM scans WHERE scanId = :scanId")
    suspend fun getScanById(scanId: Long): ScanEntity?

    @Query("SELECT * FROM scan_results WHERE scanId = :scanId LIMIT 1")
    suspend fun getScanResultByScanId(scanId: Long): ScanResultEntity?

    @Query("DELETE FROM scans WHERE scanId = :scanId")
    suspend fun deleteScanById(scanId: Long): Int

    @Query("DELETE FROM scans")
    suspend fun clearAllScans(): Int
}
