package com.example.qaphela.domain.scanner

import com.example.qaphela.data.local.BlocklistEntity
import com.example.qaphela.domain.model.RiskLevel

class ChatbotEngine {

    private val smsScanner = SmsScanner()
    private val urlScanner = UrlScanner()

    fun generateResponse(userMessage: String, blocklistMatches: List<BlocklistEntity> = emptyList()): String {
        val lower = userMessage.lowercase().trim()

        if (lower.isEmpty()) {
            return "Please enter a message or paste a link you would like me to analyze."
        }

        // Check for URLs in message
        val urlRegex = Regex("(https?://[^\\s]+)|(www\\.[^\\s]+)")
        val foundUrls = urlRegex.findAll(userMessage).map { it.value }.toList()

        if (foundUrls.isNotEmpty()) {
            val urlToTest = foundUrls.first()
            val urlFinding = urlScanner.scan(urlToTest, blocklistMatches)
            return when (urlFinding.riskLevel) {
                RiskLevel.HIGH_RISK -> "🚨 WARNING: The link '$urlToTest' is HIGH RISK! ${urlFinding.explanation} Recommended Action: ${urlFinding.recommendedAction}\n\nDisclaimer: Qaphela provides advisory assistance. Always verify directly with official bank channels."
                RiskLevel.SUSPICIOUS -> "⚠️ SUSPICIOUS LINK: The link '$urlToTest' contains warning indicators. ${urlFinding.explanation} Recommended Action: ${urlFinding.recommendedAction}"
                RiskLevel.NO_INDICATORS -> "ℹ️ No obvious scam indicators found for '$urlToTest'. However, HTTPS does not guarantee safety. Ensure this matches official domains before entering credentials."
                RiskLevel.UNABLE -> "The web address '$urlToTest' appears malformed. Please enter a valid link."
            }
        }

        // Action when user says "I clicked a link" or "I gave my PIN"
        if (lower.contains("clicked") || lower.contains("entered my pin") || lower.contains("shared otp") || lower.contains("gave password") || lower.contains("scammed")) {
            return "🆘 IMMEDIATE FRAUD RECOVERY STEPS:\n1. Contact your bank's fraud line immediately to freeze your accounts.\n2. Change your banking passwords and PINs.\n3. Tap 'Get Help Fast' on Qaphela's home screen to view your bank's fraud hotline number or USSD card freeze code."
        }

        // Questions about SASSA / SRD
        if (lower.contains("sassa") || lower.contains("srd") || lower.contains("grant")) {
            return "ℹ️ SASSA Grant Guidance: SASSA will NEVER ask for a payment or delivery fee to release your grant. Official SASSA info is available at sassa.gov.za or by calling SASSA official line [verify with official source]."
        }

        // Questions about SARS / Tax refund
        if (lower.contains("sars") || lower.contains("tax") || lower.contains("e-filing") || lower.contains("efiling")) {
            return "ℹ️ SARS Tax Refund Guidance: SARS does NOT issue tax refunds via SMS links or ask for banking PINs via text. Access your eFiling strictly at sars.gov.za."
        }

        // Questions about Bank OTPs / Capitec / FNB / ABSA / Standard Bank
        if (lower.contains("otp") || lower.contains("capitec") || lower.contains("fnb") || lower.contains("absa") || lower.contains("nedbank") || lower.contains("standard bank")) {
            return "🏦 Banking Security Notice: South African banks will NEVER call or SMS you asking for your OTP or password. If anyone asks for your OTP, it is a SCAM."
        }

        // General SMS scanning evaluation
        val smsFinding = smsScanner.scan(userMessage)
        if (smsFinding.indicators.isNotEmpty()) {
            val indicatorText = smsFinding.indicators.joinToString("\n• ") { "${it.title}: ${it.description}" }
            return "🔍 Analysis Results for your text:\nRisk Level: ${smsFinding.riskLevel.name}\n\nKey Findings:\n• $indicatorText\n\nAdvice: ${smsFinding.recommendedAction}"
        }

        // Default friendly advisory AI helper greeting
        return "Hello! I am Qaphela's AI Scam Assistant. You can paste a suspicious link, SMS text, or ask me questions like 'Is this SASSA SMS real?' or 'I clicked a link, what do I do?'\n\nRemember: Qaphela provides advisory assistance and cannot guarantee 100% safety."
    }
}
