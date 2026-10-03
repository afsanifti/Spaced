package com.example.spaced.ui.screens

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.Wallpaper
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spaced.ui.components.navigation.TitleTopBar
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
            onScrollStateChanged(false)
        } else if (delta < -12) {
            onScrollStateChanged(true)
        }
        previousOffset = currentOffset
    }

    // Expandable Menu States
    var isThemeMenuExpanded by remember { mutableStateOf(false) }
    var isPaletteMenuExpanded by remember { mutableStateOf(false) }

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
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // --- SECTION 1: Theme Mode ---
            val themeSubtitle = when (darkThemeConfig) {
                DarkThemeConfig.SYSTEM -> "System Default"
                DarkThemeConfig.LIGHT -> "Light Theme"
                DarkThemeConfig.DARK -> "Dark Theme"
            }

            ExpandableSegmentedGroup(
                icon = Icons.Rounded.DarkMode,
                title = "Theme Mode",
                subtitle = if (isThemeMenuExpanded) "Choose your display preference" else themeSubtitle,
                isExpanded = isThemeMenuExpanded,
                onToggleExpand = { isThemeMenuExpanded = !isThemeMenuExpanded }
            ) {
                val themeOptions = listOf(
                    DarkThemeConfig.SYSTEM to Pair("System Default", "Follow system dark theme setting"),
                    DarkThemeConfig.LIGHT to Pair("Light Theme", "Bright and clear palette"),
                    DarkThemeConfig.DARK to Pair("Dark Theme", "Deep dark background palette")
                )

                themeOptions.forEachIndexed { index, (config, info) ->
                    val isLast = index == themeOptions.lastIndex
                    SegmentedListItem(
                        title = info.first,
                        subtitle = info.second,
                        selected = darkThemeConfig == config,
                        isLast = isLast,
                        onClick = {
                            onDarkThemeConfigChanged(config)
                            isThemeMenuExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- SECTION 2: Dynamic Wallpaper (Single Row) ---
            SingleSegmentRow(
                icon = Icons.Rounded.Wallpaper,
                title = "Dynamic Wallpaper",
                subtitle = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    "Extract colors from device wallpaper"
                } else {
                    "Requires Android 12+"
                },
                trailingControl = {
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
            )

            Spacer(modifier = Modifier.height(8.dp))

            // --- SECTION 3: Palette Style ---
            val paletteSubtitle = paletteStyle.name.replace("_", " ")

            ExpandableSegmentedGroup(
                icon = Icons.Rounded.ColorLens,
                title = "Palette Style",
                subtitle = if (isPaletteMenuExpanded) "Select color generation tuning" else paletteSubtitle,
                isExpanded = isPaletteMenuExpanded,
                onToggleExpand = { isPaletteMenuExpanded = !isPaletteMenuExpanded }
            ) {
                val styles = listOf(
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

                styles.forEachIndexed { index, (style, info) ->
                    val isLast = index == styles.lastIndex
                    SegmentedListItem(
                        title = info.first,
                        subtitle = info.second,
                        selected = paletteStyle == style,
                        isLast = isLast,
                        onClick = {
                            onPaletteStyleChanged(style)
                            isPaletteMenuExpanded = false
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}

/**
 * Expandable Header row that transitions from flat (no background container) to an elevated
 * active card container with a circular chevron pill badge when expanded.
 */
@Composable
private fun ExpandableSegmentedGroup(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    content: @Composable () -> Unit
) {
    val expressiveFloatSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val headerShape = if (isExpanded) {
        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 6.dp, bottomEnd = 6.dp)
    } else {
        RoundedCornerShape(16.dp)
    }

    val containerBgColor by animateColorAsState(
        targetValue = if (isExpanded) {
            MaterialTheme.colorScheme.surfaceContainerHigh
        } else {
            Color.Transparent
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "ContainerBgColor"
    )

    val chevronBgColor by animateColorAsState(
        targetValue = if (isExpanded) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            Color.Transparent
        },
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "ChevronBgColor"
    )

    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = expressiveFloatSpec,
        label = "ChevronRotation"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(headerShape)
                .clickable(onClick = onToggleExpand),
            color = containerBgColor,
            shape = headerShape
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(chevronBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn(),
            exit = shrinkVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ) + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * Connected Segment Card for items expanded inside a group.
 */
@Composable
private fun SegmentedListItem(
    title: String,
    subtitle: String,
    selected: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val shape = if (isLast) {
        RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 20.dp, bottomEnd = 20.dp)
    } else {
        RoundedCornerShape(6.dp)
    }

    val itemBgColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable(onClick = onClick),
        color = itemBgColor,
        shape = shape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
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
}

/**
 * Standard un-contained list row for non-expandable setting controls.
 */
@Composable
private fun SingleSegmentRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingControl: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        trailingControl()
    }
}