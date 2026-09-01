package com.example.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    SYSTEM("system", "Follow System", "跟随系统"),
    ZH_HANS("zh_CN", "Simplified Chinese", "简体中文"),
    ZH_HANT("zh_TW", "Traditional Chinese", "繁體中文"),
    EN("en", "English", "English"),
    JA("ja", "Japanese", "日本語"),
    KO("ko", "Korean", "한국어"),
    FR("fr", "French", "Français"),
    DE("de", "German", "Deutsch"),
    ES("es", "Spanish", "Español")
}

data class AppStrings(
    val appName: String = "FocusFlow",
    val appSlogan: String = "Personal Time & Focus Operating System",
    
    // Bottom navigation
    val navToday: String = "Today",
    val navFocus: String = "Focus",
    val navTasks: String = "Tasks",
    val navSchedule: String = "Schedule",
    val navAnalytics: String = "Analytics",
    val navHabits: String = "Habits",
    val navAiCoach: String = "AI Coach",
    val navSettings: String = "Settings",
    
    // Today
    val greetingMorning: String = "Good morning",
    val greetingAfternoon: String = "Good afternoon",
    val greetingEvening: String = "Good evening",
    val todayQuote: String = "Focus on what truly matters today.",
    val todayFocusTime: String = "Today's Focus",
    val targetTime: String = "Target",
    val screenTime: String = "Screen Time",
    val taskCompletion: String = "Tasks",
    val nextImportantTask: String = "Next Important Task",
    val startFocus: String = "Start Focus",
    val scheduleMyDay: String = "Plan My Day",
    val aiDailyInsightHighTasks: String = "You have high-priority tasks today. Focus on deep work first.",
    val aiDailyInsightNormal: String = "Great pace today! Maintain momentum and take scheduled breaks.",
    val aiDailyInsightDistraction: String = "Entertainment app usage increased today. Activate focus mode to block interruptions.",
    val quickAdd: String = "Quick Add",
    val addTask: String = "Add Task",
    val addSchedule: String = "Add Event",
    val addHabit: String = "Add Habit",
    val addGoal: String = "Add Goal",
    
    // Focus
    val focusTimer: String = "Focus Timer",
    val pomodoro: String = "Pomodoro",
    val deepWork: String = "Deep Work",
    val quickFocus: String = "Quick Focus",
    val customMode: String = "Custom",
    val min25: String = "25 Min",
    val min50: String = "50 Min",
    val min60: String = "60 Min",
    val min90: String = "90 Min",
    val customDuration: String = "Custom Duration",
    val selectTask: String = "Link Task",
    val selectProject: String = "Link Project",
    val ambientNoise: String = "Ambient Sound",
    val noiseRain: String = "Rain",
    val noiseForest: String = "Forest",
    val noiseCafe: String = "Cafe",
    val noiseWaves: String = "Ocean Waves",
    val noiseFireplace: String = "Fireplace",
    val pause: String = "Pause",
    val resume: String = "Resume",
    val giveUp: String = "Exit",
    val completeFocus: String = "Complete",
    val strictModeEnabled: String = "Strict Mode Active",
    val strictModeExitHint: String = "Hold for 5 seconds to exit strict focus",
    val focusSuccessTitle: String = "Session Completed!",
    val focusSuccessDesc: String = "You stayed focused and completed your session.",
    val actualFocusDuration: String = "Focused Duration",
    val interruptionsCount: String = "Interruptions",
    val pausesCount: String = "Pauses",
    val confirmExitTitle: String = "Exit Focus Session?",
    val confirmExitDesc: String = "Leaving now will record this session as interrupted. Are you sure?",
    val cancel: String = "Cancel",
    val confirm: String = "Confirm",
    
    // Tasks & Projects
    val allTasks: String = "All Tasks",
    val pending: String = "Pending",
    val inProgress: String = "In Progress",
    val completed: String = "Completed",
    val overdue: String = "Overdue",
    val priorityLow: String = "Low",
    val priorityNormal: String = "Medium",
    val priorityHigh: String = "High",
    val priorityUrgent: String = "Urgent",
    val estimatedMinutes: String = "Est. Minutes",
    val dueToday: String = "Due Today",
    val dueDate: String = "Due Date",
    val subtasks: String = "Subtasks",
    val addSubtask: String = "Add subtask",
    val noTasksTitle: String = "No tasks yet",
    val noTasksDesc: String = "Create your first task to start organizing your day.",
    val searchPlaceholder: String = "Search tasks, projects, schedules...",
    val projects: String = "Projects",
    val createProject: String = "New Project",
    val taskTitlePlaceholder: String = "What do you want to accomplish?",
    val taskDescPlaceholder: String = "Add notes, context, or subtasks...",
    val editTask: String = "Edit Task",
    val deleteTask: String = "Delete Task",
    val save: String = "Save",
    
    // Schedule
    val calendarDay: String = "Day",
    val calendarWeek: String = "Week",
    val calendarMonth: String = "Month",
    val timeline: String = "Timeline",
    val eventTitle: String = "Event Title",
    val eventStartTime: String = "Start Time",
    val eventEndTime: String = "End Time",
    val noEventsToday: String = "No events scheduled for this day",
    
    // App Usage & Screen Time
    val screenTimeStats: String = "Screen Time & App Limits",
    val appUsageToday: String = "Today's App Usage",
    val appOpenCount: String = "Opens",
    val dailyLimit: String = "Daily Limit",
    val setLimit: String = "Set Limit",
    val editLimit: String = "Edit Limit",
    val autoBlockInFocus: String = "Auto-block in Focus Mode",
    val appShielding: String = "Distraction Shield",
    val limitReached: String = "Limit Reached",
    val weekdayLimit: String = "Weekdays",
    val weekendLimit: String = "Weekends",
    val allowedApps: String = "Allowed Apps Whitelist",
    
    // Analytics
    val analyticsTitle: String = "Data & Analytics",
    val periodToday: String = "Today",
    val periodWeek: String = "This Week",
    val periodMonth: String = "This Month",
    val periodYear: String = "This Year",
    val focusTrend: String = "Focus Trend",
    val screenVsFocus: String = "Screen vs Focus Time",
    val focusCurve: String = "Focus Trend Curve",
    val weeklyReport: String = "Weekly Report",
    val weeklyReportSummary: String = "Focused %s this week (+14%% vs last week), Screen time decreased by 2h 10m.",
    val mostDistractedTime: String = "Peak Distraction",
    val mostProductiveTime: String = "Peak Productivity",
    val shareReport: String = "Share Report",
    
    // Habits & Goals
    val habitsTitle: String = "Habits & Streaks",
    val currentStreak: String = "Streak",
    val daysUnit: String = "days",
    val goalsTitle: String = "Long-Term Goals",
    val targetHours: String = "Target Hours",
    val currentProgress: String = "Progress",
    
    // AI Coach
    val aiCoachTitle: String = "AI Focus Coach",
    val aiCoachSubtitle: String = "Personalized schedule suggestions & focus coaching",
    val aiInputPlaceholder: String = "Ask for advice, task breakdown, or day planning...",
    val aiPlanPromptExample1: String = "Help me break down: Write Thesis into 3 focus sessions",
    val aiPlanPromptExample2: String = "Plan my afternoon: 2h coding, 1h reading, 30m gym",
    val aiPlanPromptExample3: String = "I feel distracted today, what should I do?",
    val aiPermissionNotice: String = "AI uses your local tasks & focus data securely to provide advice.",
    val send: String = "Send",
    
    // Settings
    val settingsTitle: String = "Settings Center",
    val sectionAccount: String = "Account & Sync",
    val sectionGeneral: String = "General",
    val sectionAppearance: String = "Appearance",
    val sectionFocus: String = "Focus & Timer",
    val sectionAppLimits: String = "App Limits & Shield",
    val sectionNotifications: String = "Notifications",
    val sectionDataPrivacy: String = "Data & Privacy",
    val sectionSecurity: String = "App Lock & Security",
    val sectionAbout: String = "About & System",
    
    val profileName: String = "Productivity Master",
    val profileEmail: String = "user@focusflow.app",
    val cloudSync: String = "Cloud Backup & Sync",
    val cloudSyncDesc: String = "Offline first. Changes sync automatically.",
    val exportData: String = "Export All Data (JSON/CSV)",
    val importData: String = "Import / Restore Data",
    val clearLocalData: String = "Clear Local Cache",
    val deleteAccount: String = "Delete Account",
    
    val language: String = "Language",
    val timeFormat: String = "Time Format",
    val timeFormat24: String = "24-hour",
    val timeFormat12: String = "12-hour",
    val startScreen: String = "Default Launch Screen",
    
    val themeMode: String = "Theme Mode",
    val themeSystem: String = "Follow System",
    val themeLight: String = "Light",
    val themeDark: String = "Dark",
    val themeColor: String = "Accent Color",
    val dynamicColor: String = "Dynamic Color (Material You)",
    
    val hapticFeedback: String = "Haptic Feedback",
    val reduceMotion: String = "Reduce Animations",
    val strictMode: String = "Strict Focus Mode",
    val appLockPin: String = "Biometric / PIN App Lock",
    val permissionsCenter: String = "System Permissions Center",
    val platformLimitations: String = "Platform Capabilities & Docs",
    val version: String = "Version",
    
    // Onboarding
    val onboardingWelcome: String = "Welcome to FocusFlow",
    val onboardingTagline: String = "Your personal operating system for deep work and digital wellbeing.",
    val onboardingGoalPrompt: String = "What is your main productivity goal?",
    val onboardingGoal1: String = "Deep Work & Study",
    val onboardingGoal2: String = "Reduce Phone Addiction",
    val onboardingGoal3: String = "Build Good Daily Habits",
    val onboardingGoal4: String = "Organize Daily Schedule",
    val onboardingTargetPrompt: String = "Set your daily focus goal",
    val onboardingHours4: String = "4 Hours / day",
    val onboardingDistractionPrompt: String = "Select apps that distract you most",
    val getStarted: String = "Get Started",
    val skip: String = "Skip",
    val next: String = "Next"
)

val LocalStrings = compositionLocalOf { AppStrings() }

object LocaleManager {
    fun getStrings(language: AppLanguage): AppStrings {
        return when (language) {
            AppLanguage.ZH_HANS -> ZhHansStrings
            AppLanguage.ZH_HANT -> ZhHantStrings
            AppLanguage.EN -> EnStrings
            AppLanguage.JA -> JaStrings
            AppLanguage.KO -> KoStrings
            AppLanguage.FR -> FrStrings
            AppLanguage.DE -> DeStrings
            AppLanguage.ES -> EsStrings
            AppLanguage.SYSTEM -> ZhHansStrings // Default system fallback to Chinese as requested
        }
    }
}
