package com.singaludra.threat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singaludra.threat.domain.model.SecurityAlert
import com.singaludra.threat.domain.repository.SecurityAlertRepository
import com.singaludra.threat.domain.usecase.AcknowledgeAlertUseCase
import com.singaludra.threat.domain.usecase.DismissAlertUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertDetailUiState(
    val alert: SecurityAlert? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AlertDetailViewModel @Inject constructor(
    private val repository: SecurityAlertRepository,
    private val acknowledgeAlertUseCase: AcknowledgeAlertUseCase,
    private val dismissAlertUseCase: DismissAlertUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertDetailUiState())
    val uiState: StateFlow<AlertDetailUiState> = _uiState.asStateFlow()

    fun loadAlert(alertId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val alert = repository.getAlertById(alertId)
                _uiState.update { currentState ->
                    currentState.copy(
                        alert = alert,
                        isLoading = false,
                        error = if (alert == null) "Alert not found" else null
                    )
                }
            } catch (throwable: Throwable) {
                _uiState.update { currentState ->
                    currentState.copy(
                        error = throwable.message,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun acknowledgeAlert() {
        val alertId = _uiState.value.alert?.id ?: return
        viewModelScope.launch {
            acknowledgeAlertUseCase(alertId)
                .onSuccess {
                    loadAlert(alertId) // Reload to get updated state
                }
                .onFailure { throwable ->
                    _uiState.update { currentState ->
                        currentState.copy(error = throwable.message)
                    }
                }
        }
    }

    fun dismissAlert() {
        val alertId = _uiState.value.alert?.id ?: return
        viewModelScope.launch {
            dismissAlertUseCase(alertId)
                .onSuccess {
                    loadAlert(alertId) // Reload to get updated state
                }
                .onFailure { throwable ->
                    _uiState.update { currentState ->
                        currentState.copy(error = throwable.message)
                    }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}