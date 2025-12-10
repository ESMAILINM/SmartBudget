package edu.ucne.smartbudget.presentation.configuracion

sealed interface ConfiguracionUiEvent {
    data object OnClose : ConfiguracionUiEvent
    data object OnManageCategories : ConfiguracionUiEvent
    data object OnManageAccount : ConfiguracionUiEvent
    data object OnLogout : ConfiguracionUiEvent
    data class OnToggleDarkMode(val enabled: Boolean) : ConfiguracionUiEvent
    data class OnTogglePushNotifications(val enabled: Boolean) : ConfiguracionUiEvent
    data class OnCurrencyChanged(val currency: String) : ConfiguracionUiEvent
}
