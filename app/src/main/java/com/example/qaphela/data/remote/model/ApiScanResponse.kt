package com.example.qaphela.data.remote.model

data class ApiScanResponse(
    val riskLevel: String,
    val detectedIndicators: List<String>,
    val explanation: String,
    val recommendedAction: String
)
