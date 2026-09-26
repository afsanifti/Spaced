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
import androidx.compose.material.icons.rounded.SkipNext
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
    isBreakMode: Boolean,
    onPlayPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    onSkipClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sideButtonWidth by animateDpAsState(
        targetValue = if (isPlaying) 74.dp else 95.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SideButtonWidth"
    )

    val centerCornerRadius by animateDpAsState(
        targetValue = if (isPlaying) 30.dp else 47.5.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CenterCornerRadius"
    )

    val targetCenterBg = when {
        isBreakMode -> MaterialTheme.colorScheme.tertiary
        isPlaying -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    val targetCenterContent = when {
        isBreakMode -> MaterialTheme.colorScheme.onTertiary
        isPlaying -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onPrimary
    }

    val centerBgColor by animateColorAsState(
        targetValue = targetCenterBg,
        label = "CenterBgColor"
    )

    val centerContentColor by animateColorAsState(
        targetValue = targetCenterContent,
        label = "CenterContentColor"
    )

    val sideButtonBgColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val sideIconColor = MaterialTheme.colorScheme.onSecondaryContainer
    val buttonHeight = 95.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Left Action Button (Reset in Focus mode / Skip in Break mode) ---
        Surface(
            onClick = if (isBreakMode) onSkipClick else onResetClick,
            modifier = Modifier
                .width(sideButtonWidth)
                .height(buttonHeight),
            shape = CircleShape,
            color = sideButtonBgColor
        ) {
            Box(contentAlignment = Alignment.Center) {
                AnimatedContent(
                    targetState = isBreakMode,
                    transitionSpec = {
                        (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
                    },
                    contentAlignment = Alignment.Center,
                    label = "LeftButtonContent"
                ) { inBreak ->
                    Icon(
                        imageVector = if (inBreak) Icons.Rounded.SkipNext else Icons.Rounded.RestartAlt,
                        contentDescription = if (inBreak) "Skip Break" else "Reset",
                        tint = sideIconColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // --- Play / Pause ---
        Surface(
            onClick = onPlayPauseClick,
            modifier = Modifier
                .weight(1f)
                .height(buttonHeight),
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

        // --- Stop ---
        Surface(
            onClick = onStopClick,
            modifier = Modifier
                .width(sideButtonWidth)
                .height(buttonHeight),
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