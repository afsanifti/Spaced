package com.example.spaced.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.spaced.ui.theme.DarkThemeConfig
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val DARK_THEME_CONFIG = stringPreferencesKey("dark_theme_config")
        val USE_DYNAMIC_COLOR = booleanPreferencesKey("use_dynamic_color")
        val PALETTE_STYLE = stringPreferencesKey("palette_style")
    }

    val darkThemeConfig: Flow<DarkThemeConfig> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.DARK_THEME_CONFIG] ?: DarkThemeConfig.SYSTEM.name
        runCatching { DarkThemeConfig.valueOf(name) }.getOrDefault(DarkThemeConfig.SYSTEM)
    }

    val useDynamicColor: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USE_DYNAMIC_COLOR] ?: true
    }

    val paletteStyle: Flow<PaletteStyle> = context.dataStore.data.map { preferences ->
        val name = preferences[PreferencesKeys.PALETTE_STYLE] ?: PaletteStyle.TonalSpot.name
        runCatching { PaletteStyle.valueOf(name) }.getOrDefault(PaletteStyle.TonalSpot)
    }

    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.DARK_THEME_CONFIG] = darkThemeConfig.name
        }
    }

    suspend fun setUseDynamicColor(useDynamicColor: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USE_DYNAMIC_COLOR] = useDynamicColor
        }
    }

    suspend fun setPaletteStyle(paletteStyle: PaletteStyle) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.PALETTE_STYLE] = paletteStyle.name
        }
    }
}