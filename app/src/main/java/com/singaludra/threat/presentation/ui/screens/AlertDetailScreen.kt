package com.singaludra.threat.presentation.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.singaludra.threat.presentation.ui.components.SeverityChip
import com.singaludra.threat.ui.theme.AcknowledgedColor
import com.singaludra.threat.ui.theme.DismissedColor
import com.singaludra.threat.presentation.viewmodel.AlertDetailViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertDetailScreen(
    alertId: String,
    onNavigateBack: () -> Unit,
    viewModel: AlertDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(alertId) {
        viewModel.loadAlert(alertId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alert Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                uiState.alert == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Alert not found",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            TextButton(onClick = onNavigateBack) {
                                Text("Go Back")
                            }
                        }
                    }
                }

                else -> {
                    val alert = uiState.alert!!
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Header Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    SeverityChip(severity = alert.severity)
                                    if (alert.isAcknowledged) {
                                        AssistChip(
                                            onClick = { },
                                            label = { Text("Acknowledged") },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = AcknowledgedColor.copy(alpha = 0.2f),
                                                labelColor = AcknowledgedColor
                                            )
                                        )
                                    }
                                    if (alert.isDismissed) {
                                        AssistChip(
                                            onClick = { },
                                            label = { Text("Dismissed") },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = DismissedColor.copy(alpha = 0.2f),
                                                labelColor = DismissedColor
                                            )
                                        )
                                    }
                                }

                                Text(
                                    text = alert.threatType,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "Details",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                DetailRow("Alert ID", alert.id)
                                DetailRow("Source", alert.source)
                                DetailRow("Severity", alert.severity.displayName)
                                DetailRow("Threat Type", alert.threatType)
                                DetailRow(
                                    "Timestamp",
                                    alert.timestamp.format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss"))
                                )
                                DetailRow("Status", when {
                                    alert.isDismissed -> "Dismissed"
                                    alert.isAcknowledged -> "Acknowledged"
                                    else -> "Active"
                                })
                            }
                        }

                        // Action Buttons
                        if (!alert.isAcknowledged && !alert.isDismissed) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.acknowledgeAlert() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Acknowledge")
                                }

                                OutlinedButton(
                                    onClick = { viewModel.dismissAlert() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Dismiss")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            // Show error message
            viewModel.clearError()
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(2f)
        )
    }
}