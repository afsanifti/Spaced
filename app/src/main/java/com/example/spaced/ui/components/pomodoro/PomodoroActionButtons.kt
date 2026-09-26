package com.example.spaced.ui.components.pomodoro

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PomodoroActionButtons(
    isTimerRunning: Boolean,
    onTogglePlayPause: () -> Unit,
    onSkipClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pinkAccent = Color(0xFFFA7BB9)
    val darkButtonBg = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f)

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    // Responsive container height
    val containerHeight = when {
        screenHeight < 640 -> 56.dp
        screenHeight < 740 -> 64.dp
        else -> 72.dp
    }

    // Increased side paddings (scales with device height)
    val sidePadding = when {
        screenHeight < 640 -> 20.dp
        screenHeight < 740 -> 24.dp
        else -> 28.dp
    }

    val textStyle = when {
        screenHeight < 640 -> MaterialTheme.typography.titleSmall
        else -> MaterialTheme.typography.titleMedium
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = sidePadding)
            .height(containerHeight),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Play / Pause Button (Horizontal Capsule)
        Button(
            onClick = onTogglePlayPause,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = pinkAccent,
                contentColor = Color(0xFF32001E)
            ),
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isTimerRunning) Icons.Outlined.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isTimerRunning) "Pause" else "Play"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isTimerRunning) "Pause" else "Play",
                    style = textStyle,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 2. Skip Button (Middle - 1:1 Perfect Circle)
        Surface(
            shape = CircleShape,
            color = darkButtonBg,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(CircleShape)
                .clickable(onClick = onSkipClick)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = "Skip Phase",
                    tint = Color.White
                )
            }
        }

        // 3. Reset Button (Right - Vertical Capsule)
        Surface(
            shape = CircleShape,
            color = darkButtonBg,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(0.72f)
                .clip(CircleShape)
                .clickable(onClick = onResetClick)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Reset Timer",
                    tint = Color.White
                )
            }
        }
    }
}