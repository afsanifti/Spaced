package com.example.spaced.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.spaced.ui.components.TaskTab
import com.example.spaced.ui.components.TaskTabRow
import com.example.spaced.ui.components.TopDateHeader
import java.time.LocalDate

@Composable
fun HomeScreen(
    selectedDate: LocalDate,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(TaskTab.TODAY) }

    // Mock count data matching screen specifications
    val pendingCounts = remember {
        mapOf(
            TaskTab.TODAY to 0,
            TaskTab.UPCOMING to 4,
            TaskTab.MISSED to 4,
            TaskTab.PLANNED to 2
        )
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        TopDateHeader(
            selectedDate = selectedDate,
            onProfileClick = onProfileClick
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Scrollable Tabs
        TaskTabRow(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            taskCounts = pendingCounts
        )

        // Content Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${selectedTab.label} Content",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}