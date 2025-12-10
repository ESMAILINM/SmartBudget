package edu.ucne.smartbudget.data.local.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("session_prefs")

class SessionDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val KEY_USER_ID = stringPreferencesKey("key_user_id")
        private val KEY_DARK_MODE = booleanPreferencesKey("key_dark_mode")
        private val KEY_NOTIFICATIONS = booleanPreferencesKey("key_notifications")
        private val KEY_CURRENCY = stringPreferencesKey("key_currency")
    }

    val userIdFlow: Flow<String?> = context.dataStore.data
        .map { prefs -> prefs[KEY_USER_ID] }

    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { prefs -> prefs[KEY_USER_ID] = userId }
    }

    suspend fun clearUserId() {
        context.dataStore.edit { prefs -> prefs.remove(KEY_USER_ID) }
    }

    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_DARK_MODE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_DARK_MODE] = enabled }
    }

    val notificationsFlow: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[KEY_NOTIFICATIONS] ?: true }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_NOTIFICATIONS] = enabled }
    }

    val currencyFlow: Flow<String> = context.dataStore.data
        .map { prefs -> prefs[KEY_CURRENCY] ?: "USD" }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { prefs -> prefs[KEY_CURRENCY] = currency }
    }
}
