package com.singaludra.threat.presentation.ui.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.singaludra.threat.domain.model.AlertSeverity
import com.singaludra.threat.domain.model.SecurityAlert
import com.singaludra.threat.ui.theme.*
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertCard(
    alert: SecurityAlert,
    onCardClick: () -> Unit,
    onAcknowledge: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onCardClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                alert.isDismissed -> MaterialTheme.colorScheme.surfaceVariant
                alert.isAcknowledged -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SeverityChip(severity = alert.severity)
                        if (alert.isAcknowledged) {
                            AssistChip(
                                onClick = { },
                                label = { Text("Acknowledged", fontSize = 12.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = AcknowledgedColor.copy(alpha = 0.2f),
                                    labelColor = AcknowledgedColor
                                )
                            )
                        }
                        if (alert.isDismissed) {
                            AssistChip(
                                onClick = { },
                                label = { Text("Dismissed", fontSize = 12.sp) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = DismissedColor.copy(alpha = 0.2f),
                                    labelColor = DismissedColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = alert.threatType,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Source: ${alert.source}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = alert.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (!alert.isAcknowledged && !alert.isDismissed) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onAcknowledge,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Acknowledge")
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

@Composable
fun SeverityChip(severity: AlertSeverity) {
    val (color, textColor) = when (severity) {
        AlertSeverity.CRITICAL -> CriticalColor to Color.White
        AlertSeverity.HIGH -> HighColor to Color.White
        AlertSeverity.MEDIUM -> MediumColor to Color.Black
        AlertSeverity.LOW -> LowColor to Color.White
    }

    AssistChip(
        onClick = { },
        label = {
            Text(
                text = severity.displayName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color,
            labelColor = textColor
        )
    )
}