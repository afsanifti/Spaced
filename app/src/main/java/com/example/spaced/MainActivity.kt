package com.example.spaced

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spaced.data.model.Task
import com.example.spaced.ui.components.calender.CalendarBar
import com.example.spaced.ui.components.navigation.AppNavigationBar
import com.example.spaced.ui.components.navigation.BottomNavItem
import com.example.spaced.ui.components.navigation.TrackFab
import com.example.spaced.ui.screens.HomeScreen
import com.example.spaced.ui.screens.SettingsScreen
import com.example.spaced.ui.screens.TrackScreen
import com.example.spaced.ui.theme.AppThemeMode
import com.example.spaced.ui.theme.SpacedTheme
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val systemInDark = isSystemInDarkTheme()
            var currentThemeMode by rememberSaveable { mutableStateOf(AppThemeMode.DYNAMIC) }

            val isDark = when (currentThemeMode) {
                AppThemeMode.DARK -> true
                AppThemeMode.LIGHT -> false
                AppThemeMode.DYNAMIC -> systemInDark
            }

            enableEdgeToEdge(
                statusBarStyle = if (isDark) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                else SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT),
                navigationBarStyle = if (isDark) SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                else SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            SpacedTheme(themeMode = currentThemeMode) {
                MainScreen(
                    currentThemeMode = currentThemeMode,
                    onThemeModeChanged = { updatedMode ->
                        currentThemeMode = updatedMode
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    currentThemeMode: AppThemeMode,
    onThemeModeChanged: (AppThemeMode) -> Unit
) {
    var currentRoute by remember { mutableStateOf(BottomNavItem.Home.route) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isCalendarExpanded by remember { mutableStateOf(false) }
    var isTrackScreenOpen by remember { mutableStateOf(false) }
    var isHomeScreenAtTop by remember { mutableStateOf(true) }

    val sampleTasks = remember {
        listOf(
            Task(
                id = "1",
                tag = "Ph 1109",
                chapter = "Chapter 2",
                title = "ACOM Chap 2",
                currentCycle = 3,
                totalCycles = 7
            ),
            Task(
                id = "2",
                tag = "ECE 2207",
                chapter = null,
                title = "8085 Microprocessor",
                currentCycle = 13,
                totalCycles = 20
            ),
            Task(
                id = "3",
                tag = "ECE 2207",
                chapter = null,
                title = "8085 Microprocessor",
                currentCycle = 5,
                totalCycles = 20
            ),
            Task(
                id = "4",
                tag = "ECE 2207",
                chapter = null,
                title = "8085 Microprocessor",
                currentCycle = 5,
                totalCycles = 20
            )
        )
    }

    BackHandler(enabled = isTrackScreenOpen) { isTrackScreenOpen = false }
    BackHandler(enabled = isCalendarExpanded) { isCalendarExpanded = false }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // LAYER 1: Fullscreen Screen Layer
            Scaffold(
                containerColor = Color.Transparent
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding) // Removed .padding(bottom = 160.dp)
                ) {
                    when (currentRoute) {
                        BottomNavItem.Home.route -> {
                            HomeScreen(
                                selectedDate = selectedDate,
                                tasks = sampleTasks,
                                onScrollStateChanged = { isHomeScreenAtTop = it },
                                onProfileClick = { /* Handle profile action */ }
                            )
                        }
                        BottomNavItem.Settings.route -> {
                            SettingsScreen(
                                currentThemeMode = currentThemeMode,
                                onThemeModeChanged = onThemeModeChanged
                            )
                        }
                        else -> {
                            HomeScreen(
                                selectedDate = LocalDate.now(),
                                tasks = sampleTasks
                            )
                        }
                    }
                }
            }

            // LAYER 2: Dimmed Backdrop
            AnimatedVisibility(
                visible = isCalendarExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            isCalendarExpanded = false
                        }
                )
            }

            // LAYER 3: Floating Anchored Bottom Controls
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    TrackFab(
                        onClick = {
                            if (isCalendarExpanded) {
                                isCalendarExpanded = false
                            } else {
                                isTrackScreenOpen = true
                            }
                        },
                        isExpanded = isHomeScreenAtTop && !isCalendarExpanded
                    )
                }

                AnimatedVisibility(
                    visible = isHomeScreenAtTop || isCalendarExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    CalendarBar(
                        selectedDate = selectedDate,
                        onDateSelected = { selectedDate = it },
                        isExpanded = isCalendarExpanded,
                        onExpandToggle = { isCalendarExpanded = !isCalendarExpanded }
                    )
                }

                AppNavigationBar(
                    currentRoute = currentRoute,
                    onItemSelected = { selectedItem ->
                        currentRoute = selectedItem.route
                    }
                )
            }

            // LAYER 4: Track Screen Overlay
            AnimatedVisibility(
                visible = isTrackScreenOpen,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                TrackScreen(
                    onCloseClick = { isTrackScreenOpen = false },
                    onStartTrackingClick = { isTrackScreenOpen = false }
                )
            }
        }
    }
}