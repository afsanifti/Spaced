package com.example.spaced.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.example.spaced.ui.components.navigation.TitleTopBar
import com.example.spaced.ui.theme.DarkThemeConfig
import com.materialkolor.PaletteStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsScreen(
    darkThemeConfig: DarkThemeConfig,
    onDarkThemeConfigChanged: (DarkThemeConfig) -> Unit,
    useDynamicColor: Boolean,
    onDynamicColorChanged: (Boolean) -> Unit,
    paletteStyle: PaletteStyle,
    onPaletteStyleChanged: (PaletteStyle) -> Unit,
    modifier: Modifier = Modifier,
    onScrollStateChanged: (isNavVisible: Boolean) -> Unit = {}
) {
    val verticalScrollState = rememberScrollState()
    var previousOffset by remember { mutableIntStateOf(0) }

    LaunchedEffect(verticalScrollState) {
        snapshotFlow { verticalScrollState.value }.collect { currentOffset ->
            val delta = currentOffset - previousOffset
            if (delta > 12 && currentOffset > 40) {
                onScrollStateChanged(false)
            } else if (delta < -12) {
                onScrollStateChanged(true)
            }
            previousOffset = currentOffset
        }
    }

    var isThemeExpanded by rememberSaveable { mutableStateOf(false) }
    var isPaletteExpanded by rememberSaveable { mutableStateOf(false) }

    val expandedColors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    val collapsedColors = ListItemDefaults.colors(containerColor = Color.Transparent)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Top App Bar ---
        TitleTopBar(
            title = "Settings",
            primaryBg = MaterialTheme.colorScheme.background,
            onPrimaryColor = MaterialTheme.colorScheme.onBackground
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
        ) {
            // ==========================================
            // --- SECTION 1: Theme Mode Expandable ---
            // ==========================================
            val themeOptions = listOf(
                DarkThemeConfig.SYSTEM to Pair("System Default", "Follow system dark theme setting"),
                DarkThemeConfig.LIGHT to Pair("Light Theme", "Bright and clear palette"),
                DarkThemeConfig.DARK to Pair("Dark Theme", "Deep dark background palette")
            )
            val themeItemCount = 1 + if (isThemeExpanded) themeOptions.size else 0

            val themeSubtitle = when (darkThemeConfig) {
                DarkThemeConfig.SYSTEM -> "System Default"
                DarkThemeConfig.LIGHT -> "Light Theme"
                DarkThemeConfig.DARK -> "Dark Theme"
            }

            SegmentedListItem(
                onClick = { isThemeExpanded = !isThemeExpanded },
                modifier = Modifier.semantics {
                    stateDescription = if (isThemeExpanded) "Expanded" else "Collapsed"
                },
                colors = if (isThemeExpanded) expandedColors else collapsedColors,
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = themeItemCount),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.DarkMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = if (isThemeExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null
                    )
                },
                supportingContent = {
                    Text(if (isThemeExpanded) "Choose your display preference" else themeSubtitle)
                },
                content = { Text("Theme Mode") }
            )

            AnimatedVisibility(
                visible = isThemeExpanded,
                enter = expandVertically(MaterialTheme.motionScheme.fastSpatialSpec()) + fadeIn(),
                exit = shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                ) {
                    themeOptions.forEachIndexed { idx, (config, info) ->
                        val isSelected = darkThemeConfig == config
                        SegmentedListItem(
                            selected = isSelected,
                            onClick = {
                                onDarkThemeConfigChanged(config)
                                isThemeExpanded = false
                            },
                            colors = expandedColors,
                            shapes = ListItemDefaults.segmentedShapes(
                                index = idx + 1,
                                count = themeItemCount
                            ),
                            supportingContent = { Text(info.second) },
                            trailingContent = {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null
                                )
                            },
                            content = { Text(info.first) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // --- SECTION 2: Dynamic Wallpaper ---
            // ==========================================
            SegmentedListItem(
                onClick = { onDynamicColorChanged(!useDynamicColor) },
                colors = collapsedColors,
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = 1),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.Wallpaper,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingContent = {
                    Switch(
                        checked = useDynamicColor,
                        onCheckedChange = onDynamicColorChanged,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                supportingContent = { Text("Extract colors from device wallpaper") },
                content = { Text("Dynamic Wallpaper") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // --- SECTION 3: Palette Style Expandable ---
            // ==========================================
            val paletteStyles = listOf(
                PaletteStyle.TonalSpot to Pair("Tonal Spot", "Default Material 3 balanced palette"),
                PaletteStyle.Vibrant to Pair("Vibrant", "High saturation primary & secondary colors"),
                PaletteStyle.Expressive to Pair("Expressive", "Bold and energetic color contrast"),
                PaletteStyle.Neutral to Pair("Neutral", "Subtle, low saturation clean tones"),
                PaletteStyle.FruitSalad to Pair("Fruit Salad", "Playful and colorful spectrum"),
                PaletteStyle.Rainbow to Pair("Rainbow", "Multi-hue dynamic palette"),
                PaletteStyle.Monochrome to Pair("Monochrome", "Grayscale minimal tint style"),
                PaletteStyle.Fidelity to Pair("Fidelity", "Exact seed color matching"),
                PaletteStyle.Content to Pair("Content", "Content-first accessible contrast")
            )
            val paletteItemCount = 1 + if (isPaletteExpanded) paletteStyles.size else 0
            val paletteSubtitle = paletteStyle.name.replace("_", " ")

            SegmentedListItem(
                onClick = { isPaletteExpanded = !isPaletteExpanded },
                modifier = Modifier.semantics {
                    stateDescription = if (isPaletteExpanded) "Expanded" else "Collapsed"
                },
                colors = if (isPaletteExpanded) expandedColors else collapsedColors,
                shapes = ListItemDefaults.segmentedShapes(index = 0, count = paletteItemCount),
                leadingContent = {
                    Icon(
                        imageVector = Icons.Rounded.ColorLens,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = if (isPaletteExpanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                        contentDescription = null
                    )
                },
                supportingContent = {
                    Text(if (isPaletteExpanded) "Select color generation tuning" else paletteSubtitle)
                },
                content = { Text("Palette Style") }
            )

            AnimatedVisibility(
                visible = isPaletteExpanded,
                enter = expandVertically(MaterialTheme.motionScheme.fastSpatialSpec()) + fadeIn(),
                exit = shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec()) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectableGroup(),
                    verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap)
                ) {
                    paletteStyles.forEachIndexed { idx, (style, info) ->
                        val isSelected = paletteStyle == style
                        SegmentedListItem(
                            selected = isSelected,
                            onClick = {
                                onPaletteStyleChanged(style)
                                isPaletteExpanded = false
                            },
                            colors = expandedColors,
                            shapes = ListItemDefaults.segmentedShapes(
                                index = idx + 1,
                                count = paletteItemCount
                            ),
                            supportingContent = { Text(info.second) },
                            trailingContent = {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = null
                                )
                            },
                            content = { Text(info.first) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}