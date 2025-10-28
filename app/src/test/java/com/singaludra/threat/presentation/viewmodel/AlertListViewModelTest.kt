package com.singaludra.threat.presentation.viewmodel

import com.singaludra.threat.domain.model.AlertSeverity
import com.singaludra.threat.domain.model.SecurityAlert
import com.singaludra.threat.domain.usecase.*
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class AlertListViewModelTest {

    private lateinit var refreshAlertsUseCase: RefreshAlertsUseCase
    private lateinit var filterAlertsUseCase: FilterAlertsUseCase
    private lateinit var acknowledgeAlertUseCase: AcknowledgeAlertUseCase
    private lateinit var dismissAlertUseCase: DismissAlertUseCase
    private lateinit var viewModel: AlertListViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    private val sampleAlerts = listOf(
        SecurityAlert(
            id = "alert-001",
            severity = AlertSeverity.HIGH,
            source = "auth.log",
            threatType = "Brute Force Attack",
            timestamp = LocalDateTime.now(),
            isAcknowledged = false,
            isDismissed = false
        ),
        SecurityAlert(
            id = "alert-002",
            severity = AlertSeverity.CRITICAL,
            source = "endpoint_agent",
            threatType = "Malware Detection",
            timestamp = LocalDateTime.now(),
            isAcknowledged = true,
            isDismissed = false
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        refreshAlertsUseCase = mockk()
        filterAlertsUseCase = mockk()
        acknowledgeAlertUseCase = mockk()
        dismissAlertUseCase = mockk()

        every { filterAlertsUseCase(any()) } returns flowOf(sampleAlerts)
        coEvery { refreshAlertsUseCase() } returns Result.success(Unit)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `viewModel initializes with loading state and loads alerts`() = runTest {
        // Given
        every { filterAlertsUseCase(any()) } returns flowOf(sampleAlerts)

        // When
        viewModel = AlertListViewModel(
            refreshAlertsUseCase = refreshAlertsUseCase,
            filterAlertsUseCase = filterAlertsUseCase,
            acknowledgeAlertUseCase = acknowledgeAlertUseCase,
            dismissAlertUseCase = dismissAlertUseCase
        )

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(sampleAlerts, uiState.alerts)
        assertFalse(uiState.isLoading)
        assertNull(uiState.error)
    }

    @Test
    fun `refreshAlerts calls refresh use case and updates state`() = runTest {
        // Given
        every { filterAlertsUseCase(any()) } returns flowOf(sampleAlerts)
        coEvery { refreshAlertsUseCase() } returns Result.success(Unit)

        viewModel = AlertListViewModel(
            refreshAlertsUseCase = refreshAlertsUseCase,
            filterAlertsUseCase = filterAlertsUseCase,
            acknowledgeAlertUseCase = acknowledgeAlertUseCase,
            dismissAlertUseCase = dismissAlertUseCase
        )

        // When
        viewModel.refreshAlerts()

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Then
        coVerify { refreshAlertsUseCase() }
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    @Test
    fun `refreshAlerts handles error correctly`() = runTest {
        // Given
        val errorMessage = "Network error"
        every { filterAlertsUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshAlertsUseCase() } returns Result.failure(Exception(errorMessage))

        viewModel = AlertListViewModel(
            refreshAlertsUseCase = refreshAlertsUseCase,
            filterAlertsUseCase = filterAlertsUseCase,
            acknowledgeAlertUseCase = acknowledgeAlertUseCase,
            dismissAlertUseCase = dismissAlertUseCase
        )

        // When
        viewModel.refreshAlerts()

        // Wait for coroutines to complete
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertEquals(errorMessage, uiState.error)
        assertFalse(uiState.isRefreshing)
    }

    @Test
    fun `acknowledgeAlert calls acknowledge use case`() = runTest {
        // Given
        val alertId = "alert-001"
        every { filterAlertsUseCase(any()) } returns flowOf(sampleAlerts)
        coEvery { acknowledgeAlertUseCase(alertId) } returns Result.success(Unit)

        viewModel = AlertListViewModel(
            refreshAlertsUseCase = refreshAlertsUseCase,
            filterAlertsUseCase = filterAlertsUseCase,
            acknowledgeAlertUseCase = acknowledgeAlertUseCase,
            dismissAlertUseCase = dismissAlertUseCase
        )

        // When
        viewModel.acknowledgeAlert(alertId)

        // Then
        coVerify { acknowledgeAlertUseCase(alertId) }
    }

    @Test
    fun `dismissAlert calls dismiss use case`() = runTest {
        // Given
        val alertId = "alert-001"
        every { filterAlertsUseCase(any()) } returns flowOf(sampleAlerts)
        coEvery { dismissAlertUseCase(alertId) } returns Result.success(Unit)

        viewModel = AlertListViewModel(
            refreshAlertsUseCase = refreshAlertsUseCase,
            filterAlertsUseCase = filterAlertsUseCase,
            acknowledgeAlertUseCase = acknowledgeAlertUseCase,
            dismissAlertUseCase = dismissAlertUseCase
        )

        // When
        viewModel.dismissAlert(alertId)

        // Then
        coVerify { dismissAlertUseCase(alertId) }
    }

    @Test
    fun `updateFilter updates filter state and calls filter use case`() = runTest {
        // Given
        val newFilter = AlertFilter(severity = AlertSeverity.HIGH)
        every { filterAlertsUseCase(any()) } returns flowOf(sampleAlerts)

        viewModel = AlertListViewModel(
            refreshAlertsUseCase = refreshAlertsUseCase,
            filterAlertsUseCase = filterAlertsUseCase,
            acknowledgeAlertUseCase = acknowledgeAlertUseCase,
            dismissAlertUseCase = dismissAlertUseCase
        )

        // When
        viewModel.updateFilter(newFilter)

        // Then
        assertEquals(newFilter, viewModel.uiState.value.filter)
        verify { filterAlertsUseCase(newFilter) }
    }

    @Test
    fun `clearError clears error state`() = runTest {
        // Given
        every { filterAlertsUseCase(any()) } returns flowOf(emptyList())
        coEvery { refreshAlertsUseCase() } returns Result.failure(Exception("Error"))

        viewModel = AlertListViewModel(
            refreshAlertsUseCase = refreshAlertsUseCase,
            filterAlertsUseCase = filterAlertsUseCase,
            acknowledgeAlertUseCase = acknowledgeAlertUseCase,
            dismissAlertUseCase = dismissAlertUseCase
        )

        viewModel.refreshAlerts()
        assertNotNull(viewModel.uiState.value.error)

        // When
        viewModel.clearError()

        // Then
        assertNull(viewModel.uiState.value.error)
    }
}