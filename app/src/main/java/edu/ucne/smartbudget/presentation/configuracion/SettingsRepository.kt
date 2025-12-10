package edu.ucne.smartbudget.presentation.configuracion

import kotlinx.coroutines.flow.Flow


interface SettingsRepository {
    fun getThemeMode(): Flow<Boolean>
    fun getNotificationsEnabled(): Flow<Boolean>
    fun getCurrency(): Flow<String>
    suspend fun setThemeMode(enabled: Boolean)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setCurrency(currency: String)
}
