package com.example.spaced.service

import com.example.spaced.ui.utils.PomodoroPhase

data class PomodoroState(
    val phase: PomodoroPhase = PomodoroPhase.FOCUS,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val progress: Float = 1f,
    val isRunning: Boolean = false,
    val isSessionActive: Boolean = false,
    val currentStepIndex: Int = 0,
    val title: String = "Focus Session"
)