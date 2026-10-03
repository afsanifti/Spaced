package com.example.spaced.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.EaseInBack
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spaced.ui.components.navigation.TitleTopBar
import com.example.spaced.ui.components.pomodoro.PomodoroActionButtons
import com.example.spaced.ui.components.pomodoro.PomodoroDurationPickerDialog
import com.example.spaced.ui.components.pomodoro.PomodoroPhaseSelector
import com.example.spaced.ui.components.pomodoro.PomodoroTimerCard
import com.example.spaced.ui.components.pomodoro.PomodoroTimelineCard
import com.example.spaced.ui.utils.PomodoroPhase
import kotlinx.coroutines.delay

private const val TOTAL_CYCLE_STEPS = 8

@Composable
fun PomodoroScreen(
    onScrollStateChanged: (isNavVisible: Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 80.dp)
) {
    var sessionTitle by remember { mutableStateOf("Default") }
    var isEditingTitle by remember { mutableStateOf(false) }

    // Phase Durations (in seconds)
    var focusDurationSec by remember { mutableIntStateOf(25 * 60) }
    var shortBreakDurationSec by remember { mutableIntStateOf(5 * 60) }
    var longBreakDurationSec by remember { mutableIntStateOf(15 * 60) }

    // Active Session & Cycle State
    var isSessionActive by remember { mutableStateOf(false) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    var remainingSeconds by remember { mutableIntStateOf(25 * 60) }

    var phaseToEdit by remember { mutableStateOf<PomodoroPhase?>(null) }

    // Derive current phase based on step index (0, 2, 4, 6 = FOCUS | 1, 3, 5 = SHORT_BREAK | 7 = LONG_BREAK)
    val currentPhase = remember(currentStepIndex) {
        when {
            currentStepIndex % 2 == 0 -> PomodoroPhase.FOCUS
            currentStepIndex == 7 -> PomodoroPhase.LONG_BREAK
            else -> PomodoroPhase.SHORT_BREAK
        }
    }

    val isBreakMode = currentPhase != PomodoroPhase.FOCUS

    // Helper to retrieve initial duration for any step index
    fun getDurationForStep(stepIndex: Int): Int {
        return when {
            stepIndex % 2 == 0 -> focusDurationSec
            stepIndex == 7 -> longBreakDurationSec
            else -> shortBreakDurationSec
        }
    }

    val totalDurationForCurrentStep = getDurationForStep(currentStepIndex)

    val progress = if (totalDurationForCurrentStep > 0) {
        1f - (remainingSeconds.toFloat() / totalDurationForCurrentStep.toFloat())
    } else 0f

    val nextStepIndex = (currentStepIndex + 1) % TOTAL_CYCLE_STEPS
    val nextPhaseName = when {
        nextStepIndex % 2 == 0 -> "Focus period"
        nextStepIndex == 7 -> "Long break"
        else -> "Short break"
    }
    val nextPhaseMin = getDurationForStep(nextStepIndex) / 60

    LaunchedEffect(Unit) {
        onScrollStateChanged(true)
    }

    // Timer Loop & Automatic Auto-Advance
    LaunchedEffect(isTimerRunning, remainingSeconds) {
        if (isTimerRunning && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
        } else if (isTimerRunning && remainingSeconds == 0) {
            val nextStep = (currentStepIndex + 1) % TOTAL_CYCLE_STEPS
            currentStepIndex = nextStep
            remainingSeconds = getDurationForStep(nextStep)
        }
    }

    // Duration Picker Dialog
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
                if (!isSessionActive && currentPhase == phase) {
                    remainingSeconds = newDuration
                }
                phaseToEdit = null
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        TitleTopBar(
            title = "Pomodoro",
            primaryBg = MaterialTheme.colorScheme.background,
            onPrimaryColor = MaterialTheme.colorScheme.onBackground
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = contentPadding.calculateBottomPadding())
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Timer Display Card
            PomodoroTimerCard(
                currentPhase = currentPhase,
                remainingSeconds = remainingSeconds,
                progress = progress,
                sessionTitle = sessionTitle,
                isEditingTitle = isEditingTitle,
                isSessionActive = isSessionActive, // <-- Passed here
                onTitleChange = { sessionTitle = it },
                onToggleEditTitle = { isEditingTitle = !isEditingTitle },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            )

            // Fixed gap below the timer card regardless of middle container content height
            Spacer(modifier = Modifier.height(24.dp))

            // 2. Middle Container: Setup Selector <-> Active Session Timeline
            AnimatedContent(
                targetState = isSessionActive,
                contentAlignment = Alignment.TopCenter, // Keeps top edge anchored
                transitionSpec = {
                    if (targetState) {
                        (fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 180)) +
                                slideInVertically(
                                    initialOffsetY = { it / 2 },
                                    animationSpec = tween(durationMillis = 300, delayMillis = 180)
                                ) +
                                scaleIn(
                                    initialScale = 0.92f,
                                    animationSpec = tween(durationMillis = 300, delayMillis = 180)
                                ))
                            .togetherWith(
                                fadeOut(animationSpec = tween(durationMillis = 200)) +
                                        slideOutVertically(
                                            targetOffsetY = { it },
                                            animationSpec = tween(durationMillis = 300, easing = EaseInBack)
                                        ) +
                                        scaleOut(
                                            targetScale = 0.85f,
                                            animationSpec = tween(durationMillis = 300, easing = EaseInBack)
                                        )
                            )
                    } else {
                        (fadeIn(animationSpec = tween(durationMillis = 300, delayMillis = 180)) +
                                slideInVertically(
                                    initialOffsetY = { it },
                                    animationSpec = tween(durationMillis = 300, delayMillis = 180, easing = EaseOutBack)
                                ) +
                                scaleIn(
                                    initialScale = 0.85f,
                                    animationSpec = tween(durationMillis = 300, delayMillis = 180, easing = EaseOutBack)
                                ))
                            .togetherWith(
                                fadeOut(animationSpec = tween(durationMillis = 200)) +
                                        slideOutVertically(
                                            targetOffsetY = { it / 2 },
                                            animationSpec = tween(durationMillis = 300)
                                        ) +
                                        scaleOut(
                                            targetScale = 0.92f,
                                            animationSpec = tween(durationMillis = 300)
                                        )
                            )
                    }
                },
                label = "MiddleContainerTransition",
                modifier = Modifier.padding(horizontal = 20.dp)
            ) { active ->
                if (!active) {
                    PomodoroPhaseSelector(
                        currentPhase = currentPhase,
                        focusDurationSec = focusDurationSec,
                        shortBreakDurationSec = shortBreakDurationSec,
                        longBreakDurationSec = longBreakDurationSec,
                        onPhaseSelected = { /* Selection disabled in setup per design */ },
                        onOpenTimePicker = { phase -> phaseToEdit = phase },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    val nextStepIndex = (currentStepIndex + 1) % TOTAL_CYCLE_STEPS
                    val nextPhaseName = when {
                        nextStepIndex % 2 == 0 -> "Focus period"
                        nextStepIndex == 7 -> "Long break"
                        else -> "Short break"
                    }
                    val nextPhaseMin = getDurationForStep(nextStepIndex) / 60

                    PomodoroTimelineCard(
                        nextPhaseName = nextPhaseName,
                        nextPhaseDurationMin = nextPhaseMin,
                        currentStepIndex = currentStepIndex,
                        totalSteps = TOTAL_CYCLE_STEPS,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Absorbs all remaining vertical space above bottom action buttons
            Spacer(modifier = Modifier.weight(1f))

            // 3. Bottom Action Bar
            PomodoroActionButtons(
                isSessionActive = isSessionActive,
                isPlaying = isTimerRunning,
                isBreakMode = isBreakMode,
                onStartSession = {
                    isSessionActive = true
                    isTimerRunning = true
                },
                onPlayPauseClick = {
                    isTimerRunning = !isTimerRunning
                },
                onResetClick = {
                    remainingSeconds = totalDurationForCurrentStep
                },
                onSkipClick = {
                    val nextStep = (currentStepIndex + 1) % TOTAL_CYCLE_STEPS
                    currentStepIndex = nextStep
                    remainingSeconds = getDurationForStep(nextStep)
                },
                onStopClick = {
                    isSessionActive = false
                    isTimerRunning = false
                    currentStepIndex = 0
                    remainingSeconds = focusDurationSec
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
