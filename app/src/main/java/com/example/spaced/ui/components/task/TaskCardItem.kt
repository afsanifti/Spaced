package com.example.spaced.ui.components.task

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntSize
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
        modifier = modifier
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
                onCollapseClick = onExpandToggle,
                onStartReviewClick = { onStartReviewClick(task) },
                onEditClick = { onEditTaskClick(task) },
                onDeleteClick = { onDeleteTaskClick(task) },
                onClick = onExpandToggle
            )
        } else {
            TaskCardCompressed(
                tag = task.tag,
                chapter = task.chapter,
                title = task.title,
                currentCycle = task.currentCycle,
                totalCycles = task.totalCycles,
                modifier = Modifier.fillMaxWidth(),
                onClick = onExpandToggle,
                onExpandClick = onExpandToggle
            )
        }
    }
}