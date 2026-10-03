package com.example.spaced

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.spaced.data.model.Task
import com.example.spaced.ui.components.calender.CalendarBar
import com.example.spaced.ui.components.navigation.AppNavigationBar
import com.example.spaced.ui.components.navigation.TrackFab
import com.example.spaced.ui.screens.HomeScreen
import com.example.spaced.ui.screens.PomodoroScreen
import com.example.spaced.ui.screens.SettingsScreen
import com.example.spaced.ui.screens.TaskSessionScreen
import com.example.spaced.ui.screens.TrackScreen
import com.example.spaced.ui.theme.DarkThemeConfig
import com.example.spaced.ui.theme.SpacedTheme
import com.example.spaced.ui.viewmodels.SettingsViewModel
import com.materialkolor.PaletteStyle
import java.time.LocalDate

object Routes {
    const val HOME = "home"
    const val SETTINGS = "settings"
    const val POMODORO = "pomodoro"
    const val TRACK = "track"
    const val SESSION_PATTERN = "session/{taskId}"

    fun session(taskId: String) = "session/$taskId"
}

class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val systemInDark = isSystemInDarkTheme()

            // Observe persistent state from DataStore via SettingsViewModel
            val darkThemeConfig by settingsViewModel.darkThemeConfig.collectAsStateWithLifecycle()
            val useDynamicColor by settingsViewModel.useDynamicColor.collectAsStateWithLifecycle()
            val paletteStyle by settingsViewModel.paletteStyle.collectAsStateWithLifecycle()

            val isDark = when (darkThemeConfig) {
                DarkThemeConfig.DARK -> true
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.SYSTEM -> systemInDark
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

            SpacedTheme(
                darkThemeConfig = darkThemeConfig,
                useDynamicColor = useDynamicColor,
                paletteStyle = paletteStyle
            ) {
                AppNavigation(
                    darkThemeConfig = darkThemeConfig,
                    onDarkThemeConfigChanged = settingsViewModel::setDarkThemeConfig,
                    useDynamicColor = useDynamicColor,
                    onDynamicColorChanged = settingsViewModel::setUseDynamicColor,
                    paletteStyle = paletteStyle,
                    onPaletteStyleChanged = settingsViewModel::setPaletteStyle
                )
            }
        }
    }
}

