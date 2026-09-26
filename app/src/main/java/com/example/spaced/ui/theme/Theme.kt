package com.example.spaced.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.materialkolor.DynamicMaterialExpressiveTheme
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicMaterialThemeState

// Seed color generated from MaterialKolor Builder
val SeedColor = Color(0xFF4C5CDC)

enum class DarkThemeConfig {
    SYSTEM, // Follow device system theme
    LIGHT,  // Force Light mode
    DARK    // Force Dark mode
}

val ExpressiveShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpacedTheme(
    darkThemeConfig: DarkThemeConfig = DarkThemeConfig.SYSTEM,
    useDynamicColor: Boolean = true,
    paletteStyle: PaletteStyle = PaletteStyle.TonalSpot,
    systemInDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current

    // 1. Determine dark vs light mode independently from dynamic color source
    val isDark = when (darkThemeConfig) {
        DarkThemeConfig.DARK -> true
        DarkThemeConfig.LIGHT -> false
        DarkThemeConfig.SYSTEM -> systemInDarkTheme
    }

    // 2. Derive seed color (Wallpaper dynamic primary on Android 12+, or SeedColor preset)
    val effectiveSeedColor = if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val dynamicScheme = if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dynamicScheme.primary
    } else {
        SeedColor
    }

    // 3. Generate MaterialKolor Expressive state with user-selected PaletteStyle
    val dynamicThemeState = rememberDynamicMaterialThemeState(
        isDark = isDark,
        style = paletteStyle,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        seedColor = effectiveSeedColor,
    )

    // 4. Apply Material 3 Expressive theme wrapper
    DynamicMaterialExpressiveTheme(
        state = dynamicThemeState,
        motionScheme = MotionScheme.expressive(),
        animate = true,
        shapes = ExpressiveShapes,
        typography = SpacedTypography,
        content = content
    )
}