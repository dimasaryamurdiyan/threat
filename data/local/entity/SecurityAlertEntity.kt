package com.singaludra.threat.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_alerts")
data class SecurityAlertEntity(
    @PrimaryKey
    val id: String,
    val severity: String,
    val source: String,
    val threatType: String,
    val timestamp: String,
    val isAcknowledged: Boolean = false,
    val isDismissed: Boolean = false
)