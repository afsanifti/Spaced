package com.example.spaced.ui.screens

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.R
import com.example.spaced.ui.theme.HighlightedLabelTextStyle
import com.example.spaced.ui.theme.MajorButtonTextStyle
import com.example.spaced.ui.theme.NormalFontFamily
import com.example.spaced.ui.theme.TextFieldTitleTextStyle
import com.example.spaced.ui.theme.TopDateHeaderTextStyle
import com.example.spaced.ui.theme.TrackTextStyle

enum class TaskDifficulty {
    EASY, MEDIUM, HARD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackScreen(
    onCloseClick: () -> Unit = {},
    onStartTrackingClick: () -> Unit = {}
) {
    var taskTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("ECE 2201") }
    var selectedChapter by remember { mutableStateOf("None") }
    var selectedDateText by remember { mutableStateOf("17 September 2026") }
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
        focusedIndicatorColor = onPrimaryColor,      // Restores active bottom line
        unfocusedIndicatorColor = onPrimaryColor,    // Restores default bottom line
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = primaryBg,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(top = 24.dp),
                title = {
                    Text(
                        text = "Track",
                        style = TopDateHeaderTextStyle
                    )
                },
                navigationIcon = {
                    Surface(
                        modifier = Modifier
                            .padding(start = 12.dp, end = 8.dp)
                            .size(36.dp),
                        shape = CircleShape,
                        color = onPrimaryColor.copy(alpha = 0.15f)
                    ) {
                        IconButton(onClick = onCloseClick) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = onPrimaryColor
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryBg,
                    titleContentColor = onPrimaryColor
                )
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
                    bottom = innerPadding.calculateBottomPadding() + 4.dp // 4.dp gap from navigation bar
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
                    text = "Reference",
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Subject Tag",
                        style = TextFieldTitleTextStyle,
                        color = onSurface
                    )
                    ListItem(
                        headlineContent = {
                            Text(
                                text = selectedSubject,
                                fontSize = 13.sp,
                                style = HighlightedLabelTextStyle,
                                color = onPrimaryColor
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.Label,
                                contentDescription = "Subject Tag",
                                tint = onPrimaryColor
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = onPrimaryColor
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = fieldContainerColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(itemShape)
                            .clickable { /* Select Subject Tag */ }
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Chapter (Optional)",
                        style = TextFieldTitleTextStyle,
                        color = onSurface
                    )
                    ListItem(
                        headlineContent = {
                            Text(
                                text = selectedChapter,
                                fontSize = 14.sp,
                                style = HighlightedLabelTextStyle,
                                color = onPrimaryColor
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.BookmarkBorder,
                                contentDescription = "Chapter",
                                tint = onPrimaryColor
                            )
                        },
                        trailingContent = {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Dropdown",
                                tint = onPrimaryColor
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = fieldContainerColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(itemShape)
                            .clickable { /* Select Chapter */ }
                    )
                }
            }

            // GROUP 5: Studied On
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Studied On",
                    style = TextFieldTitleTextStyle,
                    color = onSurface
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            text = selectedDateText,
                            fontSize = 14.sp,
                            style = HighlightedLabelTextStyle,
                            color = onPrimaryColor
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarToday,
                            contentDescription = "Select Date",
                            tint = onPrimaryColor
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                            contentDescription = "Open Date Picker",
                            tint = onPrimaryColor
                        )
                    },
                    onClick = { /* Handle Date Picker trigger */ },
                    colors = MenuDefaults.itemColors(textColor = onPrimaryColor),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(itemShape)
                        .background(fieldContainerColor)
                )
            }

            // GROUP 6: Difficulty Selector
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
                                .clickable { selectedDifficulty = difficultyEnum },
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

            // Flexible space pushes the button neatly to the bottom
            Spacer(modifier = Modifier.weight(1f))

            // GROUP 7: Start Tracking Button (4.dp above navigation bar)
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
}