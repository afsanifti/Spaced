package com.example.spaced.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.spaced.data.model.Task
import com.example.spaced.ui.components.TopDateHeader
import com.example.spaced.ui.components.task.TaskCardCompressed
import com.example.spaced.ui.components.task.TaskCardExpanded
import com.example.spaced.ui.components.task.TaskTab
import com.example.spaced.ui.components.task.TaskTabRow
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    selectedDate: LocalDate,
    tasks: List<Task> = emptyList(),
    modifier: Modifier = Modifier,
    onScrollStateChanged: (isAtTop: Boolean) -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTaskClick: (Task) -> Unit = {},
    onStartReviewClick: (Task) -> Unit = {},
    onEditTaskClick: (Task) -> Unit = {},
    onDeleteTaskClick: (Task) -> Unit = {}
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val listState = rememberLazyListState()

    var selectedTab by remember { mutableStateOf(TaskTab.TODAY) }
    var expandedTaskId by remember { mutableStateOf<String?>(null) }

    val isAtTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 10
        }
    }

    LaunchedEffect(isAtTop) {
        onScrollStateChanged(isAtTop)
    }

    // Calculate dynamic bottom padding including system navigation bar inset
    val navBarBottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = { TopDateHeader(selectedDate = selectedDate, onProfileClick = onProfileClick) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 38.dp,
                // 200.dp clearance + system gesture bar inset to push last card above FAB & Nav Bar
                bottom = 200.dp + navBarBottomInset
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 1. Edge-to-Edge Tab Row
            item {
                TaskTabRow(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it },
                    taskCounts = mapOf(
                        TaskTab.TODAY to tasks.size,
                        TaskTab.UPCOMING to 4,
                        TaskTab.MISSED to 4
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp)
                )
            }

            // 2. Task Cards
            items(
                items = tasks,
                key = { task -> task.id }
            ) { task ->
                val isExpanded = expandedTaskId == task.id

                AnimatedContent(
                    targetState = isExpanded,
                    transitionSpec = {
                        val spatialSpring = spring<IntSize>(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )

                        val enterTransition = fadeIn(
                            animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
                        ) + scaleIn(
                            initialScale = 0.96f,
                            animationSpec = tween(durationMillis = 200, easing = LinearOutSlowInEasing)
                        )

                        val exitTransition = fadeOut(
                            animationSpec = tween(durationMillis = 180, easing = FastOutLinearInEasing)
                        ) + scaleOut(
                            targetScale = 0.96f,
                            animationSpec = tween(durationMillis = 180, easing = FastOutLinearInEasing)
                        )

                        (enterTransition togetherWith exitTransition).using(
                            SizeTransform(
                                clip = true,
                                sizeAnimationSpec = { _, _ -> spatialSpring }
                            )
                        )
                    },
                    label = "TaskCardMaterialExpressiveTransition",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .animateItem()
                ) { targetExpanded ->
                    if (targetExpanded) {
                        TaskCardExpanded(
                            tag = task.tag,
                            chapter = task.chapter,
                            title = task.title,
                            currentCycle = task.currentCycle,
                            totalCycles = task.totalCycles,
                            description = task.description,
                            modifier = Modifier.fillMaxWidth(),
                            onCollapseClick = { expandedTaskId = null },
                            onStartReviewClick = { onStartReviewClick(task) },
                            onEditClick = { onEditTaskClick(task) },
                            onDeleteClick = { onDeleteTaskClick(task) },
                            onClick = { expandedTaskId = null }
                        )
                    } else {
                        TaskCardCompressed(
                            tag = task.tag,
                            chapter = task.chapter,
                            title = task.title,
                            currentCycle = task.currentCycle,
                            totalCycles = task.totalCycles,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { expandedTaskId = task.id },
                            onExpandClick = { expandedTaskId = task.id }
                        )
                    }
                }
            }
        }
    }
}