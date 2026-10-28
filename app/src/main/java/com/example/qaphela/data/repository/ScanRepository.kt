package com.example.qaphela.data.repository

import com.example.qaphela.data.local.BlocklistDao
import com.example.qaphela.data.local.ScanDao
import com.example.qaphela.data.local.ScanEntity
import com.example.qaphela.data.local.ScanResultEntity
import com.example.qaphela.data.local.UserPreferencesRepository
import com.example.qaphela.domain.model.RiskLevel
import com.example.qaphela.domain.model.ScanFinding
import com.example.qaphela.domain.model.ScanType
import com.example.qaphela.domain.scanner.SmsScanner
import com.example.qaphela.domain.scanner.UrlScanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class ScanRepository(
    private val scanDao: ScanDao,
    private val blocklistDao: BlocklistDao,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    private val smsScanner = SmsScanner()
    private val urlScanner = UrlScanner()

    suspend fun scanSms(messageText: String): Pair<ScanFinding, Long> {
        val finding = smsScanner.scan(messageText)
        var scanId = 0L

        val isHistoryEnabled = userPreferencesRepository.historyEnabledFlow.first()
        if (isHistoryEnabled && finding.riskLevel != RiskLevel.UNABLE) {
            val scanEntity = ScanEntity(
                scanType = ScanType.SMS.name,
                content = finding.content,
                riskLevel = finding.riskLevel.name,
                scanTime = finding.timestamp
            )
            scanId = scanDao.insertScan(scanEntity)

            val categoryStr = finding.indicators.joinToString("; ") { it.category }
            val resultEntity = ScanResultEntity(
                scanId = scanId,
                threatCategory = if (categoryStr.isEmpty()) "General Scan" else categoryStr,
                explanation = finding.explanation,
                recommendedAction = finding.recommendedAction
            )
            scanDao.insertScanResult(resultEntity)
        }

        return Pair(finding, scanId)
    }

    suspend fun scanUrl(urlInput: String): Pair<ScanFinding, Long> {
        val blocklistMatches = blocklistDao.findMatchingBlocklistItems(urlInput)
        val finding = urlScanner.scan(urlInput, blocklistMatches)
        var scanId = 0L

        val isHistoryEnabled = userPreferencesRepository.historyEnabledFlow.first()
        if (isHistoryEnabled && finding.riskLevel != RiskLevel.UNABLE) {
            val scanEntity = ScanEntity(
                scanType = ScanType.URL.name,
                content = finding.content,
                riskLevel = finding.riskLevel.name,
                scanTime = finding.timestamp
            )
            scanId = scanDao.insertScan(scanEntity)

            val categoryStr = finding.indicators.joinToString("; ") { it.category }
            val resultEntity = ScanResultEntity(
                scanId = scanId,
                threatCategory = if (categoryStr.isEmpty()) "URL Assessment" else categoryStr,
                explanation = finding.explanation,
                recommendedAction = finding.recommendedAction
            )
            scanDao.insertScanResult(resultEntity)
        }

        return Pair(finding, scanId)
    }

    fun getAllScans(): Flow<List<ScanEntity>> = scanDao.getAllScans()

    fun getRecentScans(limit: Int = 5): Flow<List<ScanEntity>> = scanDao.getRecentScans(limit)

    suspend fun getScanById(scanId: Long): ScanEntity? = scanDao.getScanById(scanId)

    suspend fun getScanResultByScanId(scanId: Long): ScanResultEntity? = scanDao.getScanResultByScanId(scanId)

    suspend fun deleteScan(scanId: Long) = scanDao.deleteScanById(scanId)

    suspend fun clearAllHistory() = scanDao.clearAllScans()
}
