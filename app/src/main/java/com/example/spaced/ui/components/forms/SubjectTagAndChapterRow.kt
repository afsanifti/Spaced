package com.example.spaced.ui.components.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spaced.ui.theme.HighlightedLabelTextStyle
import com.example.spaced.ui.theme.TextFieldTitleTextStyle

@Composable
fun SubjectTagAndChapterRow(
    subjectDisplayText: String,
    selectedChapter: String,
    onSubjectTagClick: () -> Unit,
    onChapterClick: () -> Unit,
    onSurface: Color,
    onPrimaryColor: Color,
    fieldContainerColor: Color,
    itemShape: CornerBasedShape
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Subject Tag Selector
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
                        text = subjectDisplayText,
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
                    .clickable { onSubjectTagClick() }
            )
        }

        // Chapter Selector
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
                    .clickable { onChapterClick() }
            )
        }
    }
}