package com.example.spaced.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spaced.ui.components.navigation.TitleTopBar
import com.example.spaced.ui.components.pomodoro.PomodoroActionButtons
import com.example.spaced.ui.components.pomodoro.PomodoroDurationPickerDialog
import com.example.spaced.ui.components.pomodoro.PomodoroPhaseSelector
import com.example.spaced.ui.components.pomodoro.PomodoroTimerCard
import com.example.spaced.ui.components.pomodoro.PomodoroTimelineCard
import com.example.spaced.ui.utils.PomodoroPhase
import com.example.spaced.ui.viewmodels.PomodoroViewModel

private const val TOTAL_CYCLE_STEPS = 8

@Composable
fun PomodoroScreen(
    onScrollStateChanged: (isNavVisible: Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 80.dp),
    pomodoroViewModel: PomodoroViewModel = viewModel()
) {
    val context = LocalContext.current
    val timerState by pomodoroViewModel.timerState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )

    LaunchedEffect(Unit) {
        onScrollStateChanged(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    var sessionTitle by rememberSaveable { mutableStateOf("Focus Session") }
    var isEditingTitle by rememberSaveable { mutableStateOf(false) }

    var focusDurationSec by rememberSaveable { mutableIntStateOf(25 * 60) }
    var shortBreakDurationSec by rememberSaveable { mutableIntStateOf(5 * 60) }
    var longBreakDurationSec by rememberSaveable { mutableIntStateOf(15 * 60) }

    var phaseToEdit by remember { mutableStateOf<PomodoroPhase?>(null) }

    // Read state from service ViewModel
    val isSessionActive = timerState.isSessionActive
    val currentStepIndex = timerState.currentStepIndex

    val currentPhase = remember(currentStepIndex) {
        when {
            currentStepIndex % 2 == 0 -> PomodoroPhase.FOCUS
            currentStepIndex == 7 -> PomodoroPhase.LONG_BREAK
            else -> PomodoroPhase.SHORT_BREAK
        }
    }

    val isBreakMode = currentPhase != PomodoroPhase.FOCUS

    fun getDurationForStep(stepIndex: Int): Int {
        return when {
            stepIndex % 2 == 0 -> focusDurationSec
            stepIndex == 7 -> longBreakDurationSec
            else -> shortBreakDurationSec
        }
    }

    val totalDurationForCurrentStep = getDurationForStep(currentStepIndex)
    val remainingSeconds = if (isSessionActive) timerState.remainingSeconds else totalDurationForCurrentStep
    val progress = if (isSessionActive) timerState.progress else 0f
    val isPlaying = timerState.isRunning

    // Auto-advance step when background timer finishes
    LaunchedEffect(timerState.remainingSeconds, timerState.isRunning, isSessionActive) {
        if (isSessionActive && timerState.remainingSeconds == 0 && !timerState.isRunning) {
            val nextStep = (currentStepIndex + 1) % TOTAL_CYCLE_STEPS
            val nextDuration = getDurationForStep(nextStep)
            val nextPhase = when {
                nextStep % 2 == 0 -> PomodoroPhase.FOCUS
                nextStep == 7 -> PomodoroPhase.LONG_BREAK
                else -> PomodoroPhase.SHORT_BREAK
            }
            pomodoroViewModel.startTimer(
                context = context,
                totalSeconds = nextDuration,
                phase = nextPhase,
                title = sessionTitle,
                stepIndex = nextStep
            )
        }
    }

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
                currentPhase = if (isSessionActive) timerState.phase else currentPhase,
                remainingSeconds = remainingSeconds,
                progress = progress,
                sessionTitle = sessionTitle,
                isEditingTitle = isEditingTitle,
                isSessionActive = isSessionActive,
                onTitleChange = { sessionTitle = it },
                onToggleEditTitle = { isEditingTitle = !isEditingTitle },
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Middle Container
            AnimatedContent(
                targetState = isSessionActive,
                contentAlignment = Alignment.TopCenter,
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
                        onPhaseSelected = { },
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

            Spacer(modifier = Modifier.weight(1f))

            // 3. Bottom Action Bar
            PomodoroActionButtons(
                isSessionActive = isSessionActive,
                isPlaying = isPlaying,
                isBreakMode = isBreakMode,
                onStartSession = {
                    pomodoroViewModel.startTimer(
                        context = context,
                        totalSeconds = totalDurationForCurrentStep,
                        phase = currentPhase,
                        title = sessionTitle,
                        stepIndex = currentStepIndex
                    )
                },
                onPlayPauseClick = {
                    if (isPlaying) {
                        pomodoroViewModel.pauseTimer(context)
                    } else {
                        pomodoroViewModel.resumeTimer(context)
                    }
                },
                onResetClick = {
                    pomodoroViewModel.startTimer(
                        context = context,
                        totalSeconds = totalDurationForCurrentStep,
                        phase = currentPhase,
                        title = sessionTitle,
                        stepIndex = currentStepIndex
                    )
                },
                onSkipClick = {
                    val nextStep = (currentStepIndex + 1) % TOTAL_CYCLE_STEPS
                    val nextDuration = getDurationForStep(nextStep)
                    val nextPhase = when {
                        nextStep % 2 == 0 -> PomodoroPhase.FOCUS
                        nextStep == 7 -> PomodoroPhase.LONG_BREAK
                        else -> PomodoroPhase.SHORT_BREAK
                    }
                    pomodoroViewModel.startTimer(
                        context = context,
                        totalSeconds = nextDuration,
                        phase = nextPhase,
                        title = sessionTitle,
                        stepIndex = nextStep
                    )
                },
                onStopClick = {
                    pomodoroViewModel.stopTimer(context)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}