package edu.ucne.smartbudget.presentation.configuracion

data class ConfiguracionUiState(
    val isLoading: Boolean = false,
    val isDarkMode: Boolean = false,
    val pushNotificationsEnabled: Boolean = true,
    val selectedCurrency: String = "USD",
    val errorMessage: String? = null
)
