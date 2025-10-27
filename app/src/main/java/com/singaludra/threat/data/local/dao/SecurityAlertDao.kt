package com.singaludra.threat.data.local.dao

import androidx.room.*
import com.singaludra.threat.data.local.entity.SecurityAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityAlertDao {

    @Query("SELECT * FROM security_alerts ORDER BY timestamp DESC")
    fun getAllAlerts(): Flow<List<SecurityAlertEntity>>

    @Query("SELECT * FROM security_alerts WHERE id = :id")
    suspend fun getAlertById(id: String): SecurityAlertEntity?

    @Query("SELECT * FROM security_alerts WHERE severity = :severity ORDER BY timestamp DESC")
    fun getAlertsBySeverity(severity: String): Flow<List<SecurityAlertEntity>>

    @Query("SELECT * FROM security_alerts WHERE threatType LIKE :threatType ORDER BY timestamp DESC")
    fun getAlertsByThreatType(threatType: String): Flow<List<SecurityAlertEntity>>

    @Query("SELECT * FROM security_alerts WHERE isAcknowledged = :isAcknowledged ORDER BY timestamp DESC")
    fun getAlertsByAcknowledgementStatus(isAcknowledged: Boolean): Flow<List<SecurityAlertEntity>>

    @Query("SELECT * FROM security_alerts WHERE isDismissed = :isDismissed ORDER BY timestamp DESC")
    fun getAlertsByDismissalStatus(isDismissed: Boolean): Flow<List<SecurityAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: SecurityAlertEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlerts(alerts: List<SecurityAlertEntity>)

    @Update
    suspend fun updateAlert(alert: SecurityAlertEntity)

    @Query("UPDATE security_alerts SET isAcknowledged = :isAcknowledged WHERE id = :id")
    suspend fun updateAcknowledgementStatus(id: String, isAcknowledged: Boolean)

    @Query("UPDATE security_alerts SET isDismissed = :isDismissed WHERE id = :id")
    suspend fun updateDismissalStatus(id: String, isDismissed: Boolean)

    @Delete
    suspend fun deleteAlert(alert: SecurityAlertEntity)

    @Query("DELETE FROM security_alerts")
    suspend fun deleteAllAlerts()

    @Query("SELECT COUNT(*) FROM security_alerts")
    suspend fun getAlertsCount(): Int
}