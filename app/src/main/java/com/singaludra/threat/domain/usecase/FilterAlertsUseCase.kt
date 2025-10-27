package com.singaludra.threat.domain.usecase

import com.singaludra.threat.domain.model.AlertSeverity
import com.singaludra.threat.domain.model.SecurityAlert
import com.singaludra.threat.domain.repository.SecurityAlertRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class AlertFilter(
    val severity: AlertSeverity? = null,
    val threatType: String? = null,
    val showAcknowledged: Boolean = true,
    val showDismissed: Boolean = false
)

class FilterAlertsUseCase @Inject constructor(
    private val repository: SecurityAlertRepository
) {
    operator fun invoke(filter: AlertFilter): Flow<List<SecurityAlert>> {
        val baseFlow = repository.getAllAlerts()

        return baseFlow.combine(
            repository.getActiveAlerts()
        ) { allAlerts, activeAlerts ->
            var filteredAlerts = if (filter.showDismissed) allAlerts else activeAlerts

            filter.severity?.let { severity ->
                filteredAlerts = filteredAlerts.filter { it.severity == severity }
            }

            filter.threatType?.let { threatType ->
                if (threatType.isNotBlank()) {
                    filteredAlerts = filteredAlerts.filter {
                        it.threatType.contains(threatType, ignoreCase = true)
                    }
                }
            }

            if (!filter.showAcknowledged) {
                filteredAlerts = filteredAlerts.filter { !it.isAcknowledged }
            }

            filteredAlerts
        }
    }
}