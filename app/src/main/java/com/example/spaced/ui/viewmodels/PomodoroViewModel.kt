package com.example.spaced.ui.viewmodels

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.spaced.service.PomodoroService
import com.example.spaced.service.PomodoroState
import com.example.spaced.ui.utils.PomodoroPhase
import kotlinx.coroutines.flow.StateFlow

class PomodoroViewModel : ViewModel() {

    val timerState: StateFlow<PomodoroState> = PomodoroService.pomodoroState

    fun startTimer(context: Context, totalSeconds: Int, phase: PomodoroPhase, title: String, stepIndex: Int = 0) {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_START
            putExtra(PomodoroService.EXTRA_TOTAL_SECONDS, totalSeconds)
            putExtra(PomodoroService.EXTRA_PHASE, phase.name)
            putExtra(PomodoroService.EXTRA_TITLE, title)
            putExtra(PomodoroService.EXTRA_STEP_INDEX, stepIndex)
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun pauseTimer(context: Context) {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_PAUSE
        }
        context.startService(intent)
    }

    fun resumeTimer(context: Context) {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_RESUME
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopTimer(context: Context) {
        val intent = Intent(context, PomodoroService::class.java).apply {
            action = PomodoroService.ACTION_STOP
        }
        context.startService(intent)
    }
}