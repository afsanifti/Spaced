package com.example.spaced.data.model

import com.example.spaced.ui.theme.DarkThemeConfig
import com.materialkolor.PaletteStyle

data class UserSettings(
    val darkThemeConfig: DarkThemeConfig = DarkThemeConfig.SYSTEM,
    val useDynamicColor: Boolean = true,
    val paletteStyle: PaletteStyle = PaletteStyle.TonalSpot
)