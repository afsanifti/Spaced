package com.example.spaced.ui.screens

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.spaced.ui.theme.DarkThemeConfig
import com.materialkolor.PaletteStyle

@Composable
fun SettingsScreen(
    darkThemeConfig: DarkThemeConfig,
    onDarkThemeConfigChanged: (DarkThemeConfig) -> Unit,
    useDynamicColor: Boolean,
    onDynamicColorChanged: (Boolean) -> Unit,
    paletteStyle: PaletteStyle,
    onPaletteStyleChanged: (PaletteStyle) -> Unit,
    onScrollStateChanged: (isNavVisible: Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var previousOffset by remember { mutableIntStateOf(0) }

    // Detect scroll direction to shrink/hide bottom navigation bar
    LaunchedEffect(scrollState.value) {
        val currentOffset = scrollState.value
        val delta = currentOffset - previousOffset

        if (delta > 12 && scrollState.value > 40) {
            onScrollStateChanged(false) // Scroll Down -> Shrink / Hide Nav Bar
        } else if (delta < -12) {
            onScrollStateChanged(true)  // Scroll Up -> Show Nav Bar
        }
        previousOffset = currentOffset
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECTION 1: Theme Mode ---
        Text(
            text = "Theme Mode",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                SettingRow(
                    title = "System Default",
                    subtitle = "Follow system dark theme setting",
                    selected = darkThemeConfig == DarkThemeConfig.SYSTEM,
                    onClick = { onDarkThemeConfigChanged(DarkThemeConfig.SYSTEM) }
                )

                SettingRow(
                    title = "Light Theme",
                    subtitle = "Bright and clear palette",
                    selected = darkThemeConfig == DarkThemeConfig.LIGHT,
                    onClick = { onDarkThemeConfigChanged(DarkThemeConfig.LIGHT) }
                )

                SettingRow(
                    title = "Dark Theme",
                    subtitle = "Deep background palette",
                    selected = darkThemeConfig == DarkThemeConfig.DARK,
                    onClick = { onDarkThemeConfigChanged(DarkThemeConfig.DARK) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECTION 2: Color Source ---
        Text(
            text = "Color Source",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Dynamic Wallpaper",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            "Extract colors from device wallpaper"
                        } else {
                            "Requires Android 12+"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Switch(
                    checked = useDynamicColor,
                    onCheckedChange = onDynamicColorChanged,
                    enabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECTION 3: Palette Style ---
        Text(
            text = "Palette Style",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                val styles = listOf(
                    PaletteStyle.TonalSpot to Pair("Tonal Spot", "Default Material 3 balanced palette"),
                    PaletteStyle.Vibrant to Pair("Vibrant", "High saturation primary and secondary colors"),
                    PaletteStyle.Expressive to Pair("Expressive", "Bold and energetic color contrast"),
                    PaletteStyle.Neutral to Pair("Neutral", "Subtle, low saturation clean tones"),
                    PaletteStyle.FruitSalad to Pair("Fruit Salad", "Playful and colorful spectrum"),
                    PaletteStyle.Rainbow to Pair("Rainbow", "Multi-hue dynamic palette"),
                    PaletteStyle.Monochrome to Pair("Monochrome", "Grayscale minimal tint style"),
                    PaletteStyle.Fidelity to Pair("Fidelity", "Exact seed color matching"),
                    PaletteStyle.Content to Pair("Content", "Content-first accessible contrast")
                )

                styles.forEachIndexed { index, (style, info) ->
                    SettingRow(
                        title = info.first,
                        subtitle = info.second,
                        selected = paletteStyle == style,
                        onClick = { onPaletteStyleChanged(style) }
                    )
                    if (index < styles.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }

        // 👈 Extra bottom spacing so all items can scroll completely above bottom overlays
        Spacer(modifier = Modifier.height(120.dp))
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}