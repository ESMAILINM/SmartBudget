package edu.ucne.smartbudget.presentation.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    getThemeModeUseCase: GetThemeModeUseCase,
    getNotificationsUseCase: GetNotificationsUseCase,
    getCurrencyUseCase: GetCurrencyUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val setNotificationsUseCase: SetNotificationsUseCase,
    private val setCurrencyUseCase: SetCurrencyUseCase
) : ViewModel() {

    val state = combine(
        getThemeModeUseCase(),
        getNotificationsUseCase(),
        getCurrencyUseCase()
    ) { darkMode, notifications, currency ->
        ConfiguracionUiState(
            isDarkMode = darkMode,
            pushNotificationsEnabled = notifications,
            selectedCurrency = currency,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ConfiguracionUiState(isLoading = true)
    )

    fun onEvent(event: ConfiguracionUiEvent) {
        viewModelScope.launch {
            when (event) {
                is ConfiguracionUiEvent.OnToggleDarkMode -> {
                    setThemeModeUseCase(event.enabled)
                }
                is ConfiguracionUiEvent.OnTogglePushNotifications -> {
                    setNotificationsUseCase(event.enabled)
                }
                is ConfiguracionUiEvent.OnCurrencyChanged -> {
                    setCurrencyUseCase(event.currency)
                }
                else -> {}
            }
        }
    }
}
