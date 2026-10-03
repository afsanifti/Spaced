package com.example.spaced.ui.components.pomodoro

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.spaced.ui.theme.TrackTextStyle
import kotlinx.coroutines.launch

private val BUTTON_HEIGHT = 95.dp

@Composable
fun PomodoroActionButtons(
    isSessionActive: Boolean,
    isPlaying: Boolean,
    isBreakMode: Boolean,
    onStartSession: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onResetClick: () -> Unit,
    onSkipClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Shared hold progress (0f = normal, 1f = fully held to stop session)
    val holdProgress = remember { Animatable(0f) }
    val progressFactor = holdProgress.value

    // --- Base Layout & Width Animations ---
    val baseSideWidth = when {
        !isSessionActive -> 0.dp
        isPlaying -> 74.dp
        else -> 95.dp
    }

    // Shrink side button target width down to 0 as hold progress reaches 1f
    val targetSideWidth = baseSideWidth * (1f - progressFactor)

    val sideButtonWidth by animateDpAsState(
        targetValue = targetSideWidth,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "SideButtonWidth"
    )

    val sideButtonAlpha by animateFloatAsState(
        targetValue = if (isSessionActive) (1f - progressFactor) else 0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "SideButtonAlpha"
    )

    val sideButtonScale by animateFloatAsState(
        targetValue = if (isSessionActive) (1f - progressFactor * 0.3f) else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "SideButtonScale"
    )

    val centerMaxWidth by animateDpAsState(
        targetValue = if (isSessionActive) 600.dp else 180.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CenterMaxWidth"
    )

    val centerCornerRadius by animateDpAsState(
        targetValue = when {
            !isSessionActive || !isPlaying -> 47.5.dp
            else -> 30.dp
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "CenterCornerRadius"
    )

    // Reduce spacing as side buttons shrink to allow full expansion
    val itemSpacing by animateDpAsState(
        targetValue = if (isSessionActive) 10.dp * (1f - progressFactor) else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ItemSpacing"
    )

    // --- Dynamic Color Calculations ---
    val targetCenterBg = when {
        !isSessionActive -> MaterialTheme.colorScheme.tertiary
        isBreakMode -> MaterialTheme.colorScheme.tertiary
        isPlaying -> MaterialTheme.colorScheme.secondary
        else -> MaterialTheme.colorScheme.primary
    }

    val targetCenterContent = when {
        !isSessionActive -> MaterialTheme.colorScheme.onTertiary
        isBreakMode -> MaterialTheme.colorScheme.onTertiary
        isPlaying -> MaterialTheme.colorScheme.onSecondary
        else -> MaterialTheme.colorScheme.onPrimary
    }

    val centerBgColor by animateColorAsState(targetValue = targetCenterBg, label = "CenterBgColor")
    val centerContentColor by animateColorAsState(targetValue = targetCenterContent, label = "CenterContentColor")

    val sideButtonBgColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val sideIconColor = MaterialTheme.colorScheme.onSecondaryContainer

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(itemSpacing, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- Left Action Button: Reset ---
        TerminalSideButton(
            icon = Icons.Rounded.RestartAlt,
            contentDescription = "Reset Timer",
            baseWidth = sideButtonWidth,
            scale = sideButtonScale,
            alpha = sideButtonAlpha,
            containerColor = sideButtonBgColor,
            contentColor = sideIconColor,
            onClick = onResetClick,
            modifier = Modifier.zIndex(0f)
        )

        // --- Center Action Button ---
        TerminalCenterButton(
            isSessionActive = isSessionActive,
            isPlaying = isPlaying,
            cornerRadius = centerCornerRadius,
            containerColor = centerBgColor,
            contentColor = centerContentColor,
            holdProgress = holdProgress,
            onClick = {
                if (isSessionActive) onPlayPauseClick() else onStartSession()
            },
            onLongClick = {
                if (isSessionActive) onStopClick()
            },
            modifier = Modifier
                .zIndex(1f)
                .weight(1f)
                .widthIn(max = centerMaxWidth)
        )

        // --- Right Action Button: Skip ---
        TerminalSideButton(
            icon = Icons.Rounded.SkipNext,
            contentDescription = "Skip Phase",
            baseWidth = sideButtonWidth,
            scale = sideButtonScale,
            alpha = sideButtonAlpha,
            containerColor = sideButtonBgColor,
            contentColor = sideIconColor,
            onClick = onSkipClick,
            modifier = Modifier.zIndex(0f)
        )
    }
}

// ============================================================================
// Internal Sub-Composables
// ============================================================================

@Composable
private fun TerminalSideButton(
    icon: ImageVector,
    contentDescription: String,
    baseWidth: Dp,
    scale: Float,
    alpha: Float,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressedWidthOffset by animateDpAsState(
        targetValue = if (isPressed) 20.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "PressedWidthOffset"
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isPressed) 22.dp else 47.5.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "SideCornerRadius"
    )

    val currentShape = RoundedCornerShape(cornerRadius)

    Surface(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = modifier
            .width(baseWidth + pressedWidthOffset)
            .height(BUTTON_HEIGHT)
            .graphicsLayer {
                this.alpha = alpha
                this.scaleX = scale
                this.scaleY = scale
            },
        shape = currentShape,
        color = containerColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = contentColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun TerminalCenterButton(
    isSessionActive: Boolean,
    isPlaying: Boolean,
    cornerRadius: Dp,
    containerColor: Color,
    contentColor: Color,
    holdProgress: Animatable<Float, AnimationVector1D>,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    var isHoldCompleted by remember { mutableStateOf(false) }

    val errorBgColor = MaterialTheme.colorScheme.error
    val errorContentColor = MaterialTheme.colorScheme.onError

    // Interpolate container and content colors based on hold progress
    val animatedBgColor = lerp(containerColor, errorBgColor, holdProgress.value)
    val animatedContentColor = lerp(contentColor, errorContentColor, holdProgress.value)

    val pressedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "CenterPressedScale"
    )

    val centerShape = RoundedCornerShape(cornerRadius)

    Surface(
        modifier = modifier
            .height(BUTTON_HEIGHT)
            .graphicsLayer {
                scaleX = pressedScale
                scaleY = pressedScale
            }
            .clip(centerShape)
            .pointerInput(isSessionActive) {
                detectTapGestures(
                    onTap = {
                        if (!isHoldCompleted) {
                            onClick()
                        }
                    },
                    onPress = { offset ->
                        isHoldCompleted = false
                        val press = PressInteraction.Press(offset)
                        interactionSource.emit(press)

                        if (isSessionActive) {
                            val holdJob = scope.launch {
                                // Linear expansion during continuous hold
                                holdProgress.animateTo(
                                    targetValue = 1f,
                                    animationSpec = tween(
                                        durationMillis = 800,
                                        easing = LinearEasing
                                    )
                                )
                                if (holdProgress.value >= 1f) {
                                    isHoldCompleted = true
                                    onLongClick() // Stops session at once

                                    // Bounce progress back via spring animation
                                    holdProgress.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMedium
                                        )
                                    )
                                }
                            }

                            val released = tryAwaitRelease()
                            holdJob.cancel()

                            interactionSource.emit(
                                if (released) PressInteraction.Release(press)
                                else PressInteraction.Cancel(press)
                            )

                            // Spring back smoothly if released before completion
                            if (!isHoldCompleted && holdProgress.value > 0f) {
                                scope.launch {
                                    holdProgress.animateTo(
                                        targetValue = 0f,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessMediumLow
                                        )
                                    )
                                }
                            }
                        } else {
                            val released = tryAwaitRelease()
                            interactionSource.emit(
                                if (released) PressInteraction.Release(press)
                                else PressInteraction.Cancel(press)
                            )
                        }
                    }
                )
            },
        shape = centerShape,
        color = animatedBgColor
    ) {
        AnimatedContent(
            targetState = Pair(isSessionActive, isPlaying),
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            },
            contentAlignment = Alignment.Center,
            label = "CenterButtonContent"
        ) { (active, playing) ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val icon = when {
                    !active -> Icons.Rounded.PlayArrow
                    playing -> Icons.Rounded.Pause
                    else -> Icons.Rounded.PlayArrow
                }

                val text = when {
                    !active -> "Play"
                    playing -> "Pause"
                    else -> "Play"
                }

                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = animatedContentColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    fontSize = 20.sp,
                    style = TrackTextStyle,
                    color = animatedContentColor
                )
            }
        }
    }
}