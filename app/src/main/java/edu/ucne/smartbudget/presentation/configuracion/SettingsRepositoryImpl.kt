package edu.ucne.smartbudget.data.repository

import edu.ucne.smartbudget.data.local.datastore.SessionDataStore
import edu.ucne.smartbudget.presentation.configuracion.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val sessionDataStore: SessionDataStore
) : SettingsRepository {

    override fun getThemeMode(): Flow<Boolean> = sessionDataStore.isDarkModeFlow
    override fun getNotificationsEnabled(): Flow<Boolean> = sessionDataStore.notificationsFlow
    override fun getCurrency(): Flow<String> = sessionDataStore.currencyFlow

    override suspend fun setThemeMode(enabled: Boolean) = sessionDataStore.setDarkMode(enabled)
    override suspend fun setNotificationsEnabled(enabled: Boolean) = sessionDataStore.setNotifications(enabled)
    override suspend fun setCurrency(currency: String) = sessionDataStore.setCurrency(currency)
}
