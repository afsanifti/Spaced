package com.example.spaced.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.example.spaced.data.model.Task
import com.example.spaced.ui.components.navigation.ReviewTopBar
import com.example.spaced.ui.components.session.TaskSessionBottomBar
import com.example.spaced.ui.components.session.TaskSessionCard
import com.example.spaced.ui.components.session.TaskSessionHistoryList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSessionScreen(
    task: Task,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlaying by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ReviewTopBar(
                onBackClick = onCloseClick,
                primaryBg = MaterialTheme.colorScheme.surface,
                onPrimaryColor = MaterialTheme.colorScheme.onSurface
            )
        },
        bottomBar = {
            TaskSessionBottomBar(
                isPlaying = isPlaying,
                onPlayPauseClick = { isPlaying = !isPlaying },
                onResetClick = { isPlaying = false },
                onStopClick = { isPlaying = false },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = (innerPadding.calculateTopPadding() - 12.dp).coerceAtLeast(0.dp), // 👈 Pulls Card closer to App Bar
                    bottom = innerPadding.calculateBottomPadding(),
                    start = 20.dp,
                    end = 20.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TaskSessionCard(
                task = task,
                modifier = Modifier.fillMaxWidth()
            )

            TaskSessionHistoryList(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}