package com.example.spaced.ui.components.task

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TaskCardCompressed(
    tag: String,
    title: String,
    currentCycle: Int,
    totalCycles: Int,
    modifier: Modifier = Modifier,
    chapter: String? = null,
    onExpandClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    TaskCard(
        isExpanded = false,
        tag = tag,
        title = title,
        currentCycle = currentCycle,
        totalCycles = totalCycles,
        modifier = modifier,
        chapter = chapter,
        onToggleExpand = onExpandClick,
        onClick = onClick
    )
}