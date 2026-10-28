package com.example.qaphela.domain.scanner

import com.example.qaphela.data.local.BlocklistEntity
import com.example.qaphela.domain.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class UrlScannerTest {

    private lateinit var urlScanner: UrlScanner

    @Before
    fun setUp() {
        urlScanner = UrlScanner()
    }

    @Test
    fun scan_emptyUrl_returnsUnable() {
        val result = urlScanner.scan("")
        assertEquals(RiskLevel.UNABLE, result.riskLevel)
    }

    @Test
    fun scan_legitimateUrl_returnsNoIndicators() {
        val result = urlScanner.scan("https://www.capitecbank.co.za")
        assertEquals(RiskLevel.NO_INDICATORS, result.riskLevel)
        assertTrue(result.indicators.isEmpty())
    }

    @Test
    fun scan_malformedUrl_returnsUnable() {
        val result = urlScanner.scan("ht://invalid_url_space 123")
        assertEquals(RiskLevel.UNABLE, result.riskLevel)
    }

    @Test
    fun scan_blocklistMatch_returnsHighRisk() {
        val blocklistMatch = BlocklistEntity(
            indicator = "capitec-login-security-update.xyz",
            indicatorType = "URL",
            reason = "Phishing domain",
            source = "Test Feed"
        )
        val result = urlScanner.scan("https://capitec-login-security-update.xyz", listOf(blocklistMatch))
        assertEquals(RiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.indicators.any { it.category == "Blocklist Hit" })
    }

    @Test
    fun scan_rawIpHost_returnsHighRisk() {
        val result = urlScanner.scan("http://192.168.1.100/login")
        assertEquals(RiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.indicators.any { it.category == "Anomalous Host" })
    }

    @Test
    fun scan_deceptiveKeywordDomain_returnsHighRisk() {
        val result = urlScanner.scan("https://sars-e-filing-tax-refund.online")
        assertEquals(RiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.indicators.any { it.category == "Phishing Domain" })
    }

    @Test
    fun scan_suspiciousTld_returnsSuspiciousOrHighRisk() {
        val result = urlScanner.scan("https://mycompany-update.xyz")
        assertTrue(result.riskLevel == RiskLevel.SUSPICIOUS || result.riskLevel == RiskLevel.HIGH_RISK)
        assertTrue(result.indicators.any { it.category == "High-Risk TLD" })
    }
}
