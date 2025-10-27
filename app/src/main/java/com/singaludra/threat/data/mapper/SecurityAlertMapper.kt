package com.singaludra.threat.data.mapper

import com.singaludra.threat.data.local.entity.SecurityAlertEntity
import com.singaludra.threat.data.remote.dto.SecurityAlertDto
import com.singaludra.threat.domain.model.AlertSeverity
import com.singaludra.threat.domain.model.SecurityAlert
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object SecurityAlertMapper {

    private val dateFormatter = DateTimeFormatter.ISO_DATE_TIME

    fun dtoToDomain(dto: SecurityAlertDto): SecurityAlert {
        return SecurityAlert(
            id = dto.id,
            severity = AlertSeverity.fromString(dto.severity),
            source = dto.source,
            threatType = dto.threatType,
            timestamp = LocalDateTime.parse(dto.timestamp, dateFormatter)
        )
    }

    fun entityToDomain(entity: SecurityAlertEntity): SecurityAlert {
        return SecurityAlert(
            id = entity.id,
            severity = AlertSeverity.fromString(entity.severity),
            source = entity.source,
            threatType = entity.threatType,
            timestamp = LocalDateTime.parse(entity.timestamp, dateFormatter),
            isAcknowledged = entity.isAcknowledged,
            isDismissed = entity.isDismissed
        )
    }

    fun domainToEntity(domain: SecurityAlert): SecurityAlertEntity {
        return SecurityAlertEntity(
            id = domain.id,
            severity = domain.severity.value,
            source = domain.source,
            threatType = domain.threatType,
            timestamp = domain.timestamp.format(dateFormatter),
            isAcknowledged = domain.isAcknowledged,
            isDismissed = domain.isDismissed
        )
    }

    fun dtoToEntity(dto: SecurityAlertDto): SecurityAlertEntity {
        return SecurityAlertEntity(
            id = dto.id,
            severity = dto.severity,
            source = dto.source,
            threatType = dto.threatType,
            timestamp = dto.timestamp
        )
    }
}