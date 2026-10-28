package com.example.qaphela.domain.scanner

import com.example.qaphela.data.local.BlocklistEntity
import com.example.qaphela.domain.model.DetectedIndicator
import com.example.qaphela.domain.model.RiskLevel
import com.example.qaphela.domain.model.ScanFinding
import com.example.qaphela.domain.model.ScanType
import java.net.URI

class UrlScanner {

    fun scan(urlInput: String, blocklistMatches: List<BlocklistEntity> = emptyList()): ScanFinding {
        val trimmed = urlInput.trim()
        if (trimmed.isEmpty()) {
            return ScanFinding(
                scanType = ScanType.URL,
                content = urlInput,
                riskLevel = RiskLevel.UNABLE,
                indicators = emptyList(),
                explanation = "URL input was empty.",
                recommendedAction = "Please enter or paste a valid web address."
            )
        }

        // Format prepending http if protocol is missing for URI parsing
        val formattedUrl = if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            "https://$trimmed"
        } else {
            trimmed
        }

        val uri = try {
            URI(formattedUrl)
        } catch (e: Exception) {
            return ScanFinding(
                scanType = ScanType.URL,
                content = trimmed,
                riskLevel = RiskLevel.UNABLE,
                indicators = emptyList(),
                explanation = "Invalid URL structure: ${e.localizedMessage ?: "Format error"}",
                recommendedAction = "Please check the URL for typos and ensure it is formatted correctly (e.g. https://domain.com)."
            )
        }

        val host = uri.host ?: ""
        if (host.isEmpty()) {
            return ScanFinding(
                scanType = ScanType.URL,
                content = trimmed,
                riskLevel = RiskLevel.UNABLE,
                indicators = emptyList(),
                explanation = "Invalid domain name in URL.",
                recommendedAction = "Please enter a valid web domain."
            )
        }

        val indicators = mutableListOf<DetectedIndicator>()
        val lowerHost = host.lowercase()
        val lowerUrl = formattedUrl.lowercase()

        // 1. Known Blocklist Match
        if (blocklistMatches.isNotEmpty()) {
            for (match in blocklistMatches) {
                indicators.add(
                    DetectedIndicator(
                        category = "Blocklist Hit",
                        title = "Known Scam Indicator Found",
                        description = "Matches blocklist record: ${match.indicator} (${match.reason})",
                        isHighRisk = true
                    )
                )
            }
        }

        // 2. Direct IP Address Host
        val ipRegex = Regex("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")
        if (ipRegex.matches(lowerHost)) {
            indicators.add(
                DetectedIndicator(
                    category = "Anomalous Host",
                    title = "Raw IP Address Used Instead of Domain Name",
                    description = "Legitimate services use registered domain names. IP hosts are frequently used by phishing servers.",
                    isHighRisk = true
                )
            )
        }

        // 3. Deceptive / Typo-Squatted Keywords in Host
        val suspiciousKeywords = listOf(
            "-bank", "capitec-", "fnb-", "absa-", "nedbank-", "standardbank-",
            "-sars", "sars-", "login-", "secure-login", "verify-account",
            "update-banking", "banking-security", "postoffice-", "sapo-"
        )
        for (keyword in suspiciousKeywords) {
            if (lowerHost.contains(keyword)) {
                indicators.add(
                    DetectedIndicator(
                        category = "Phishing Domain",
                        title = "Deceptive Domain Structure",
                        description = "Host '$host' contains suspicious banking or service hyphenation ('$keyword').",
                        isHighRisk = true
                    )
                )
                break
            }
        }

        // 4. High Risk Top Level Domains (TLDs)
        val riskyTlds = listOf(".xyz", ".top", ".online", ".site", ".cc", ".club", ".info", ".work", ".click", ".monster", ".fit", ".ga", ".tk", ".cf", ".gq")
        for (tld in riskyTlds) {
            if (lowerHost.endsWith(tld)) {
                indicators.add(
                    DetectedIndicator(
                        category = "High-Risk TLD",
                        title = "Suspicious Top-Level Domain Extension ($tld)",
                        description = "Domain '$host' uses a cheap or disposable TLD frequently associated with fraud campaigns.",
                        isHighRisk = false
                    )
                )
                break
            }
        }

        // 5. Excessive Subdomains
        val parts = lowerHost.split(".")
        if (parts.size > 4) {
            indicators.add(
                DetectedIndicator(
                    category = "Domain Complexity",
                    title = "Excessive Subdomains Detected",
                    description = "Domain '$host' has multiple nested levels, often used to disguise the real parent domain.",
                    isHighRisk = false
                )
            )
        }

        // 6. User Info / @ symbol trickery
        if (uri.userInfo != null || lowerUrl.contains("@")) {
            indicators.add(
                DetectedIndicator(
                    category = "Deceptive Format",
                    title = "Credential Masking in URL (@ Symbol)",
                    description = "The '@' character in URLs is used by attackers to hide the real destination server.",
                    isHighRisk = true
                )
            )
        }

        // 7. Non-standard Port
        if (uri.port != -1 && uri.port != 80 && uri.port != 443) {
            indicators.add(
                DetectedIndicator(
                    category = "Non-Standard Port",
                    title = "Unusual Network Port (${uri.port})",
                    description = "Web address connects to port ${uri.port} instead of standard HTTPS (443).",
                    isHighRisk = false
                )
            )
        }

        val riskLevel = when {
            indicators.any { it.isHighRisk } -> RiskLevel.HIGH_RISK
            indicators.isNotEmpty() -> RiskLevel.SUSPICIOUS
            else -> RiskLevel.NO_INDICATORS
        }

        val explanation = when (riskLevel) {
            RiskLevel.HIGH_RISK -> "High risk detected. This URL displays known scam patterns, blocklist matches, or deceptive domain structures."
            RiskLevel.SUSPICIOUS -> "Suspicious URL characteristics detected. Exercise caution before opening this website."
            RiskLevel.NO_INDICATORS -> "No known malicious indicators were matched against our local threat list. Always verify the domain name carefully."
            RiskLevel.UNABLE -> "Unable to scan URL."
        }

        val recommendedAction = when (riskLevel) {
            RiskLevel.HIGH_RISK -> "DO NOT click or open this link. Do not enter any login credentials or personal information."
            RiskLevel.SUSPICIOUS -> "Verify the domain carefully. Ensure the website matches the official address of the company."
            RiskLevel.NO_INDICATORS -> "Verify the URL matches official sources. HTTPS encryption alone does not guarantee a site is safe."
            RiskLevel.UNABLE -> "Please enter a valid URL."
        }

        return ScanFinding(
            scanType = ScanType.URL,
            content = trimmed,
            riskLevel = riskLevel,
            indicators = indicators,
            explanation = explanation,
            recommendedAction = recommendedAction
        )
    }
}
