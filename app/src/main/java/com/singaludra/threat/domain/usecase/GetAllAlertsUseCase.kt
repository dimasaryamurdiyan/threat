package com.singaludra.threat.domain.usecase

import com.singaludra.threat.domain.model.SecurityAlert
import com.singaludra.threat.domain.repository.SecurityAlertRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAlertsUseCase @Inject constructor(
    private val repository: SecurityAlertRepository
) {
    operator fun invoke(): Flow<List<SecurityAlert>> {
        return repository.getAllAlerts()
    }
}