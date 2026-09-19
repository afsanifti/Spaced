package com.example.spaced.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTagBottomSheet(
    onDismissRequest: () -> Unit,
    onTagSubmitted: (String) -> Unit,
    sheetBg: Color,
    darkBlue: Color
) {
    var newTagInput by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    // Auto-focus input when the bottom sheet appears so the keyboard opens immediately
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = sheetBg,
        contentWindowInsets = { BottomSheetDefaults.modalWindowInsets }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create tag",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = darkBlue
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = newTagInput,
                    onValueChange = { newTagInput = it },
                    placeholder = {
                        Text(
                            text = "Tag name",
                            color = darkBlue.copy(alpha = 0.6f),
                            fontSize = 16.sp
                        )
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = darkBlue,
                        unfocusedIndicatorColor = darkBlue.copy(alpha = 0.5f),
                        focusedTextColor = darkBlue,
                        unfocusedTextColor = darkBlue
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester)
                )

                IconButton(
                    onClick = {
                        if (newTagInput.isNotBlank()) {
                            onTagSubmitted(newTagInput.trim())
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Submit tag",
                        tint = darkBlue,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}