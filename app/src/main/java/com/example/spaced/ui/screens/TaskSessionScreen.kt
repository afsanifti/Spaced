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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.spaced.data.model.Task
import com.example.spaced.ui.components.navigation.ReviewTopBar
import com.example.spaced.ui.components.session.TaskSessionBottomBar
import com.example.spaced.ui.components.session.TaskSessionCard
import com.example.spaced.ui.components.session.TaskSessionHistoryList
import com.example.spaced.ui.viewmodels.TaskSessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskSessionScreen(
    task: Task,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskSessionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val sectionSpacing = 16.dp

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
                isPlaying = uiState.isPlaying,
                isBreakMode = uiState.isBreak,
                onPlayPauseClick = { viewModel.togglePlayPause() },
                onResetClick = { viewModel.resetTimer() },
                onSkipClick = { viewModel.skipBreak() }, // 👈 Handled break skip action
                onStopClick = { viewModel.stopSession() },
                modifier = Modifier.navigationBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(sectionSpacing)
        ) {
            TaskSessionCard(
                task = task,
                formattedTime = uiState.formattedTime,
                progress = uiState.progress,
                currentCycle = uiState.currentCycle,
                totalCycles = uiState.totalCycles,
                isBreakMode = uiState.isBreak,
                modeLabel = uiState.modeLabel,
                modifier = Modifier.fillMaxWidth()
            )

            TaskSessionHistoryList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}