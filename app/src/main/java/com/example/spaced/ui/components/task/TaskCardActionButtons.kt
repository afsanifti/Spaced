package com.example.spaced.ui.components.task

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.example.spaced.ui.theme.TrackTextStyle

@Composable
fun TaskCardActionButtons(
    onStartReviewClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reviewInteraction = remember { MutableInteractionSource() }
    val editInteraction = remember { MutableInteractionSource() }
    val deleteInteraction = remember { MutableInteractionSource() }

    val reviewProgress = rememberPressProgress(reviewInteraction)
    val editProgress = rememberPressProgress(editInteraction)
    val deleteProgress = rememberPressProgress(deleteInteraction)

    // Interpolated weights
    val reviewWeight = 154f + (185f - 154f) * reviewProgress
    val editWeight = 80f + (105f - 80f) * editProgress
    val deleteWeight = 57f + (82f - 57f) * deleteProgress

    // Interpolated corner radii
    val reviewCornerRadius = lerp(40.dp, 16.dp, reviewProgress)
    val editCornerRadius = lerp(40.dp, 16.dp, editProgress)
    val deleteCornerRadius = lerp(40.dp, 16.dp, deleteProgress)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "Start" Button
        FilledTonalButton(
            onClick = onStartReviewClick,
            modifier = Modifier
                .weight(reviewWeight)
                .height(80.dp),
            shape = RoundedCornerShape(reviewCornerRadius),
            interactionSource = reviewInteraction,
            contentPadding = PaddingValues(horizontal = 8.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = "Start",
                    style = TrackTextStyle,
                    maxLines = 1
                )
            }
        }

        // Edit Button
        FilledTonalIconButton(
            onClick = onEditClick,
            modifier = Modifier
                .weight(editWeight)
                .height(80.dp),
            shape = RoundedCornerShape(editCornerRadius),
            interactionSource = editInteraction,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Task",
                modifier = Modifier.size(22.dp)
            )
        }

        // Delete Button
        FilledTonalIconButton(
            onClick = onDeleteClick,
            modifier = Modifier
                .weight(deleteWeight)
                .height(80.dp),
            shape = RoundedCornerShape(deleteCornerRadius),
            interactionSource = deleteInteraction,
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete Task",
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun rememberPressProgress(interactionSource: MutableInteractionSource): Float {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> {
                    progress.animateTo(
                        targetValue = 1f,
                        animationSpec = spring(
                            stiffness = Spring.StiffnessMedium,
                            dampingRatio = Spring.DampingRatioNoBouncy
                        )
                    )
                }
                is PressInteraction.Release, is PressInteraction.Cancel -> {
                    progress.animateTo(
                        targetValue = 0f,
                        initialVelocity = 0f, // Immediately kills forward expansion momentum
                        animationSpec = spring(
                            stiffness = Spring.StiffnessHigh, // Snappy collapse back on release
                            dampingRatio = Spring.DampingRatioNoBouncy
                        )
                    )
                }
            }
        }
    }

    return progress.value
}