package com.example.spaced.ui.components.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.data.model.Task
import com.example.spaced.ui.theme.NormalFontFamily
import com.example.spaced.ui.theme.TagAndChapTextStyle
import com.example.spaced.ui.theme.TaskTitleTextStyle
import com.example.spaced.ui.theme.TimerTextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSessionCard(
    task: Task,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val strokePx = with(density) { 10.dp.toPx() }

    AmbientMeshCard(
        modifier = modifier
            .fillMaxWidth()
            .height(369.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Details Header
            Column(
                modifier = Modifier.padding(top = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Ph 1109 · Chapter 2",
                    style = TagAndChapTextStyle,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                )
                Text(
                    text = task.title.ifEmpty { "8085 Microprocessor" },
                    style = TaskTitleTextStyle,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "My notes, Book page 112",
                    style = TagAndChapTextStyle,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // 32dp Gap from 'My notes, Book page' section
            Spacer(modifier = Modifier.height(28.dp))

            // Material 3 Expressive Wavy Circular Progress Indicator
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(154.dp)
            ) {
                CircularWavyProgressIndicator(
                    progress = { 0.25f },
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.secondaryContainer,
                    stroke = Stroke(width = strokePx, cap = StrokeCap.Round),
                    trackStroke = Stroke(width = strokePx, cap = StrokeCap.Round),
                    wavelength = 36.dp,
                    gapSize = 6.dp
                )

                Text(
                    text = "24 : 30",
                    style = TimerTextStyle,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Cycle and Focus container
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = "Cycle 3 of 6",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NormalFontFamily,
                        ),
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Text(
                        text = "FOCUS",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = NormalFontFamily,
                        ),
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}