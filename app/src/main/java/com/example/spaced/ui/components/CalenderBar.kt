package com.example.spaced.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val INITIAL_PAGE = 10000

@Composable
fun CalendarBar(
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit = {},
    isExpanded: Boolean = false,
    onExpandToggle: () -> Unit = {}
) {
    var displayedMonth by remember(selectedDate) { mutableStateOf(YearMonth.from(selectedDate)) }

    val anchorDate = remember { LocalDate.now() }
    val baseStartOfWeek = remember(anchorDate) {
        anchorDate.minusDays(anchorDate.dayOfWeek.value.toLong() % 7)
    }

    val pagerState = rememberPagerState(
        initialPage = INITIAL_PAGE,
        pageCount = { INITIAL_PAGE * 2 }
    )

    val visibleStartOfWeek = remember(pagerState.currentPage) {
        val weekOffset = (pagerState.currentPage - INITIAL_PAGE).toLong()
        baseStartOfWeek.plusWeeks(weekOffset)
    }

    val collapsedMonthHeader = remember(visibleStartOfWeek) {
        visibleStartOfWeek.plusDays(3).format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault()))
    }

    val shape = if (isExpanded) RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp) else RoundedCornerShape(0.dp)

    // Tracks total vertical drag distance (negative = upwards, positive = downwards)
    var totalDragAmount by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(if (isExpanded) 16.dp else 0.dp, shape = shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(bottom = 8.dp)
            .pointerInput(isExpanded) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        // Swipe UP to expand (negative drag distance)
                        if (!isExpanded && totalDragAmount < -30f) {
                            onExpandToggle()
                        }
                        // Swipe DOWN to collapse (positive drag distance)
                        else if (isExpanded && totalDragAmount > 30f) {
                            onExpandToggle()
                        }
                        totalDragAmount = 0f
                    },
                    onDragCancel = { totalDragAmount = 0f },
                    onVerticalDrag = { _, dragAmount ->
                        totalDragAmount += dragAmount
                    }
                )
            }
            .animateContentSize()
    ) {
        if (isExpanded) {
            CalendarBarExpandedView(
                displayedMonth = displayedMonth,
                onMonthChange = { displayedMonth = it },
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                onExpandToggle = onExpandToggle
            )
        } else {
            CalendarBarCollapsedView(
                collapsedMonthHeader = collapsedMonthHeader,
                pagerState = pagerState,
                baseStartOfWeek = baseStartOfWeek,
                initialPage = INITIAL_PAGE,
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                onExpandToggle = {
                    displayedMonth = YearMonth.from(selectedDate)
                    onExpandToggle()
                }
            )
        }
    }
}