package com.example.spaced.ui.components.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.theme.TaskTabTextStyle

enum class TaskTab(val label: String) {
    TODAY("TODAY"),
    UPCOMING("UPCOMING"),
    MISSED("MISSED"),
    PLANNED("PLANNED")
}

@Composable
fun TaskTabRow(
    selectedTab: TaskTab,
    onTabSelected: (TaskTab) -> Unit,
    modifier: Modifier = Modifier,
    taskCounts: Map<TaskTab, Int> = emptyMap(),
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp) // Added to allow edge-to-edge scrolling aligned with cards
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TaskTab.entries.toTypedArray()) { tab ->
            val count = taskCounts[tab] ?: 0
            val isSelected = tab == selectedTab

            TaskTabPill(
                tab = tab,
                count = count,
                isSelected = isSelected,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
private fun TaskTabPill(
    tab: TaskTab,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f)
    val labelTextColor = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSecondary
    val badgeBgColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    val badgeTextColor = MaterialTheme.colorScheme.inverseSurface

    if (count > 0) {
        Box(
            modifier = Modifier
                .height(34.dp)
                .widthIn(min = 110.dp)
                .clip(CircleShape)
                .background(containerColor)
                .clickable { onClick() }
        ) {
            Text(
                text = tab.label,
                style = TaskTabTextStyle,
                color = labelTextColor,
                softWrap = false,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp, end = 36.dp)
            )

            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .clip(CircleShape)
                    .background(badgeBgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    style = TaskTabTextStyle.copy(fontSize = 12.sp),
                    color = badgeTextColor
                )
            }
        }
    } else {
        Box(
            modifier = Modifier
                .height(34.dp)
                .widthIn(min = 110.dp)
                .clip(CircleShape)
                .background(containerColor)
                .clickable { onClick() }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tab.label,
                style = TaskTabTextStyle,
                color = labelTextColor,
                softWrap = false
            )
        }
    }
}