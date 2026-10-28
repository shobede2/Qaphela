package com.example.qaphela.domain.model

data class ScanFinding(
    val scanType: ScanType,
    val content: String,
    val riskLevel: RiskLevel,
    val indicators: List<DetectedIndicator>,
    val explanation: String,
    val recommendedAction: String,
    val timestamp: Long = System.currentTimeMillis()
)
