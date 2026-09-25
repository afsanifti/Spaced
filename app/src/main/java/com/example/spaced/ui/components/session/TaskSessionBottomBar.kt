package com.example.spaced.ui.components.session

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.theme.TrackTextStyle

@Composable
fun TaskSessionBottomBar(
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Animated side button width: 95.dp when stopped -> 74.dp when playing
    val sideButtonWidth by animateDpAsState(
        targetValue = if (isPlaying) 74.dp else 95.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SideButtonWidth"
    )

    // 2. Animated shape for center button: Pill shape (47.5.dp) -> Squircle (30.dp)
    val centerCornerRadius by animateDpAsState(
        targetValue = if (isPlaying) 30.dp else 47.5.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CenterCornerRadius"
    )

    // 3. Animated background color for center button
    val centerBgColor by animateColorAsState(
        targetValue = if (isPlaying) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.tertiary,
        label = "CenterBgColor"
    )

    // 4. Animated text/icon color for center button
    val centerContentColor by animateColorAsState(
        targetValue = if (isPlaying) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onTertiary,
        label = "CenterContentColor"
    )

    val sideButtonBgColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val sideIconColor = MaterialTheme.colorScheme.onSecondaryContainer

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Left Button: Reset ---
        Surface(
            onClick = onResetClick,
            modifier = Modifier
                .width(sideButtonWidth)
                .height(95.dp),
            shape = CircleShape,
            color = sideButtonBgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = "Reset",
                    tint = sideIconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // --- Center Button: Play / Pause ---
        Surface(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .weight(1f)
                .height(95.dp),
            shape = RoundedCornerShape(centerCornerRadius),
            color = centerBgColor
        ) {
            AnimatedContent(
                targetState = isPlaying,
                transitionSpec = {
                    (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                },
                contentAlignment = Alignment.Center,
                label = "PlayPauseContent"
            ) { playing ->
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                        contentDescription = if (playing) "Pause" else "Play",
                        tint = centerContentColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (playing) "Pause" else "Play",
                        fontSize = 20.sp,
                        style = TrackTextStyle,
                        color = centerContentColor
                    )
                }
            }
        }

        // --- Right Button: Stop ---
        Surface(
            onClick = onStopClick,
            modifier = Modifier
                .width(sideButtonWidth)
                .height(95.dp),
            shape = CircleShape,
            color = sideButtonBgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Stop,
                    contentDescription = "Stop",
                    tint = sideIconColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}