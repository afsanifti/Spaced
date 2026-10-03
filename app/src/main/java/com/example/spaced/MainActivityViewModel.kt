package com.example.spaced

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.spaced.data.model.UserSettings
import com.example.spaced.data.repository.SettingsRepository
import com.example.spaced.ui.theme.DarkThemeConfig
import com.materialkolor.PaletteStyle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)

    val uiState: StateFlow<MainActivityUiState> = settingsRepository.userSettings
        .map<UserSettings, MainActivityUiState> { userSettings ->
            MainActivityUiState.Success(userSettings)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = MainActivityUiState.Loading
        )

    fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            settingsRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    fun setUseDynamicColor(useDynamicColor: Boolean) {
        viewModelScope.launch {
            settingsRepository.setUseDynamicColor(useDynamicColor)
        }
    }

    fun setPaletteStyle(paletteStyle: PaletteStyle) {
        viewModelScope.launch {
            settingsRepository.setPaletteStyle(paletteStyle)
        }
    }
}