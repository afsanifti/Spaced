package com.example.spaced.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spaced.ui.components.navigation.TitleTopBar
import com.example.spaced.ui.components.pomodoro.PomodoroActionButtons
import com.example.spaced.ui.components.pomodoro.PomodoroDurationPickerDialog
import com.example.spaced.ui.components.pomodoro.PomodoroPhaseSelector
import com.example.spaced.ui.components.pomodoro.PomodoroTimerCard
import com.example.spaced.ui.utils.PomodoroPhase
import kotlinx.coroutines.delay

@Composable
fun PomodoroScreen(
    onScrollStateChanged: (isNavVisible: Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sessionTitle by remember { mutableStateOf("Default") }
    var isEditingTitle by remember { mutableStateOf(false) }

    var currentPhase by remember { mutableStateOf(PomodoroPhase.FOCUS) }
    var focusDurationSec by remember { mutableIntStateOf(25 * 60) }
    var shortBreakDurationSec by remember { mutableIntStateOf(5 * 60) }
    var longBreakDurationSec by remember { mutableIntStateOf(15 * 60) }

    var isTimerRunning by remember { mutableStateOf(false) }
    var remainingSeconds by remember { mutableIntStateOf(25 * 60) }

    var phaseToEdit by remember { mutableStateOf<PomodoroPhase?>(null) }

    val scrollState = rememberScrollState()
    var previousOffset by remember { mutableIntStateOf(0) }

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

    LaunchedEffect(isTimerRunning) {
        while (isTimerRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        }
        if (remainingSeconds == 0) {
            isTimerRunning = false
        }
    }

    val totalDurationForPhase = when (currentPhase) {
        PomodoroPhase.FOCUS -> focusDurationSec
        PomodoroPhase.SHORT_BREAK -> shortBreakDurationSec
        PomodoroPhase.LONG_BREAK -> longBreakDurationSec
    }

    val progress = if (totalDurationForPhase > 0) {
        1f - (remainingSeconds.toFloat() / totalDurationForPhase.toFloat())
    } else 0f

    phaseToEdit?.let { phase ->
        val initialDuration = when (phase) {
            PomodoroPhase.FOCUS -> focusDurationSec
            PomodoroPhase.SHORT_BREAK -> shortBreakDurationSec
            PomodoroPhase.LONG_BREAK -> longBreakDurationSec
        }

        val title = when (phase) {
            PomodoroPhase.FOCUS -> "Focus Period"
            PomodoroPhase.SHORT_BREAK -> "Short Break"
            PomodoroPhase.LONG_BREAK -> "Long Break"
        }

        PomodoroDurationPickerDialog(
            title = title,
            initialDurationSec = initialDuration,
            onDismiss = { phaseToEdit = null },
            onConfirm = { newDuration ->
                when (phase) {
                    PomodoroPhase.FOCUS -> focusDurationSec = newDuration
                    PomodoroPhase.SHORT_BREAK -> shortBreakDurationSec = newDuration
                    PomodoroPhase.LONG_BREAK -> longBreakDurationSec = newDuration
                }
                if (currentPhase == phase) {
                    remainingSeconds = newDuration
                    isTimerRunning = false
                }
                phaseToEdit = null
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Bar without back button
        TitleTopBar(
            title = "Pomodoro",
            primaryBg = MaterialTheme.colorScheme.background,
            onPrimaryColor = MaterialTheme.colorScheme.onBackground
        )

        // Scrollable Screen Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            PomodoroTimerCard(
                currentPhase = currentPhase,
                remainingSeconds = remainingSeconds,
                progress = progress,
                sessionTitle = sessionTitle,
                isEditingTitle = isEditingTitle,
                onTitleChange = { sessionTitle = it },
                onToggleEditTitle = { isEditingTitle = !isEditingTitle }
            )

            Spacer(modifier = Modifier.height(20.dp))

            PomodoroPhaseSelector(
                currentPhase = currentPhase,
                focusDurationSec = focusDurationSec,
                shortBreakDurationSec = shortBreakDurationSec,
                longBreakDurationSec = longBreakDurationSec,
                onPhaseSelected = { phase ->
                    currentPhase = phase
                    remainingSeconds = when (phase) {
                        PomodoroPhase.FOCUS -> focusDurationSec
                        PomodoroPhase.SHORT_BREAK -> shortBreakDurationSec
                        PomodoroPhase.LONG_BREAK -> longBreakDurationSec
                    }
                    isTimerRunning = false
                },
                onOpenTimePicker = { phase ->
                    phaseToEdit = phase
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            PomodoroActionButtons(
                isTimerRunning = isTimerRunning,
                onTogglePlayPause = { isTimerRunning = !isTimerRunning },
                onResetClick = { },
                onSkipClick = {
                    isTimerRunning = false
                    remainingSeconds = totalDurationForPhase
                }
            )

            Spacer(modifier = Modifier.height(120.dp))
        }
    }
}