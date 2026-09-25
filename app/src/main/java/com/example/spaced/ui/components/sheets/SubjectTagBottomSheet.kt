package com.example.spaced.ui.components.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.theme.MajorButtonTextStyle
import com.example.spaced.ui.theme.TaskTabTextStyle
import com.example.spaced.ui.theme.TextFieldTitleTextStyle
import com.example.spaced.ui.theme.TrackTextStyle

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SubjectTagBottomSheet(
    onDismissRequest: () -> Unit,
    allAvailableTags: List<String>,
    currentlySelectedTag: String?,
    onTagSelected: (String?) -> Unit,
    onNewTagCreated: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    var showCreateTagSheet by remember { mutableStateOf(false) }

    // Color definitions
    val sheetBg = MaterialTheme.colorScheme.primaryContainer
    val darkBlue = Color(0xFF00159E)
    val unselectedTagBg = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
    val textMuted = Color(0xFF2C3170)

    // MAIN "SELECT TAG" BOTTOM SHEET
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = sheetBg,
        contentWindowInsets = { WindowInsets(0) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // SHEET TITLE
            Text(
                text = "Select tag",
                fontSize = 18.sp,
                style = TrackTextStyle,
                color = MaterialTheme.colorScheme.onPrimary
            )

            // SELECTED TAG
            currentlySelectedTag?.let { selectedTag ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(darkBlue)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = selectedTag,
                            fontSize = 14.sp,
                            style = TaskTabTextStyle,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove tag",
                            tint = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    onTagSelected(null)
                                }
                        )
                    }
                }
            }

            // YOUR TAGS LIST
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Your tags",
                    fontSize = 14.sp,
                    style = TextFieldTitleTextStyle,
                    color = textMuted
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    allAvailableTags.forEach { tag ->
                        if (tag != currentlySelectedTag) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(unselectedTagBg)
                                    .clickable {
                                        onTagSelected(tag)
                                    }
                                    .padding(horizontal = 18.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = tag,
                                    fontSize = 14.sp,
                                    style = TaskTabTextStyle,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // BOTTOM ACTION BUTTONS: [+ Create new]  [Done]
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showCreateTagSheet = true },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Create new",
                            style = MajorButtonTextStyle,
                            fontSize = 14.sp
                        )
                    }
                }

                Button(
                    onClick = onDismissRequest,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Text(
                        text = "Done",
                        style = MajorButtonTextStyle,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    // SECONDARY "CREATE TAG" BOTTOM SHEET
    if (showCreateTagSheet) {
        CreateTagBottomSheet(
            onDismissRequest = { showCreateTagSheet = false },
            onTagSubmitted = { newTagName ->
                onNewTagCreated(newTagName)
                onTagSelected(newTagName)
                showCreateTagSheet = false
            },
            sheetBg = sheetBg,
            darkBlue = darkBlue
        )
    }
}