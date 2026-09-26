package com.example.spaced.ui.components.session

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.theme.ListItemTextStyle
import com.example.spaced.ui.theme.NormalFontFamily

data class SessionLogItem(
    val id: String,
    val label: String,
    val date: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSessionHistoryList(
    modifier: Modifier = Modifier,
    logs: List<SessionLogItem> = defaultSessionLogs
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(24.dp))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(logs, key = { _, item -> item.id }) { index, item ->
                SegmentedListItem(
                    modifier = Modifier.height(58.dp), // Original uncompressed row height
                    shapes = ListItemDefaults.segmentedShapes(
                        index = index,
                        count = logs.size
                    ),
                    colors = ListItemDefaults.segmentedColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
                    ),
                    leadingContent = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    overlineContent = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontFamily = NormalFontFamily,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    content = {
                        Text(
                            text = item.date,
                            fontSize = 15.sp,
                            style = ListItemTextStyle,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                )
            }
        }
    }
}

val defaultSessionLogs = listOf(
    SessionLogItem("1", "Studied on", "17 September 2026", Icons.Rounded.CalendarMonth),
    SessionLogItem("2", "Cycle 1", "20 September 2026", Icons.Rounded.History),
    SessionLogItem("3", "Cycle 2", "20 September 2026", Icons.Rounded.History),
    SessionLogItem("4", "Cycle 3", "23 September 2026", Icons.Rounded.History),
    SessionLogItem("5", "Cycle 4", "26 September 2026", Icons.Rounded.History),
    SessionLogItem("6", "Cycle 5", "29 September 2026", Icons.Rounded.History)
)