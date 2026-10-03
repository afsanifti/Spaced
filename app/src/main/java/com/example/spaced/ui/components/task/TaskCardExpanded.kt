package com.example.spaced.ui.components.task

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TaskCardExpanded(
    tag: String,
    title: String,
    currentCycle: Int,
    totalCycles: Int,
    modifier: Modifier = Modifier,
    chapter: String? = null,
    description: String? = null,
    studiedOnText: String = "Wednesday, September 16",
    difficultyText: String = "Medium · review every 3 days",
    nextCycleDueText: String = "Friday, September 18",
    onCollapseClick: () -> Unit = {},
    onStartReviewClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    TaskCard(
        isExpanded = true,
        tag = tag,
        title = title,
        currentCycle = currentCycle,
        totalCycles = totalCycles,
        modifier = modifier,
        chapter = chapter,
        description = description,
        studiedOnText = studiedOnText,
        difficultyText = difficultyText,
        nextCycleDueText = nextCycleDueText,
        onToggleExpand = onCollapseClick,
        onStartReviewClick = onStartReviewClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick,
        onClick = onClick
    )
}