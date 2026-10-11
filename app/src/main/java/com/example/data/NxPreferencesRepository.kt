package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.NxThemeOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.nxDataStore: DataStore<Preferences> by preferencesDataStore(name = "nxpanel_preferences")

data class NxAppPreferences(
    val themeId: String = NxThemeOption.DEFAULT.id,
    val backgroundId: String = "GRAD_THEME",
    val baseUrl: String = "http://127.0.0.1:8080",
    val hapticEnabled: Boolean = true,
    val soundEnabled: Boolean = true
)

class NxPreferencesRepository(private val context: Context) {

    companion object {
        private val KEY_THEME_ID = stringPreferencesKey("theme_id")
        private val KEY_BACKGROUND_ID = stringPreferencesKey("background_id")
        private val KEY_BASE_URL = stringPreferencesKey("base_url")
        private val KEY_HAPTIC_ENABLED = booleanPreferencesKey("haptic_enabled")
        private val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        const val DEFAULT_BASE_URL = "http://127.0.0.1:8080"
    }

    val preferencesFlow: Flow<NxAppPreferences> = context.nxDataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            try {
                NxAppPreferences(
                    themeId = prefs[KEY_THEME_ID] ?: NxThemeOption.DEFAULT.id,
                    backgroundId = prefs[KEY_BACKGROUND_ID] ?: "GRAD_THEME",
                    baseUrl = prefs[KEY_BASE_URL]?.ifBlank { DEFAULT_BASE_URL } ?: DEFAULT_BASE_URL,
                    hapticEnabled = prefs[KEY_HAPTIC_ENABLED] ?: true,
                    soundEnabled = prefs[KEY_SOUND_ENABLED] ?: true
                )
            } catch (e: Exception) {
                NxAppPreferences()
            }
        }

    suspend fun setTheme(themeId: String) {
        try {
            context.nxDataStore.edit { prefs ->
                prefs[KEY_THEME_ID] = themeId
            }
        } catch (_: Exception) {
        }
    }

    suspend fun setBackground(backgroundId: String) {
        try {
            context.nxDataStore.edit { prefs ->
                prefs[KEY_BACKGROUND_ID] = backgroundId
            }
        } catch (_: Exception) {
        }
    }

    suspend fun setBaseUrl(url: String) {
        try {
            val sanitized = url.trim().removeSuffix("/").ifBlank { DEFAULT_BASE_URL }
            context.nxDataStore.edit { prefs ->
                prefs[KEY_BASE_URL] = sanitized
            }
        } catch (_: Exception) {
        }
    }

    suspend fun setHapticEnabled(enabled: Boolean) {
        try {
            context.nxDataStore.edit { prefs ->
                prefs[KEY_HAPTIC_ENABLED] = enabled
            }
        } catch (_: Exception) {
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        try {
            context.nxDataStore.edit { prefs ->
                prefs[KEY_SOUND_ENABLED] = enabled
            }
        } catch (_: Exception) {
        }
    }
}
