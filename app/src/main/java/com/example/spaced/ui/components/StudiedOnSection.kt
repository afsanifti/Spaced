package com.example.spaced.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import com.example.spaced.ui.theme.HighlightedLabelTextStyle
import com.example.spaced.ui.theme.TextFieldTitleTextStyle
import com.example.spaced.ui.theme.TrackTextStyle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun getTodayFormatted(): String {
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    return formatter.format(Date())
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudiedOnSection(
    selectedDateText: String,
    onDateSelected: (String) -> Unit,
    onSurface: Color,
    onPrimaryColor: Color,
    fieldContainerColor: Color,
    itemShape: CornerBasedShape,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
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
            onClick = { showDatePicker = true },
            colors = MenuDefaults.itemColors(textColor = onPrimaryColor),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(itemShape)
                .background(MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f))
        )
    }

    if (showDatePicker) {
        CustomColoredDatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { millis ->
                millis?.let {
                    val formattedDate = formatDate(it)
                    onDateSelected(formattedDate)
                }
                showDatePicker = false
            }
        )
    }
}

private fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomColoredDatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Long?) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    // 1. CUSTOM FONTS / TYPOGRAPHY OVERRIDE
    // DatePicker reads typography from MaterialTheme.typography slots directly
    val customDatePickerTypography = MaterialTheme.typography.copy(
        labelMedium = HighlightedLabelTextStyle.copy(fontSize = 14.sp),   // Title label (e.g., "Select date")
        headlineLarge = TrackTextStyle.copy(fontSize = 32.sp), // Large header date text
        headlineMedium = HighlightedLabelTextStyle.copy(fontSize = 28.sp),
        titleSmall = HighlightedLabelTextStyle.copy(fontSize = 16.sp),    // Month/Year title (e.g., "September 2026")
        bodyLarge = HighlightedLabelTextStyle.copy(fontSize = 14.sp),     // Weekdays (S, M, T, W, T, F, S)
        bodyMedium = HighlightedLabelTextStyle.copy(fontSize = 14.sp),    // Calendar day numbers
        labelLarge = HighlightedLabelTextStyle.copy(fontSize = 14.sp)     // Action buttons (OK/Cancel)
    )

    // 2. CUSTOM COLORS
    val customDatePickerColors = DatePickerDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,  // Picker background
        titleContentColor = MaterialTheme.colorScheme.onSurface,              // "Select date" title
        headlineContentColor = MaterialTheme.colorScheme.onSurface,                 // Selected date header text
        subheadContentColor = MaterialTheme.colorScheme.onSurface,                  // Month title text
        navigationContentColor = MaterialTheme.colorScheme.onSurface,               // Arrow icons
        weekdayContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),            // Weekdays (S, M, T, W, T, F, S)
        dayContentColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),                      // Day numbers
        disabledDayContentColor = Color(0xFF49454F),        // Disabled day numbers
        selectedDayContainerColor = MaterialTheme.colorScheme.primary,      // Circle background around selected day
        selectedDayContentColor = MaterialTheme.colorScheme.onPrimary,              // Selected day text color
        todayContentColor = Color(0xFFD6E2FF),              // Today's date text
        todayDateBorderColor = MaterialTheme.colorScheme.primary,          // Outline ring around today's date
        yearContentColor = Color(0xFFCAC4D0),               // Year text in year list
        currentYearContentColor = MaterialTheme.colorScheme.primary,
        selectedYearContainerColor = MaterialTheme.colorScheme.primary,
        selectedYearContentColor = MaterialTheme.colorScheme.onPrimary,
        dividerColor = Color.Transparent                 // Horizontal divider line
    )

    // Wrap in local MaterialTheme to apply the typography overrides to DatePicker
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = customDatePickerTypography
    ) {
        DatePickerDialog(
            onDismissRequest = onDismissRequest,
            confirmButton = {
                TextButton(
                    onClick = {
                        onDateSelected(datePickerState.selectedDateMillis)
                    }
                ) {
                    Text("OK", color = Color(0xFFD6E2FF), style = HighlightedLabelTextStyle)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel", color = Color(0xFFD6E2FF), style = HighlightedLabelTextStyle)
                }
            },
            colors = customDatePickerColors
        ) {
            DatePicker(
                state = datePickerState,
                colors = customDatePickerColors
            )
        }
    }
}