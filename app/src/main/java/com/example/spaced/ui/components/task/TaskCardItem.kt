package com.example.spaced.ui.components.task

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.spaced.data.model.Task

@Composable
fun TaskCardItem(
    task: Task,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onStartReviewClick: (Task) -> Unit,
    onEditTaskClick: (Task) -> Unit,
    onDeleteTaskClick: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    TaskCard(
        isExpanded = isExpanded,
        tag = task.tag,
        chapter = task.chapter,
        title = task.title,
        currentCycle = task.currentCycle,
        totalCycles = task.totalCycles,
        description = task.description,
        modifier = modifier.fillMaxWidth(),
        onToggleExpand = onExpandToggle,
        onStartReviewClick = { onStartReviewClick(task) },
        onEditClick = { onEditTaskClick(task) },
        onDeleteClick = { onDeleteTaskClick(task) },
        onClick = onExpandToggle
    )
}