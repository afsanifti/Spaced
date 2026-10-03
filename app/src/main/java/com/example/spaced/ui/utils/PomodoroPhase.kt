package com.example.spaced.ui.utils

enum class PomodoroPhase {
    FOCUS,
    SHORT_BREAK,
    LONG_BREAK;

    val defaultDurationSeconds: Int
        get() = when (this) {
            FOCUS -> 25 * 60
            SHORT_BREAK -> 5 * 60
            LONG_BREAK -> 15 * 60
        }

    val nextPhase: PomodoroPhase
        get() = when (this) {
            FOCUS -> SHORT_BREAK
            SHORT_BREAK -> FOCUS
            LONG_BREAK -> FOCUS
        }
}