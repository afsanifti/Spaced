package com.example.spaced.ui.components.task

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.theme.TaskTabTextStyle
import kotlinx.coroutines.launch

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
    contentPadding: PaddingValues = PaddingValues(horizontal = 20.dp)
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val tabs = remember { TaskTab.entries.toTypedArray() }

    LazyRow(
        state = lazyListState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(tabs, key = { _, tab -> tab.name }) { index, tab ->
            val count = taskCounts[tab] ?: 0
            val isSelected = tab == selectedTab

            TaskTabPill(
                tab = tab,
                count = count,
                isSelected = isSelected,
                onClick = {
                    onTabSelected(tab)
                    coroutineScope.launch {
                        lazyListState.animateScrollToItem(index)
                    }
                }
            )
        }
    }
}

@Composable
private fun TaskTabPill(
    tab: TaskTab,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Snappy spring physics for scale transitions
    val snappySpring = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // Dynamic scale highlight effect
    val scale by animateFloatAsState(
        targetValue = when {
            isPressed -> 0.95f
            isSelected -> 1.05f
            else -> 1.0f
        },
        animationSpec = snappySpring,
        label = "TabScale"
    )

    // Animated container and label colors
    val containerColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.secondary.copy(alpha = 0.9f),
        animationSpec = tween(durationMillis = 180),
        label = "TabContainerColor"
    )

    val labelTextColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.onSecondary,
        animationSpec = tween(durationMillis = 180),
        label = "TabLabelColor"
    )

    val badgeBgColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
    val badgeTextColor = MaterialTheme.colorScheme.inverseSurface

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .height(34.dp)
            .widthIn(min = 110.dp)
            .clip(CircleShape)
            .background(containerColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        if (count > 0) {
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
        } else {
            Text(
                text = tab.label,
                style = TaskTabTextStyle,
                color = labelTextColor,
                softWrap = false,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 12.dp)
            )
        }
    }
}