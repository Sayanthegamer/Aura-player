package com.auraplayer.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "aura_settings")

enum class ThemeMode {
    SYSTEM, DARK, LIGHT
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val replayGainTargetLufs: Float = -18f,
    val antiClippingEnabled: Boolean = true
)

class SettingsPreferences(private val context: Context) {

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val KEY_REPLAYGAIN_LUFS = floatPreferencesKey("replaygain_lufs")
        private val KEY_ANTI_CLIPPING = booleanPreferencesKey("anti_clipping")
    }

    val settingsFlow: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[KEY_THEME_MODE] ?: ThemeMode.SYSTEM.name
        val themeMode = try { ThemeMode.valueOf(themeStr) } catch (e: Exception) { ThemeMode.SYSTEM }
        val dynamicColor = prefs[KEY_DYNAMIC_COLOR] ?: true
        val lufs = prefs[KEY_REPLAYGAIN_LUFS] ?: -18f
        val antiClipping = prefs[KEY_ANTI_CLIPPING] ?: true

        AppSettings(
            themeMode = themeMode,
            dynamicColor = dynamicColor,
            replayGainTargetLufs = lufs,
            antiClippingEnabled = antiClipping
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode.name
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun setReplayGainTargetLufs(lufs: Float) {
        context.dataStore.edit { prefs ->
            prefs[KEY_REPLAYGAIN_LUFS] = lufs
        }
    }

    suspend fun setAntiClippingEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ANTI_CLIPPING] = enabled
        }
    }
}
