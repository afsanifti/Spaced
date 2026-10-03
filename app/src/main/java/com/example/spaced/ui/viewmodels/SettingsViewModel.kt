package com.example.spaced.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaced.data.repository.SettingsRepository
import com.example.spaced.ui.theme.DarkThemeConfig
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)

    val darkThemeConfig: StateFlow<DarkThemeConfig> = repository.darkThemeConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DarkThemeConfig.SYSTEM
        )

    val useDynamicColor: StateFlow<Boolean> = repository.useDynamicColor
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    val paletteStyle: StateFlow<PaletteStyle> = repository.paletteStyle
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PaletteStyle.TonalSpot
        )

    fun setDarkThemeConfig(config: DarkThemeConfig) {
        viewModelScope.launch {
            repository.setDarkThemeConfig(config)
        }
    }

    fun setUseDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            repository.setUseDynamicColor(enabled)
        }
    }

    fun setPaletteStyle(style: PaletteStyle) {
        viewModelScope.launch {
            repository.setPaletteStyle(style)
        }
    }
}