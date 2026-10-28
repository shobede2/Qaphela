package com.example.qaphela.domain.model

data class DetectedIndicator(
    val category: String,
    val title: String,
    val description: String,
    val isHighRisk: Boolean = false
)
