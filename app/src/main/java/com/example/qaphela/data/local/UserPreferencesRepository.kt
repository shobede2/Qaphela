package com.example.qaphela.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        val KEY_HISTORY_ENABLED = booleanPreferencesKey("history_enabled")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_PREFERRED_LANGUAGE = stringPreferencesKey("preferred_language")
        val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val KEY_ELDER_MODE_ENABLED = booleanPreferencesKey("elder_mode_enabled")
        val KEY_TTS_SPEECH_RATE = floatPreferencesKey("tts_speech_rate")
        val KEY_APP_THEME_MODE = stringPreferencesKey("app_theme_mode") // "SYSTEM", "LIGHT", "DARK"
        val KEY_LOGGED_IN_USER_ID = longPreferencesKey("logged_in_user_id")
        val KEY_LOGGED_IN_USER_NAME = stringPreferencesKey("logged_in_user_name")
        val KEY_LOGGED_IN_USER_EMAIL = stringPreferencesKey("logged_in_user_email")
    }

    val historyEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_HISTORY_ENABLED] ?: true
    }

    val onboardingCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val preferredLanguageFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_PREFERRED_LANGUAGE] ?: "en"
    }

    val notificationsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATIONS_ENABLED] ?: true
    }

    val elderModeEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        val systemScale = context.resources.configuration.fontScale
        preferences[KEY_ELDER_MODE_ENABLED] ?: (systemScale >= 1.2f)
    }

    val ttsSpeechRateFlow: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[KEY_TTS_SPEECH_RATE] ?: 1.0f
    }

    val appThemeModeFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_APP_THEME_MODE] ?: "SYSTEM"
    }

    val loggedInUserIdFlow: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_LOGGED_IN_USER_ID] ?: -1L
    }

    val loggedInUserNameFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_LOGGED_IN_USER_NAME] ?: "Guest"
    }

    val loggedInUserEmailFlow: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_LOGGED_IN_USER_EMAIL] ?: "guest@qaphela.org.za"
    }

    suspend fun setHistoryEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_HISTORY_ENABLED] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setPreferredLanguage(languageCode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PREFERRED_LANGUAGE] = languageCode
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENABLED] = enabled
        }
    }

    suspend fun setElderModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ELDER_MODE_ENABLED] = enabled
        }
    }

    suspend fun setTtsSpeechRate(rate: Float) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TTS_SPEECH_RATE] = rate
        }
    }

    suspend fun setAppThemeMode(themeMode: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_APP_THEME_MODE] = themeMode
        }
    }

    suspend fun saveUserSession(userId: Long, userName: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOGGED_IN_USER_ID] = userId
            preferences[KEY_LOGGED_IN_USER_NAME] = userName
            preferences[KEY_LOGGED_IN_USER_EMAIL] = email
        }
    }

    suspend fun clearUserSession() {
        context.dataStore.edit { preferences ->
            preferences[KEY_LOGGED_IN_USER_ID] = -1L
            preferences[KEY_LOGGED_IN_USER_NAME] = "Guest"
            preferences[KEY_LOGGED_IN_USER_EMAIL] = "guest@qaphela.org.za"
        }
    }
}
