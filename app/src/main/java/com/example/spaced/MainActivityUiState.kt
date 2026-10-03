package com.example.spaced

import com.example.spaced.data.model.UserSettings

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(val userSettings: UserSettings) : MainActivityUiState
}