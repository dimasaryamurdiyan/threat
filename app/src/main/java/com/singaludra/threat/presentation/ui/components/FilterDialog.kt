package com.singaludra.threat.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.singaludra.threat.domain.model.AlertSeverity
import com.singaludra.threat.domain.usecase.AlertFilter

@Composable
fun FilterDialog(
    currentFilter: AlertFilter,
    onFilterChange: (AlertFilter) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedSeverity by remember { mutableStateOf(currentFilter.severity) }
    var threatTypeText by remember { mutableStateOf(currentFilter.threatType ?: "") }
    var showAcknowledged by remember { mutableStateOf(currentFilter.showAcknowledged) }
    var showDismissed by remember { mutableStateOf(currentFilter.showDismissed) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Alerts") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Severity Filter
                Text("Severity", style = MaterialTheme.typography.titleSmall)
                Column(Modifier.selectableGroup()) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedSeverity == null,
                                onClick = { selectedSeverity = null },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedSeverity == null,
                            onClick = null
                        )
                        Text(
                            text = "All",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }

                    AlertSeverity.entries.forEach { severity ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = selectedSeverity == severity,
                                    onClick = { selectedSeverity = severity },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSeverity == severity,
                                onClick = null
                            )
                            Text(
                                text = severity.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }

                // Threat Type Filter
                Text("Threat Type", style = MaterialTheme.typography.titleSmall)
                OutlinedTextField(
                    value = threatTypeText,
                    onValueChange = { threatTypeText = it },
                    label = { Text("Search threat type") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Status Filters
                Text("Status", style = MaterialTheme.typography.titleSmall)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showAcknowledged,
                            onCheckedChange = { showAcknowledged = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                checkmarkColor = androidx.compose.ui.graphics.Color.White
                            )
                        )
                        Text(
                            text = "Show Acknowledged",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = showDismissed,
                            onCheckedChange = { showDismissed = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = androidx.compose.ui.graphics.Color(0xFF757575),
                                checkmarkColor = androidx.compose.ui.graphics.Color.White
                            )
                        )
                        Text(
                            text = "Show Dismissed",
                            modifier = Modifier.padding(start = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onFilterChange(
                        AlertFilter(
                            severity = selectedSeverity,
                            threatType = threatTypeText.takeIf { it.isNotBlank() },
                            showAcknowledged = showAcknowledged,
                            showDismissed = showDismissed
                        )
                    )
                }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}