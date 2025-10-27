package com.singaludra.threat.domain.usecase

import com.singaludra.threat.domain.repository.SecurityAlertRepository
import javax.inject.Inject

class RefreshAlertsUseCase @Inject constructor(
    private val repository: SecurityAlertRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshAlerts()
    }
}