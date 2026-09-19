package com.example.spaced.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.components.SubjectTagBottomSheet
import com.example.spaced.ui.components.DifficultySelectorSection
import com.example.spaced.ui.components.StudiedOnSection
import com.example.spaced.ui.components.SubjectTagAndChapterRow
import com.example.spaced.ui.components.TrackTopBar
import com.example.spaced.ui.components.getTodayFormatted
import com.example.spaced.ui.theme.HighlightedLabelTextStyle
import com.example.spaced.ui.theme.TextFieldTitleTextStyle
import com.example.spaced.ui.theme.TrackTextStyle
import com.example.spaced.ui.utils.TaskDifficulty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    onCloseClick: () -> Unit = {},
    onStartTrackingClick: () -> Unit = {}
) {
    var taskTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }

    // Tag Selection State - Starts empty for user-created tags
    var availableTags by remember { mutableStateOf(emptyList<String>()) }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    var showSubjectTagSheet by remember { mutableStateOf(false) }

    var selectedChapter by remember { mutableStateOf("None") }
    var selectedDateText by remember { mutableStateOf(getTodayFormatted()) }
    var selectedDifficulty by remember { mutableStateOf(TaskDifficulty.EASY) }

    val primaryBg = MaterialTheme.colorScheme.primary
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val onSurface = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.7f)
    val fieldContainerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)

    val fieldShape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
    val itemShape = RoundedCornerShape(16.dp)

    val customFieldColors = TextFieldDefaults.colors(
        focusedContainerColor = fieldContainerColor,
        unfocusedContainerColor = fieldContainerColor,
        focusedIndicatorColor = onPrimaryColor,
        unfocusedIndicatorColor = onPrimaryColor,
        disabledIndicatorColor = Color.Transparent,
        focusedTextColor = MaterialTheme.colorScheme.secondaryContainer,
        unfocusedTextColor = MaterialTheme.colorScheme.secondaryContainer,
        focusedPlaceholderColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
        unfocusedPlaceholderColor = onPrimaryColor.copy(alpha = 0.6f)
    )

    val cyclesText = when (selectedDifficulty) {
        TaskDifficulty.EASY -> "5 Cycles"
        TaskDifficulty.MEDIUM -> "7 Cycles"
        TaskDifficulty.HARD -> "10 Cycles"
    }

    // Displays selected tag or defaults to "Select Tag" when none is chosen
    val subjectDisplayText = selectedTag ?: "Select Tag"

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = primaryBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TrackTopBar(
                onCloseClick = onCloseClick,
                primaryBg = primaryBg,
                onPrimaryColor = onPrimaryColor
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = 20.dp,
                    end = 20.dp,
                    bottom = innerPadding.calculateBottomPadding() + 4.dp
                ),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // GROUP 1: Task Title
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Task Title",
                    style = TextFieldTitleTextStyle,
                    color = onSurface
                )
                TextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    placeholder = { Text("e.g. Analog Communication") },
                    singleLine = true,
                    shape = fieldShape,
                    colors = customFieldColors,
                    trailingIcon = {
                        if (taskTitle.isNotEmpty()) {
                            IconButton(onClick = { taskTitle = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Clear field",
                                    tint = onPrimaryColor
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }

            // GROUP 2: Description
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Description (Optional)",
                    style = TextFieldTitleTextStyle,
                    color = onSurface
                )
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("e.g. Analog Communication") },
                    shape = fieldShape,
                    colors = customFieldColors,
                    trailingIcon = {
                        if (description.isNotEmpty()) {
                            IconButton(onClick = { description = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Clear field",
                                    tint = onPrimaryColor
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }

            // GROUP 3: Reference
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Reference (Optional)",
                    style = TextFieldTitleTextStyle,
                    color = onSurface
                )
                TextField(
                    value = reference,
                    onValueChange = { reference = it },
                    placeholder = { Text("e.g. Page Numbers, Writer") },
                    singleLine = true,
                    shape = fieldShape,
                    colors = customFieldColors,
                    trailingIcon = {
                        if (reference.isNotEmpty()) {
                            IconButton(onClick = { reference = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Clear field",
                                    tint = onPrimaryColor
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                )
            }

            // GROUP 4: Subject Tag & Chapter
            SubjectTagAndChapterRow(
                subjectDisplayText = subjectDisplayText,
                selectedChapter = selectedChapter,
                onSubjectTagClick = { showSubjectTagSheet = true },
                onChapterClick = { /* Select Chapter */ },
                onSurface = onSurface,
                onPrimaryColor = onPrimaryColor,
                fieldContainerColor = fieldContainerColor,
                itemShape = itemShape
            )

            // GROUP 5: Studied On
            StudiedOnSection(
                selectedDateText = selectedDateText,
                onDateSelected = { newDate ->
                    selectedDateText = newDate
                },
                onSurface = onSurface,
                onPrimaryColor = onPrimaryColor,
                fieldContainerColor = MaterialTheme.colorScheme.surfaceBright,
                itemShape = itemShape
            )

            // GROUP 6: Difficulty Selector
            DifficultySelectorSection(
                selectedDifficulty = selectedDifficulty,
                cyclesText = cyclesText,
                onDifficultySelected = { selectedDifficulty = it },
                onSurface = onSurface,
                onPrimaryColor = onPrimaryColor,
                primaryBg = primaryBg,
                fieldContainerColor = fieldContainerColor
            )

            Spacer(modifier = Modifier.weight(1f))

            // GROUP 7: Start Tracking Button
            Button(
                onClick = onStartTrackingClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = onPrimaryColor,
                    contentColor = primaryBg
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    "Start Tracking",
                    style = TrackTextStyle,
                    color = primaryBg
                )
            }
        }
    }

    // SUBJECT TAG BOTTOM SHEET
    if (showSubjectTagSheet) {
        SubjectTagBottomSheet(
            onDismissRequest = { showSubjectTagSheet = false },
            allAvailableTags = availableTags,
            currentlySelectedTag = selectedTag,
            onTagSelected = { newSelectedTag ->
                selectedTag = newSelectedTag
            },
            onNewTagCreated = { newTag ->
                if (!availableTags.contains(newTag)) {
                    availableTags = availableTags + newTag
                }
                selectedTag = newTag
            }
        )
    }
}