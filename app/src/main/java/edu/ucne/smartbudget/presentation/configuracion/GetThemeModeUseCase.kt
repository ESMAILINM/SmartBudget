package edu.ucne.smartbudget.presentation.configuracion

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeModeUseCase @Inject constructor(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<Boolean> = repo.getThemeMode()
}

class GetNotificationsUseCase @Inject constructor(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<Boolean> = repo.getNotificationsEnabled()
}

class GetCurrencyUseCase @Inject constructor(private val repo: SettingsRepository) {
    operator fun invoke(): Flow<String> = repo.getCurrency()
}
class SetThemeModeUseCase @Inject constructor(private val repo: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) = repo.setThemeMode(enabled)
}

class SetNotificationsUseCase @Inject constructor(private val repo: SettingsRepository) {
    suspend operator fun invoke(enabled: Boolean) = repo.setNotificationsEnabled(enabled)
}

class SetCurrencyUseCase @Inject constructor(private val repo: SettingsRepository) {
    suspend operator fun invoke(currency: String) = repo.setCurrency(currency)
}
