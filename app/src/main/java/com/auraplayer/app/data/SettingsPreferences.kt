package com.auraplayer.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
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
    val antiClippingEnabled: Boolean = true,
    val blacklistedFolders: Set<String> = emptySet(),
    val homeRailOrder: List<String> = listOf("CONTINUE_LISTENING", "MADE_FOR_YOU", "MOST_PLAYED_ARTISTS", "RECENTLY_ADDED", "ON_REPEAT"),
    val hiddenRails: Set<String> = emptySet(),
    val songsSortOrder: String = "TITLE_ASC",
    val songsViewMode: String = "LIST"
)

class SettingsPreferences(private val dataStore: DataStore<Preferences>) {

    constructor(context: Context) : this(context.dataStore)

    companion object {
        private val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        private val KEY_DYNAMIC_COLOR = booleanPreferencesKey("dynamic_color")
        private val KEY_REPLAYGAIN_LUFS = floatPreferencesKey("replaygain_lufs")
        private val KEY_ANTI_CLIPPING = booleanPreferencesKey("anti_clipping")
        private val KEY_BLACKLISTED_FOLDERS = stringSetPreferencesKey("blacklisted_folders")
        private val KEY_HOME_RAIL_ORDER = stringPreferencesKey("home_rail_order")
        private val KEY_HIDDEN_RAILS = stringSetPreferencesKey("hidden_rails")
        private val KEY_SONGS_SORT_ORDER = stringPreferencesKey("songs_sort_order")
        private val KEY_SONGS_VIEW_MODE = stringPreferencesKey("songs_view_mode")
    }

    val settingsFlow: Flow<AppSettings> = dataStore.data.map { prefs ->
        val themeStr = prefs[KEY_THEME_MODE] ?: ThemeMode.SYSTEM.name
        val themeMode = try { ThemeMode.valueOf(themeStr) } catch (e: Exception) { ThemeMode.SYSTEM }
        val dynamicColor = prefs[KEY_DYNAMIC_COLOR] ?: true
        val lufs = prefs[KEY_REPLAYGAIN_LUFS] ?: -18f
        val antiClipping = prefs[KEY_ANTI_CLIPPING] ?: true
        val blacklist = prefs[KEY_BLACKLISTED_FOLDERS] ?: emptySet()

        val railOrderStr = prefs[KEY_HOME_RAIL_ORDER] ?: "CONTINUE_LISTENING,MADE_FOR_YOU,MOST_PLAYED_ARTISTS,RECENTLY_ADDED,ON_REPEAT"
        val railOrder = railOrderStr.split(",").filter { it.isNotBlank() }
        val hiddenRails = prefs[KEY_HIDDEN_RAILS] ?: emptySet()

        val songsSort = prefs[KEY_SONGS_SORT_ORDER] ?: "TITLE_ASC"
        val songsView = prefs[KEY_SONGS_VIEW_MODE] ?: "LIST"

        AppSettings(
            themeMode = themeMode,
            dynamicColor = dynamicColor,
            replayGainTargetLufs = lufs,
            antiClippingEnabled = antiClipping,
            blacklistedFolders = blacklist,
            homeRailOrder = railOrder,
            hiddenRails = hiddenRails,
            songsSortOrder = songsSort,
            songsViewMode = songsView
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { prefs ->
            prefs[KEY_THEME_MODE] = mode.name
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_DYNAMIC_COLOR] = enabled
        }
    }

    suspend fun setReplayGainTargetLufs(lufs: Float) {
        dataStore.edit { prefs ->
            prefs[KEY_REPLAYGAIN_LUFS] = lufs
        }
    }

    suspend fun setAntiClippingEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEY_ANTI_CLIPPING] = enabled
        }
    }

    suspend fun addBlacklistedFolder(folderPath: String) {
        if (folderPath.isBlank()) return
        val normalized = folderPath.trim()
        dataStore.edit { prefs ->
            val current = prefs[KEY_BLACKLISTED_FOLDERS] ?: emptySet()
            prefs[KEY_BLACKLISTED_FOLDERS] = current + normalized
        }
    }

    suspend fun removeBlacklistedFolder(folderPath: String) {
        dataStore.edit { prefs ->
            val current = prefs[KEY_BLACKLISTED_FOLDERS] ?: emptySet()
            prefs[KEY_BLACKLISTED_FOLDERS] = current - folderPath
        }
    }

    suspend fun setHomeRailOrder(order: List<String>) {
        dataStore.edit { prefs ->
            prefs[KEY_HOME_RAIL_ORDER] = order.joinToString(",")
        }
    }

    suspend fun setHiddenRails(hidden: Set<String>) {
        dataStore.edit { prefs ->
            prefs[KEY_HIDDEN_RAILS] = hidden
        }
    }

    suspend fun setSongsSortOrder(sortOrder: String) {
        dataStore.edit { prefs ->
            prefs[KEY_SONGS_SORT_ORDER] = sortOrder
        }
    }

    suspend fun setSongsViewMode(viewMode: String) {
        dataStore.edit { prefs ->
            prefs[KEY_SONGS_VIEW_MODE] = viewMode
        }
    }
}
