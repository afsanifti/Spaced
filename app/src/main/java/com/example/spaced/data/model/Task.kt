package com.example.spaced.data.model

import com.example.spaced.ui.utils.TaskDifficulty

data class Task(
    val id: String,
    val title: String,
    val tag: String,
    val chapter: String? = null,
    val currentCycle: Int = 1,
    val totalCycles: Int = 5,
    val description: String? = null,
    val reference: String? = null,
    val difficulty: TaskDifficulty = TaskDifficulty.EASY
)