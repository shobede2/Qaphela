package com.example.qaphela.domain.scanner

import com.example.qaphela.domain.model.DetectedIndicator
import com.example.qaphela.domain.model.RiskLevel
import com.example.qaphela.domain.model.ScanFinding
import com.example.qaphela.domain.model.ScanType

class SmsScanner {

    fun scan(messageText: String): ScanFinding {
        val trimmed = messageText.trim()
        if (trimmed.isEmpty()) {
            return ScanFinding(
                scanType = ScanType.SMS,
                content = messageText,
                riskLevel = RiskLevel.UNABLE,
                indicators = emptyList(),
                explanation = "Message text was empty.",
                recommendedAction = "Please enter valid SMS text to scan."
            )
        }

        val indicators = mutableListOf<DetectedIndicator>()
        val lower = trimmed.lowercase()

        // 1. PIN / Password / OTP Requests
        if (lower.contains("pin") || lower.contains("password") || lower.contains("otp") ||
            lower.contains("one time pin") || lower.contains("one-time pin") || lower.contains("verification code") ||
            lower.contains("secret code") || lower.contains("passcode")
        ) {
            indicators.add(
                DetectedIndicator(
                    category = "Credential Threat",
                    title = "Request for Sensitive Credentials / OTP",
                    description = "Legitimate institutions and banks will NEVER ask you to share your PIN, password, or OTP via SMS.",
                    isHighRisk = true
                )
            )
        }

        // 2. Urgent / Pressure Tactics
        if (lower.contains("immediately") || lower.contains("within 24 hours") || lower.contains("within 12 hours") ||
            lower.contains("account suspended") || lower.contains("account locked") || lower.contains("urgent action") ||
            lower.contains("act now") || lower.contains("final notice") || lower.contains("terminated")
        ) {
            indicators.add(
                DetectedIndicator(
                    category = "Social Engineering",
                    title = "Urgent Pressure & Fear Tactics",
                    description = "Scammers use artificial urgency (e.g. 'account locked in 24 hours') to induce panic and bypass critical thinking.",
                    isHighRisk = false
                )
            )
        }

        // 3. Payment / Fee / Refund Demands
        if (lower.contains("pay fee") || lower.contains("customs duty") || lower.contains("delivery fee") ||
            lower.contains("outstanding balance") || lower.contains("unpaid tax") || lower.contains("claim refund") ||
            lower.contains("r15.00") || lower.contains("r25.00") || lower.contains("r50.00") || lower.contains("r100")
        ) {
            indicators.add(
                DetectedIndicator(
                    category = "Financial Scam",
                    title = "Unexpected Fee or Refund Claim",
                    description = "Demands for small delivery fees or claims of unexpected tax refunds are common parcel/banking scam hooks.",
                    isHighRisk = true
                )
            )
        }

        // 4. Shortened / Suspicious Links in SMS
        val urlRegex = Regex("(https?://[^\\s]+)")
        val foundUrls = urlRegex.findAll(trimmed).map { it.value }.toList()
        if (foundUrls.isNotEmpty()) {
            for (url in foundUrls) {
                val urlLower = url.lowercase()
                if (urlLower.contains("bit.ly") || urlLower.contains("tinyurl.com") || urlLower.contains("t.co") ||
                    urlLower.contains("is.gd") || urlLower.contains("ow.ly") || urlLower.contains("t.ly") || urlLower.contains("cutt.ly")
                ) {
                    indicators.add(
                        DetectedIndicator(
                            category = "Deceptive Link",
                            title = "Shortened / Masked URL Detected",
                            description = "Link '$url' hides the true destination address.",
                            isHighRisk = true
                        )
                    )
                } else if (!urlLower.contains("capitecbank.co.za") && !urlLower.contains("fnb.co.za") &&
                    !urlLower.contains("standardbank.co.za") && !urlLower.contains("absa.co.za") &&
                    !urlLower.contains("nedbank.co.za") && !urlLower.contains("sars.gov.za") &&
                    !urlLower.contains("postoffice.co.za")
                ) {
                    indicators.add(
                        DetectedIndicator(
                            category = "Unverified Link",
                            title = "External / Unofficial Link",
                            description = "Link '$url' does not match known official South African service domains.",
                            isHighRisk = false
                        )
                    )
                }
            }
        }

        // 5. Impersonation of South African Entities
        val saEntities = listOf(
            "sars", "capitec", "fnb", "absa", "nedbank", "standard bank",
            "post office", "sapo", "postoffice", "discovery", "shoprite",
            "vodacom", "mtn", "telkom", "cell c"
        )
        for (entity in saEntities) {
            if (lower.contains(entity) && foundUrls.isNotEmpty()) {
                indicators.add(
                    DetectedIndicator(
                        category = "Brand Impersonation",
                        title = "Potential ${entity.uppercase()} Impersonation",
                        description = "Message references $entity while prompting interaction with an external web link.",
                        isHighRisk = true
                    )
                )
                break
            }
        }

        val riskLevel = when {
            indicators.any { it.isHighRisk } -> RiskLevel.HIGH_RISK
            indicators.isNotEmpty() -> RiskLevel.SUSPICIOUS
            else -> RiskLevel.NO_INDICATORS
        }

        val explanation = when (riskLevel) {
            RiskLevel.HIGH_RISK -> "High risk detected. This message contains critical scam indicators such as credential harvesting, brand impersonation, or deceptive URLs."
            RiskLevel.SUSPICIOUS -> "Suspicious elements found. This message uses pressure tactics or unverified external links."
            RiskLevel.NO_INDICATORS -> "No obvious scam indicators were detected in this message text. Always stay cautious."
            RiskLevel.UNABLE -> "Unable to scan message."
        }

        val recommendedAction = when (riskLevel) {
            RiskLevel.HIGH_RISK -> "DO NOT click any links, DO NOT share PINs/OTPs, and DO NOT send money. Delete the message and block the sender."
            RiskLevel.SUSPICIOUS -> "Do not tap links directly. Contact the company or bank using official phone numbers from their official website."
            RiskLevel.NO_INDICATORS -> "Verify any requests directly with the sender before taking action. Remember that automated tools cannot guarantee complete safety."
            RiskLevel.UNABLE -> "Please enter message text to analyze."
        }

        return ScanFinding(
            scanType = ScanType.SMS,
            content = trimmed,
            riskLevel = riskLevel,
            indicators = indicators,
            explanation = explanation,
            recommendedAction = recommendedAction
        )
    }
}
