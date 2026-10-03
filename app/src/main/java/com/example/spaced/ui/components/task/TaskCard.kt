package com.example.spaced.ui.components.task

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.theme.NormalFontFamily
import com.example.spaced.ui.theme.TagAndChapTextStyle
import com.example.spaced.ui.theme.TaskTitleTextStyle
import com.example.spaced.ui.theme.TrackFontFamily

@Composable
fun TaskCard(
    isExpanded: Boolean,
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
    onToggleExpand: () -> Unit = {},
    onStartReviewClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val categoryText = if (!chapter.isNullOrBlank()) "$tag · $chapter" else tag
    val progressRatio = if (totalCycles > 0) currentCycle.toFloat() / totalCycles else 0f

    // Spring spec for Dp values (Padding, Corner Radius)
    val sharedDpSpring = spring<Dp>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    // Spring spec for animateContentSize (IntSize)
    val sharedIntSizeSpring = spring<IntSize>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    // Spring spec for Float values (Fading)
    val sharedFloatSpring = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    // Hardware-accelerated arrow rotation
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "ArrowRotation"
    )

    // Animated container corner radius
    val cardCornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 36.dp else 70.dp,
        animationSpec = sharedDpSpring,
        label = "CardCornerRadius"
    )

    // Animated bottom padding avoids sudden jumps on collapse
    val bottomPadding by animateDpAsState(
        targetValue = if (isExpanded) 32.dp else 24.dp,
        animationSpec = sharedDpSpring,
        label = "CardBottomPadding"
    )

    val cardShape = RoundedCornerShape(cardCornerRadius)

    Card(
        modifier = modifier
            .widthIn(max = 380.dp)
            .fillMaxWidth()
            .clip(cardShape)
            .animateContentSize(animationSpec = sharedIntSizeSpring)
            .clickable(onClick = onClick),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomPadding)
        ) {
            // --- HEADER ROW ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 36.dp, end = 24.dp, top = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = categoryText,
                        style = TagAndChapTextStyle,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        style = TaskTitleTextStyle,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Compact progress bar shown only when collapsed
                    AnimatedVisibility(
                        visible = !isExpanded,
                        enter = fadeIn(animationSpec = sharedFloatSpring) + expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)),
                        exit = fadeOut(animationSpec = sharedFloatSpring) + shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                LinearProgressIndicator(
                                    progress = { progressRatio },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(5.dp)
                                        .clip(CircleShape),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.secondaryContainer,
                                    strokeCap = StrokeCap.Round
                                )
                                Text(
                                    text = "$currentCycle/$totalCycles",
                                    style = TagAndChapTextStyle,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // --- PINNED ARROW BUTTON ---
                val collapseInteraction = remember { MutableInteractionSource() }
                val isPressed by collapseInteraction.collectIsPressedAsState()
                val buttonShape = if (isPressed) MaterialTheme.shapes.small else RoundedCornerShape(50.dp)

                Box(
                    modifier = Modifier
                        .size(width = 46.dp, height = 34.dp)
                        .clip(buttonShape)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        .clickable(
                            interactionSource = collapseInteraction,
                            indication = null,
                            onClick = onToggleExpand
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Collapse Task" else "Expand Task",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.graphicsLayer {
                            rotationZ = rotationAngle
                        }
                    )
                }
            }

            // --- EXPANDED DETAILS BODY ---
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeIn(animationSpec = sharedFloatSpring),
                exit = shrinkVertically(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) + fadeOut(animationSpec = sharedFloatSpring)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 36.dp, end = 36.dp, top = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    if (!description.isNullOrBlank()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Studied on",
                                    style = TextStyle(fontFamily = NormalFontFamily, fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = studiedOnText,
                                    style = TextStyle(fontFamily = TrackFontFamily, fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = "Difficulty",
                                    style = TextStyle(fontFamily = NormalFontFamily, fontSize = 12.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = difficultyText,
                                    style = TextStyle(fontFamily = TrackFontFamily, fontSize = 14.sp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Review cycles",
                                style = TagAndChapTextStyle,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "$currentCycle / $totalCycles",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.secondaryContainer,
                            strokeCap = StrokeCap.Round
                        )

                        Text(
                            text = "Next cycle due $nextCycleDueText",
                            style = TextStyle(fontFamily = NormalFontFamily, fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }

                    TaskCardActionButtons(
                        onStartReviewClick = onStartReviewClick,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                }
            }
        }
    }
}