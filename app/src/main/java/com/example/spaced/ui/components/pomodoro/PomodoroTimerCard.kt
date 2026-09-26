package com.example.spaced.ui.components.pomodoro

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.components.session.AmbientMeshCard
import com.example.spaced.ui.utils.PomodoroPhase
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PomodoroTimerCard(
    currentPhase: PomodoroPhase,
    remainingSeconds: Int,
    progress: Float,
    sessionTitle: String,
    isEditingTitle: Boolean,
    onTitleChange: (String) -> Unit,
    onToggleEditTitle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lavenderAccent = Color(0xFF9F83FF)
    val darkTrackColor = Color(0xFF42328A).copy(alpha = 0.5f)

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp

    // Adapt inner sizing based on screen height while preserving aspect ratio
    val cardPadding = when {
        screenHeight < 640 -> 12.dp
        screenHeight < 740 -> 16.dp
        else -> 20.dp
    }

    val progressFraction = when {
        screenHeight < 640 -> 0.52f
        screenHeight < 740 -> 0.58f
        else -> 0.65f
    }

    val timerFontSize = when {
        screenHeight < 640 -> 22.sp
        screenHeight < 740 -> 25.sp
        else -> 28.sp
    }

    val badgeVerticalPadding = when {
        screenHeight < 640 -> 4.dp
        screenHeight < 740 -> 5.dp
        else -> 6.dp
    }

    AmbientMeshCard(
        isBreakMode = currentPhase != PomodoroPhase.FOCUS,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Row with Title Editing
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (isEditingTitle) {
                    OutlinedTextField(
                        value = sessionTitle,
                        onValueChange = onTitleChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth(0.65f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = lavenderAccent,
                            unfocusedBorderColor = lavenderAccent.copy(alpha = 0.5f)
                        )
                    )
                } else {
                    Text(
                        text = sessionTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = lavenderAccent,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .clickable(onClick = onToggleEditTitle)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isEditingTitle) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEditingTitle) "Confirm Title" else "Edit Title",
                            tint = Color(0xFF231842),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Material 3 Circular Wavy Progress Indicator
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularWavyProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxSize(),
                    color = lavenderAccent,
                    trackColor = darkTrackColor
                )

                Text(
                    text = formatTimeDisplay(remainingSeconds),
                    fontSize = timerFontSize,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Phase Tag Pill Badge
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(lavenderAccent)
                    .padding(horizontal = 18.dp, vertical = badgeVerticalPadding)
            ) {
                Text(
                    text = currentPhase.name.uppercase(),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF231842)
                )
            }
        }
    }
}

private fun formatTimeDisplay(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d : %02d", mins, secs)
}