package com.example.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.database.TaskEntity
import com.example.core.localization.LocaleManager
import com.example.core.localization.LocalStrings
import com.example.ui.MainViewModel
import com.example.ui.aicoach.AICoachScreen
import com.example.ui.analytics.AnalyticsScreen
import com.example.ui.focus.FocusScreen
import com.example.ui.habits.HabitsScreen
import com.example.ui.onboarding.OnboardingScreen
import com.example.ui.schedule.ScheduleScreen
import com.example.ui.settings.SettingsScreen
import com.example.ui.tasks.AddTaskBottomSheet
import com.example.ui.tasks.TasksScreen
import com.example.ui.theme.FocusFlowTheme
import com.example.ui.today.TodayScreen

enum class NavDestination(val route: String, val icon: ImageVector) {
    TODAY("today", Icons.Default.Dashboard),
    FOCUS("focus", Icons.Default.Timer),
    TASKS("tasks", Icons.Default.FormatListBulleted),
    ANALYTICS("analytics", Icons.Default.Insights),
    AI_COACH("ai_coach", Icons.Default.AutoAwesome),
    SCHEDULE("schedule", Icons.Default.CalendarMonth),
    HABITS("habits", Icons.Default.SelfImprovement),
    SETTINGS("settings", Icons.Default.Settings)
}

@Composable
fun FocusFlowApp(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()
    val strings = LocaleManager.getStrings(userSettings.language)

    var currentDestination by remember { mutableStateOf(NavDestination.TODAY) }
    var isOnboardingActive by remember { mutableStateOf(!userSettings.isOnboardingCompleted) }
    var showGlobalAddTaskModal by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalStrings provides strings) {
        FocusFlowTheme(
            themeMode = userSettings.themeMode,
            accentTheme = userSettings.accentTheme
        ) {
            if (isOnboardingActive) {
                OnboardingScreen(
                    viewModel = viewModel,
                    onFinish = {
                        isOnboardingActive = false
                    }
                )
            } else {
                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 8.dp,
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                                .testTag("bottom_navigation_bar")
                        ) {
                            val navItems = listOf(
                                NavDestination.TODAY to strings.navToday,
                                NavDestination.FOCUS to strings.navFocus,
                                NavDestination.TASKS to strings.navTasks,
                                NavDestination.ANALYTICS to strings.navAnalytics,
                                NavDestination.SETTINGS to strings.navSettings
                            )

                            navItems.forEach { (dest, label) ->
                                val isSelected = currentDestination == dest
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { currentDestination = dest },
                                    icon = {
                                        Icon(
                                            imageVector = dest.icon,
                                            contentDescription = label,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                            maxLines = 1
                                        )
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                        indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    ),
                                    modifier = Modifier.testTag("nav_item_${dest.route}")
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        if (currentDestination == NavDestination.TODAY || currentDestination == NavDestination.TASKS) {
                            FloatingActionButton(
                                onClick = {
                                    showGlobalAddTaskModal = true
                                },
                                containerColor = Color(0xFF6366F1),
                                contentColor = Color.White,
                                shape = RoundedCornerShape(16.dp),
                                elevation = FloatingActionButtonDefaults.elevation(
                                    defaultElevation = 8.dp,
                                    pressedElevation = 12.dp
                                ),
                                modifier = Modifier
                                    .padding(bottom = 8.dp, end = 4.dp)
                                    .testTag("quick_add_fab")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Quick Add Task",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    },
                    modifier = modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = currentDestination,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "nav_content_transition"
                        ) { destination ->
                            when (destination) {
                                NavDestination.TODAY -> TodayScreen(
                                    viewModel = viewModel,
                                    onNavigateToFocus = { currentDestination = NavDestination.FOCUS },
                                    onNavigateToAiCoach = { currentDestination = NavDestination.AI_COACH },
                                    onNavigateToSchedule = { currentDestination = NavDestination.SCHEDULE }
                                )
                                NavDestination.FOCUS -> FocusScreen(viewModel = viewModel)
                                NavDestination.TASKS -> TasksScreen(
                                    viewModel = viewModel,
                                    onStartFocusWithTask = { task ->
                                        viewModel.linkTaskToFocus(task)
                                        currentDestination = NavDestination.FOCUS
                                    }
                                )
                                NavDestination.SCHEDULE -> ScheduleScreen(viewModel = viewModel)
                                NavDestination.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                                NavDestination.HABITS -> HabitsScreen(viewModel = viewModel)
                                NavDestination.AI_COACH -> AICoachScreen(viewModel = viewModel)
                                NavDestination.SETTINGS -> SettingsScreen(viewModel = viewModel)
                            }
                        }
                    }
                }

                // Global Quick Add Task Bottom Sheet
                if (showGlobalAddTaskModal) {
                    AddTaskBottomSheet(
                        taskToEdit = null,
                        onDismiss = { showGlobalAddTaskModal = false },
                        onSave = { id, title, desc, priority, category, estMinutes, dueDate, tags ->
                            viewModel.saveTask(id, title, desc, priority, category, estMinutes, dueDate, tags)
                            showGlobalAddTaskModal = false
                        }
                    )
                }
            }
        }
    }
}
