package com.example.spaced.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class SessionMode {
    FOCUS, BREAK
}

data class SessionState(
    val mode: SessionMode = SessionMode.FOCUS,
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val isPlaying: Boolean = false,
    val currentCycle: Int = 1,
    val totalCycles: Int = 6
) {
    val isBreak: Boolean get() = mode == SessionMode.BREAK

    val progress: Float
        get() = if (totalSeconds > 0) 1f - (remainingSeconds.toFloat() / totalSeconds.toFloat()) else 0f

    val formattedTime: String
        get() {
            val minutes = remainingSeconds / 60
            val seconds = remainingSeconds % 60
            return "%02d : %02d".format(minutes, seconds)
        }

    val modeLabel: String
        get() = if (isBreak) "BREAK" else "FOCUS"
}

class TaskSessionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SessionState())
    val uiState: StateFlow<SessionState> = _uiState.asStateFlow()

    private var timerJob: Job? = null

    fun togglePlayPause() {
        if (_uiState.value.isPlaying) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun startTimer() {
        _uiState.update { it.copy(isPlaying = true) }
        runTimerLoop()
    }

    private fun runTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0 && _uiState.value.isPlaying) {
                delay(1000L)
                val current = _uiState.value.remainingSeconds - 1

                if (current <= 0) {
                    transitionMode()
                } else {
                    _uiState.update { it.copy(remainingSeconds = current) }
                }
            }
        }
    }

    private fun transitionMode() {
        _uiState.update { state ->
            if (state.mode == SessionMode.FOCUS) {
                state.copy(
                    mode = SessionMode.BREAK,
                    totalSeconds = 5 * 60,
                    remainingSeconds = 5 * 60
                )
            } else {
                val nextCycle = state.currentCycle + 1
                state.copy(
                    mode = SessionMode.FOCUS,
                    currentCycle = nextCycle,
                    totalSeconds = 25 * 60,
                    remainingSeconds = 25 * 60
                )
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isPlaying = false) }
    }

    /**
     * Resets timer during Focus mode: immediately triggers 5-min BREAK mode.
     */
    fun resetTimer() {
        timerJob?.cancel()
        _uiState.update {
            it.copy(
                mode = SessionMode.BREAK,
                totalSeconds = 5 * 60,
                remainingSeconds = 5 * 60,
                isPlaying = true
            )
        }
        runTimerLoop()
    }

    /**
     * Skips Break mode: returns to Focus mode for the next cycle immediately.
     */
    fun skipBreak() {
        timerJob?.cancel()
        _uiState.update { state ->
            val nextCycle = state.currentCycle + 1
            state.copy(
                mode = SessionMode.FOCUS,
                currentCycle = nextCycle,
                totalSeconds = 25 * 60,
                remainingSeconds = 25 * 60,
                isPlaying = true
            )
        }
        runTimerLoop()
    }

    fun stopSession() {
        timerJob?.cancel()
        _uiState.update {
            SessionState()
        }
    }
}