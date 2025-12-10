package edu.ucne.smartbudget.presentation.dashboardScreen.Screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.smartbudget.data.local.datastore.SessionDataStore
import edu.ucne.smartbudget.presentation.dashboardScreen.GetDashboardDataUseCase
import edu.ucne.smartbudget.presentation.dashboardScreen.model.DashboardUiData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    val selectedCurrency = sessionDataStore.currencyFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = "USD"
        )

    init {
        viewModelScope.launch {
            sessionDataStore.userIdFlow.collectLatest { userId ->
                if (!userId.isNullOrBlank()) {
                    observeDashboard(userId)
                } else {
                    _state.update { HomeUiState(isLoading = false) }
                }
            }
        }
    }

    private fun observeDashboard(userId: String) {
        viewModelScope.launch {
            getDashboardDataUseCase(userId)
                .onStart {
                    _state.update { it.copy(isLoading = true) }
                }
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Error desconocido"
                        )
                    }
                }
                .collect { uiData: DashboardUiData ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            summary = uiData.summary,
                            trend = uiData.trend,
                            breakdown = uiData.breakdown,
                            recentTransactions = uiData.recentTransactions,
                            error = null
                        )
                    }
                }
        }
    }

    fun onIntent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.ViewGoals -> {}
            HomeUiEvent.ViewReports -> {}
            is HomeUiEvent.OpenAddTransaction -> {}
            is HomeUiEvent.ViewAllTransactions -> {}
        }
    }
}
