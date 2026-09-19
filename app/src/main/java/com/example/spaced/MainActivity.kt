package com.example.spaced

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.spaced.ui.components.AppNavigationBar
import com.example.spaced.ui.components.BottomNavItem
import com.example.spaced.ui.components.CalendarBar
import com.example.spaced.ui.components.TrackFab
import com.example.spaced.ui.screens.HomeScreen
import com.example.spaced.ui.screens.TrackScreen
import com.example.spaced.ui.theme.AppThemeMode
import com.example.spaced.ui.theme.SpacedTheme
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import java.time.LocalDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        setContent {
            SpacedTheme(themeMode = AppThemeMode.DARK) {
                MainScreen()
            }
        }
    }
}



@Composable
fun MainScreen() {
    var currentRoute by remember { mutableStateOf(BottomNavItem.Home.route) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isCalendarExpanded by remember { mutableStateOf(false) }
    var isTrackScreenOpen by remember { mutableStateOf(false) }

    // 1. Intercept back gesture when the Track screen overlay is open
    BackHandler(enabled = isTrackScreenOpen) {
        isTrackScreenOpen = false
    }

    // 2. Intercept back gesture when the Calendar menu is expanded
    BackHandler(enabled = isCalendarExpanded) {
        isCalendarExpanded = false
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // LAYER 1: Fixed Home Screen Surface
        Scaffold { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = 160.dp)
            ) {
                HomeScreen(
                    selectedDate = selectedDate,
                    onProfileClick = { /* Handle profile action */ }
                )
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
                    .background(Color.Black.copy(alpha = 0.25f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isCalendarExpanded = false
                    }
            )
        }

        // LAYER 3: Anchored Bottom Navigation, Calendar & FAB
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
                    isExpanded = !isCalendarExpanded
                )
            }

            CalendarBar(
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                isExpanded = isCalendarExpanded,
                onExpandToggle = { isCalendarExpanded = !isCalendarExpanded }
            )

            AppNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = { selectedItem ->
                    currentRoute = selectedItem.route
                }
            )
        }

        // LAYER 4: Track Screen / Overflow Overlay
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