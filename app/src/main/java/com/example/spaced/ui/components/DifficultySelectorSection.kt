package com.example.spaced.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.utils.TaskDifficulty
import com.example.spaced.ui.theme.MajorButtonTextStyle
import com.example.spaced.ui.theme.NormalFontFamily
import com.example.spaced.ui.theme.TextFieldTitleTextStyle

@Composable
fun DifficultySelectorSection(
    selectedDifficulty: TaskDifficulty,
    cyclesText: String,
    onDifficultySelected: (TaskDifficulty) -> Unit,
    onSurface: Color,
    onPrimaryColor: Color,
    primaryBg: Color,
    fieldContainerColor: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Difficulty",
                style = TextFieldTitleTextStyle,
                color = onSurface
            )
            Text(
                text = cyclesText,
                fontFamily = NormalFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = onPrimaryColor
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val difficulties = listOf(
                TaskDifficulty.EASY to "Easy",
                TaskDifficulty.MEDIUM to "Medium",
                TaskDifficulty.HARD to "Hard"
            )

            difficulties.forEach { (difficultyEnum, label) ->
                val isSelected = selectedDifficulty == difficultyEnum

                val (startRadius, endRadius) = when {
                    isSelected -> 20.dp to 20.dp
                    difficultyEnum == TaskDifficulty.EASY -> 20.dp to 6.dp
                    difficultyEnum == TaskDifficulty.MEDIUM -> 6.dp to 6.dp
                    difficultyEnum == TaskDifficulty.HARD -> 6.dp to 20.dp
                    else -> 6.dp to 6.dp
                }

                val animatedStartRadius by animateDpAsState(
                    targetValue = startRadius,
                    animationSpec = spring(stiffness = 500f),
                    label = "startRadius"
                )

                val animatedEndRadius by animateDpAsState(
                    targetValue = endRadius,
                    animationSpec = spring(stiffness = 500f),
                    label = "endRadius"
                )

                val buttonContainerColor by animateColorAsState(
                    targetValue = if (isSelected) onPrimaryColor else fieldContainerColor,
                    animationSpec = spring(stiffness = 500f),
                    label = "containerColor"
                )

                val textColor by animateColorAsState(
                    targetValue = if (isSelected) primaryBg else onPrimaryColor,
                    label = "textColor"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(
                            RoundedCornerShape(
                                topStart = animatedStartRadius,
                                bottomStart = animatedStartRadius,
                                topEnd = animatedEndRadius,
                                bottomEnd = animatedEndRadius
                            )
                        )
                        .background(buttonContainerColor)
                        .clickable { onDifficultySelected(difficultyEnum) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        style = MajorButtonTextStyle,
                        color = textColor,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}