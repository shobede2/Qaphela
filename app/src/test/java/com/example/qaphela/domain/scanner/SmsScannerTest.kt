package com.example.qaphela.domain.scanner

import com.example.qaphela.domain.model.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SmsScannerTest {

    private lateinit var smsScanner: SmsScanner

    @Before
    fun setUp() {
        smsScanner = SmsScanner()
    }

    @Test
    fun scan_emptyMessage_returnsUnable() {
        val result = smsScanner.scan("   ")
        assertEquals(RiskLevel.UNABLE, result.riskLevel)
        assertTrue(result.indicators.isEmpty())
    }

    @Test
    fun scan_legitimateMessage_returnsNoIndicators() {
        val result = smsScanner.scan("Hi Mom, see you at 5pm for dinner!")
        assertEquals(RiskLevel.NO_INDICATORS, result.riskLevel)
        assertTrue(result.indicators.isEmpty())
    }

    @Test
    fun scan_otpRequest_returnsHighRisk() {
        val result = smsScanner.scan("Your Capitec OTP is 482910. Never share your PIN or OTP with anyone.")
        assertEquals(RiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.indicators.any { it.category == "Credential Threat" })
    }

    @Test
    fun scan_urgentPressureTactics_returnsSuspiciousOrHighRisk() {
        val result = smsScanner.scan("URGENT: Your account will be locked immediately within 24 hours. Act now.")
        assertTrue(result.riskLevel == RiskLevel.SUSPICIOUS || result.riskLevel == RiskLevel.HIGH_RISK)
        assertTrue(result.indicators.any { it.category == "Social Engineering" })
    }

    @Test
    fun scan_unexpectedFeeDemand_returnsHighRisk() {
        val result = smsScanner.scan("SA Post Office: Your parcel is on hold due to an unpaid R15.00 delivery fee.")
        assertEquals(RiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.indicators.any { it.category == "Financial Scam" })
    }

    @Test
    fun scan_shortenedUrlInSms_returnsHighRisk() {
        val result = smsScanner.scan("Claim your SASSA grant immediately at http://bit.ly/sassa-claim")
        assertEquals(RiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.indicators.any { it.category == "Deceptive Link" || it.category == "Brand Impersonation" })
    }

    @Test
    fun scan_bankImpersonationWithLink_returnsHighRisk() {
        val result = smsScanner.scan("FNB Alert: Unrecognized login detected. Verify immediately at http://fnb-secure-login.online")
        assertEquals(RiskLevel.HIGH_RISK, result.riskLevel)
        assertTrue(result.indicators.any { it.category == "Brand Impersonation" })
    }
}
