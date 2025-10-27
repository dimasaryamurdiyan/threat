package com.singaludra.threat.domain.repository

import com.singaludra.threat.domain.model.AlertSeverity
import com.singaludra.threat.domain.model.SecurityAlert
import kotlinx.coroutines.flow.Flow

interface SecurityAlertRepository {

    fun getAllAlerts(): Flow<List<SecurityAlert>>

    suspend fun getAlertById(id: String): SecurityAlert?

    fun getAlertsBySeverity(severity: AlertSeverity): Flow<List<SecurityAlert>>

    fun getAlertsByThreatType(threatType: String): Flow<List<SecurityAlert>>

    fun getAcknowledgedAlerts(): Flow<List<SecurityAlert>>

    fun getDismissedAlerts(): Flow<List<SecurityAlert>>

    fun getActiveAlerts(): Flow<List<SecurityAlert>>

    suspend fun refreshAlerts(): Result<Unit>

    suspend fun acknowledgeAlert(id: String): Result<Unit>

    suspend fun dismissAlert(id: String): Result<Unit>

    suspend fun deleteAlert(id: String): Result<Unit>

    suspend fun deleteAllAlerts(): Result<Unit>

    suspend fun getAlertsCount(): Int
}