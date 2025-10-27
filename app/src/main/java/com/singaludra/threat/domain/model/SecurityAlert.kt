package com.singaludra.threat.domain.model

import java.time.LocalDateTime

data class SecurityAlert(
    val id: String,
    val severity: AlertSeverity,
    val source: String,
    val threatType: String,
    val timestamp: LocalDateTime,
    val isAcknowledged: Boolean = false,
    val isDismissed: Boolean = false
)

enum class AlertSeverity(val value: String, val displayName: String) {
    LOW("Low", "Low"),
    MEDIUM("Medium", "Medium"),
    HIGH("High", "High"),
    CRITICAL("Critical", "Critical");

    companion object {
        fun fromString(value: String): AlertSeverity {
            return entries.find { it.value.equals(value, ignoreCase = true) } ?: MEDIUM
        }
    }
}