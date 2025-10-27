package com.singaludra.threat.domain.usecase

import com.singaludra.threat.domain.repository.SecurityAlertRepository
import javax.inject.Inject

class DismissAlertUseCase @Inject constructor(
    private val repository: SecurityAlertRepository
) {
    suspend operator fun invoke(alertId: String): Result<Unit> {
        return repository.dismissAlert(alertId)
    }
}