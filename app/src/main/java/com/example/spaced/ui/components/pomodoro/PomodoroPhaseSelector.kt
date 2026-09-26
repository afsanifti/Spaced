package com.example.spaced.ui.components.pomodoro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.outlined.CenterFocusWeak
import androidx.compose.material.icons.outlined.Coffee
import androidx.compose.material.icons.outlined.EventSeat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.spaced.ui.utils.PomodoroPhase
import java.util.Locale

private data class PhaseItem(
    val phase: PomodoroPhase,
    val title: String,
    val icon: ImageVector,
    val durationSec: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroPhaseSelector(
    currentPhase: PomodoroPhase,
    focusDurationSec: Int,
    shortBreakDurationSec: Int,
    longBreakDurationSec: Int,
    onPhaseSelected: (PomodoroPhase) -> Unit,
    onOpenTimePicker: (PomodoroPhase) -> Unit,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    // Dynamic item height & typography based on screen height
    val (itemMinHeight, overlineStyle, titleStyle) = when {
        screenHeight < 640 -> Triple(
            44.dp,
            MaterialTheme.typography.labelSmall,
            MaterialTheme.typography.bodyMedium
        )
        screenHeight < 740 -> Triple(
            52.dp,
            MaterialTheme.typography.bodySmall,
            MaterialTheme.typography.titleSmall
        )
        else -> Triple(
            60.dp,
            MaterialTheme.typography.bodySmall,
            MaterialTheme.typography.titleMedium
        )
    }

    val items = listOf(
        PhaseItem(
            phase = PomodoroPhase.FOCUS,
            title = "Focus Period",
            icon = Icons.Outlined.CenterFocusWeak,
            durationSec = focusDurationSec
        ),
        PhaseItem(
            phase = PomodoroPhase.SHORT_BREAK,
            title = "Short break",
            icon = Icons.Outlined.Coffee,
            durationSec = shortBreakDurationSec
        ),
        PhaseItem(
            phase = PomodoroPhase.LONG_BREAK,
            title = "Long break",
            icon = Icons.Outlined.EventSeat,
            durationSec = longBreakDurationSec
        )
    )

    Box(
        modifier = modifier.clip(RoundedCornerShape(24.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items.forEachIndexed { index, item ->
                SegmentedListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = itemMinHeight),
                    onClick = {
                        onPhaseSelected(item.phase)
                        onOpenTimePicker(item.phase)
                    },
                    shapes = ListItemDefaults.segmentedShapes(
                        index = index,
                        count = items.size
                    ),
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (currentPhase == item.phase) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    },
                    overlineContent = {
                        Text(
                            text = item.title,
                            style = overlineStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    content = {
                        Text(
                            text = formatSeconds(item.durationSec),
                            style = titleStyle,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ArrowRight,
                            contentDescription = "Edit ${item.title}",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }
    }
}

private fun formatSeconds(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
}