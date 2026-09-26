package com.example.spaced.ui.components.session

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.data.model.Task
import com.example.spaced.ui.theme.NormalFontFamily
import com.example.spaced.ui.theme.TagAndChapTextStyle
import com.example.spaced.ui.theme.TaskTitleTextStyle
import com.example.spaced.ui.theme.TimerTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSessionCard(
    task: Task,
    formattedTime: String,
    progress: Float,
    currentCycle: Int,
    totalCycles: Int,
    isBreakMode: Boolean,
    modeLabel: String,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val strokePx = with(density) { 10.dp.toPx() }

    // Dynamic animated colors for Break vs Focus mode
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
    val noteTextColor by animateColorAsState(
        targetValue = if (isBreakMode) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(1000),
        label = "NoteTextColor"
    )

    AmbientMeshCard(
        isBreakMode = isBreakMode,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp, horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Details
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Ph 1109 · Chapter 2",
                    style = TagAndChapTextStyle,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                )
                Text(
                    text = task.title.ifEmpty { "8085 Microprocessor" },
                    style = TaskTitleTextStyle,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "My notes, Book page 112",
                    style = TagAndChapTextStyle,
                    color = noteTextColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Dynamic Progress & Timer Text
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(154.dp)
            ) {
                CircularWavyProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = progressColor,
                    trackColor = progressTrackColor,
                    stroke = Stroke(width = strokePx, cap = StrokeCap.Round),
                    trackStroke = Stroke(width = strokePx, cap = StrokeCap.Round),
                    wavelength = 36.dp,
                    gapSize = 6.dp
                )

                Text(
                    text = formattedTime,
                    style = TimerTextStyle,
                    color = progressColor
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Dynamic Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = badgeBgColor
                ) {
                    Text(
                        text = "Cycle $currentCycle of $totalCycles",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NormalFontFamily,
                        ),
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = badgeBgColor
                ) {
                    Text(
                        text = modeLabel,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NormalFontFamily,
                        ),
                        color = badgeTextColor,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}