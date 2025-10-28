package com.singaludra.threat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.singaludra.threat.domain.model.AlertSeverity
import com.singaludra.threat.domain.model.SecurityAlert
import com.singaludra.threat.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AlertListUiState(
    val alerts: List<SecurityAlert> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val filter: AlertFilter = AlertFilter()
)

@HiltViewModel
class AlertListViewModel @Inject constructor(
    private val refreshAlertsUseCase: RefreshAlertsUseCase,
    private val filterAlertsUseCase: FilterAlertsUseCase,
    private val acknowledgeAlertUseCase: AcknowledgeAlertUseCase,
    private val dismissAlertUseCase: DismissAlertUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertListUiState())
    val uiState: StateFlow<AlertListUiState> = _uiState.asStateFlow()

    private val _filter = MutableStateFlow(AlertFilter())

    init {
        observeAlerts()
        refreshAlerts()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeAlerts() {
        viewModelScope.launch {
            _filter
                .flatMapLatest { filter ->
                    filterAlertsUseCase(filter)
                }
                .catch { throwable ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            error = throwable.message,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                }
                .collect { alerts ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            alerts = alerts,
                            isLoading = false,
                            isRefreshing = false,
                            error = null
                        )
                    }
                }
        }
    }

    fun refreshAlerts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true, error = null) }

            refreshAlertsUseCase()
                .onSuccess {
                    _uiState.update { currentState ->
                        currentState.copy(isRefreshing = false)
                    }
                }
                .onFailure { throwable ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            error = throwable.message,
                            isRefreshing = false
                        )
                    }
                }
        }
    }

    fun acknowledgeAlert(alertId: String) {
        viewModelScope.launch {
            acknowledgeAlertUseCase(alertId)
                .onFailure { throwable ->
                    _uiState.update { currentState ->
                        currentState.copy(error = throwable.message)
                    }
                }
        }
    }

    fun dismissAlert(alertId: String) {
        viewModelScope.launch {
            dismissAlertUseCase(alertId)
                .onFailure { throwable ->
                    _uiState.update { currentState ->
                        currentState.copy(error = throwable.message)
                    }
                }
        }
    }

    fun updateFilter(newFilter: AlertFilter) {
        _filter.value = newFilter
        _uiState.update { it.copy(filter = newFilter) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}