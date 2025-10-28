package com.singaludra.threat.data.repository

import com.singaludra.threat.data.local.dao.SecurityAlertDao
import com.singaludra.threat.data.mapper.SecurityAlertMapper
import com.singaludra.threat.data.remote.api.SecurityAlertApiService
import com.singaludra.threat.domain.model.AlertSeverity
import com.singaludra.threat.domain.model.SecurityAlert
import com.singaludra.threat.domain.repository.SecurityAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurityAlertRepositoryImpl @Inject constructor(
    private val apiService: SecurityAlertApiService,
    private val dao: SecurityAlertDao
) : SecurityAlertRepository {

    override fun getAllAlerts(): Flow<List<SecurityAlert>> {
        return dao.getAllAlerts().map { entities ->
            entities.map { SecurityAlertMapper.entityToDomain(it) }
        }
    }

    override suspend fun getAlertById(id: String): SecurityAlert? {
        return dao.getAlertById(id)?.let { SecurityAlertMapper.entityToDomain(it) }
    }

    override fun getAlertsBySeverity(severity: AlertSeverity): Flow<List<SecurityAlert>> {
        return dao.getAlertsBySeverity(severity.value).map { entities ->
            entities.map { SecurityAlertMapper.entityToDomain(it) }
        }
    }

    override fun getAlertsByThreatType(threatType: String): Flow<List<SecurityAlert>> {
        return dao.getAlertsByThreatType("%$threatType%").map { entities ->
            entities.map { SecurityAlertMapper.entityToDomain(it) }
        }
    }

    override fun getAcknowledgedAlerts(): Flow<List<SecurityAlert>> {
        return dao.getAlertsByAcknowledgementStatus(true).map { entities ->
            entities.map { SecurityAlertMapper.entityToDomain(it) }
        }
    }

    override fun getDismissedAlerts(): Flow<List<SecurityAlert>> {
        return dao.getAlertsByDismissalStatus(true).map { entities ->
            entities.map { SecurityAlertMapper.entityToDomain(it) }
        }
    }

    override fun getActiveAlerts(): Flow<List<SecurityAlert>> {
        return dao.getAlertsByDismissalStatus(false).map { entities ->
            entities.map { SecurityAlertMapper.entityToDomain(it) }
        }
    }

    override suspend fun refreshAlerts(): Result<Unit> {
        return try {
            val response = apiService.getSecurityAlerts()
            if (response.isSuccessful && response.body() != null) {
                val alertDtos = response.body()!!
                val alertEntities = alertDtos.map { dto ->
                    // Preserve local state (acknowledged/dismissed) for existing alerts
                    val existingAlert = dao.getAlertById(dto.id)
                    SecurityAlertMapper.dtoToEntity(dto).copy(
                        isAcknowledged = existingAlert?.isAcknowledged ?: false,
                        isDismissed = existingAlert?.isDismissed ?: false
                    )
                }

                // Use replace strategy to update existing alerts while preserving local changes
                dao.insertAlerts(alertEntities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to fetch alerts: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun acknowledgeAlert(id: String): Result<Unit> {
        return try {
            dao.updateAcknowledgementStatus(id, true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun dismissAlert(id: String): Result<Unit> {
        return try {
            dao.updateDismissalStatus(id, true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAlert(id: String): Result<Unit> {
        return try {
            val alert = dao.getAlertById(id)
            if (alert != null) {
                dao.deleteAlert(alert)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Alert not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteAllAlerts(): Result<Unit> {
        return try {
            dao.deleteAllAlerts()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAlertsCount(): Int {
        return dao.getAlertsCount()
    }
}