@Composable
fun AppNavigation(
    darkThemeConfig: DarkThemeConfig,
    onDarkThemeConfigChanged: (DarkThemeConfig) -> Unit,
    useDynamicColor: Boolean,
    onDynamicColorChanged: (Boolean) -> Unit,
    paletteStyle: PaletteStyle,
    onPaletteStyleChanged: (PaletteStyle) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: Routes.HOME

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var isCalendarExpanded by remember { mutableStateOf(false) }
    var isHomeScreenAtTop by remember { mutableStateOf(true) }
    var isBottomBarVisible by remember { mutableStateOf(true) }

    // Reset navigation bar visibility when changing screens
    LaunchedEffect(currentRoute) {
        isBottomBarVisible = true
    }

    val sampleTasks = remember {
        listOf(
            Task(id = "1", tag = "Ph 1109", chapter = "Chapter 2", title = "ACOM Chap 2", currentCycle = 3, totalCycles = 7),
            Task(id = "2", tag = "ECE 2207", chapter = null, title = "8085 Microprocessor", currentCycle = 13, totalCycles = 20),
            Task(id = "3", tag = "ECE 2207", chapter = null, title = "8085 Microprocessor", currentCycle = 5, totalCycles = 20),
            Task(id = "4", tag = "ECE 2207", chapter = null, title = "8085 Microprocessor", currentCycle = 5, totalCycles = 20)
        )
    }

    val isOverlayDestination = currentRoute == Routes.TRACK || currentRoute == Routes.SESSION_PATTERN

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                modifier = Modifier.fillMaxSize(),
                // Material 3 Expressive Standard: Fade Through for main bottom bar navigation
                enterTransition = {
                    fadeIn(
                        animationSpec = tween(durationMillis = 220, delayMillis = 60, easing = LinearOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                    )
                },
                exitTransition = {
                    fadeOut(
                        animationSpec = tween(durationMillis = 90, easing = FastOutLinearInEasing)
                    ) + scaleOut(
                        targetScale = 0.96f,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                    )
                },
                popEnterTransition = {
                    fadeIn(
                        animationSpec = tween(durationMillis = 220, delayMillis = 60, easing = LinearOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing)
                    )
                },
                popExitTransition = {
                    fadeOut(
                        animationSpec = tween(durationMillis = 90, easing = FastOutLinearInEasing)
                    ) + scaleOut(
                        targetScale = 0.96f,
                        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
                    )
                }
            ) {
                composable(route = Routes.HOME) {
                    HomeScreen(
                        selectedDate = selectedDate,
                        tasks = sampleTasks,
                        onScrollStateChanged = { isHomeScreenAtTop = it },
                        onProfileClick = { /* Handle profile action */ },
                        onStartReviewClick = { task ->
                            navController.navigate(Routes.session(task.id))
                        }
                    )
                }

                composable(route = Routes.SETTINGS) {
                    SettingsScreen(
                        darkThemeConfig = darkThemeConfig,
                        onDarkThemeConfigChanged = onDarkThemeConfigChanged,
                        useDynamicColor = useDynamicColor,
                        onDynamicColorChanged = onDynamicColorChanged,
                        paletteStyle = paletteStyle,
                        onPaletteStyleChanged = onPaletteStyleChanged,
                        onScrollStateChanged = { isVisible -> isBottomBarVisible = isVisible }
                    )
                }

                // Track Screen: Material 3 Shared Axis Y (Slide up from bottom)
                composable(
                    route = Routes.TRACK,
                    enterTransition = {
                        slideInVertically(
                            initialOffsetY = { fullHeight -> (fullHeight * 0.15f).toInt() },
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(250))
                    },
                    exitTransition = {
                        slideOutVertically(
                            targetOffsetY = { fullHeight -> (fullHeight * 0.15f).toInt() },
                            animationSpec = tween(250, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(200))
                    },
                    popExitTransition = {
                        slideOutVertically(
                            targetOffsetY = { fullHeight -> (fullHeight * 0.15f).toInt() },
                            animationSpec = tween(250, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(200))
                    }
                ) {
                    TrackScreen(
                        onCloseClick = { navController.popBackStack() },
                        onStartTrackingClick = { navController.popBackStack() }
                    )
                }

                composable(route = Routes.POMODORO) {
                    PomodoroScreen(
                        onScrollStateChanged = { isVisible -> isBottomBarVisible = isVisible }
                    )
                }

                // Task Session: Material 3 Shared Axis X (Forward Push)
                composable(
                    route = Routes.SESSION_PATTERN,
                    arguments = listOf(navArgument("taskId") { type = NavType.StringType }),
                    enterTransition = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth / 4 },
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(250))
                    },
                    popExitTransition = {
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> fullWidth / 4 },
                            animationSpec = tween(250, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(200))
                    }
                ) { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId")
                    val task = sampleTasks.find { it.id == taskId }

                    if (task != null) {
                        TaskSessionScreen(
                            task = task,
                            onCloseClick = { navController.popBackStack() }
                        )
                    }
                }
            }

            if (!isOverlayDestination) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    // Floating Action Button
                    AnimatedVisibility(
                        visible = isBottomBarVisible && currentRoute == Routes.HOME,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(250, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(200)),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(200, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(150))
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
                                        navController.navigate(Routes.TRACK)
                                    }
                                },
                                isExpanded = currentRoute == Routes.HOME && isHomeScreenAtTop && !isCalendarExpanded
                            )
                        }
                    }

                    // CalendarBar only displays on Home Screen
                    if (currentRoute == Routes.HOME && (isHomeScreenAtTop || isCalendarExpanded)) {
                        CalendarBar(
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it },
                            isExpanded = isCalendarExpanded,
                            onExpandToggle = { isCalendarExpanded = !isCalendarExpanded }
                        )
                    }

                    // Bottom Navigation Bar with shrink/slide animation on scroll
                    AnimatedVisibility(
                        visible = isBottomBarVisible,
                        enter = slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = tween(250, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(200)),
                        exit = slideOutVertically(
                            targetOffsetY = { it },
                            animationSpec = tween(200, easing = FastOutLinearInEasing)
                        ) + fadeOut(animationSpec = tween(150))
                    ) {
                        AppNavigationBar(
                            currentRoute = currentRoute,
                            onItemSelected = { selectedItem ->
                                navController.navigate(selectedItem.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}