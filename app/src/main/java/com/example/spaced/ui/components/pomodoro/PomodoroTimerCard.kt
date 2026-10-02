package com.example.spaced.ui.components.pomodoro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.theme.NormalFontFamily
import com.example.spaced.ui.theme.TimerTextStyle
import com.example.spaced.ui.utils.PomodoroPhase

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroTimerCard(
    currentPhase: PomodoroPhase,
    remainingSeconds: Int,
    progress: Float,
    sessionTitle: String,
    isEditingTitle: Boolean,
    isSessionActive: Boolean = false,
    onTitleChange: (String) -> Unit,
    onToggleEditTitle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isBreakMode = currentPhase != PomodoroPhase.FOCUS
    val density = LocalDensity.current
    val strokePx = with(density) { 8.dp.toPx() }

    val progressColor by animateColorAsState(
        targetValue = if (isBreakMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.primary,
        animationSpec = tween(1000),
        label = "ProgressColor"
    )
    val progressTrackColor by animateColorAsState(
        targetValue = if (isBreakMode) MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.secondaryContainer,
        animationSpec = tween(1000),
        label = "ProgressTrackColor"
    )
    val badgeBgColor by animateColorAsState(
        targetValue = if (isBreakMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(1000),
        label = "BadgeBgColor"
    )
    val badgeTextColor by animateColorAsState(
        targetValue = if (isBreakMode) MaterialTheme.colorScheme.onTertiary else MaterialTheme.colorScheme.onSecondary,
        animationSpec = tween(1000),
        label = "BadgeTextColor"
    )

    AmbientMeshCard(
        isBreakMode = isBreakMode,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(6f / 5f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header: Perfectly centered title with animated Edit Button
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isEditingTitle) {
                    OutlinedTextField(
                        value = sessionTitle,
                        onValueChange = onTitleChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium.copy(textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onToggleEditTitle() }),
                        modifier = Modifier.padding(horizontal = 40.dp)
                    )
                } else {
                    Text(
                        text = sessionTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 40.dp)
                    )
                }

                // Call explicit top-level AnimatedVisibility function to avoid ColumnScope ambiguity inside Box
                androidx.compose.animation.AnimatedVisibility(
                    visible = !isSessionActive,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f))
                            .clickable(onClick = onToggleEditTitle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEditingTitle) Icons.Rounded.Check else Icons.Rounded.Edit,
                            contentDescription = if (isEditingTitle) "Save Title" else "Edit Title",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timer & Wavy Indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(136.dp)
            ) {
                CircularWavyProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = progressColor,
                    trackColor = progressTrackColor,
                    stroke = Stroke(width = strokePx, cap = StrokeCap.Round),
                    trackStroke = Stroke(width = strokePx, cap = StrokeCap.Round),
                    wavelength = 32.dp,
                    gapSize = 6.dp
                )
                Text(
                    text = formatTimer(remainingSeconds),
                    style = TimerTextStyle,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer Phase Pill
            Surface(
                shape = CircleShape,
                color = badgeBgColor
            ) {
                Text(
                    text = currentPhase.name.replace("_", " "),
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = NormalFontFamily
                    ),
                    color = badgeTextColor,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }
    }
}

private fun formatTimer(totalSeconds: Int): String {
    val mins = (totalSeconds / 60).toString().padStart(2, '0')
    val secs = (totalSeconds % 60).toString().padStart(2, '0')
    return "$mins : $secs"
}