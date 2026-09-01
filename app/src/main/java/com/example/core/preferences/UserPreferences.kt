package com.example.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.core.localization.AppLanguage
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "focusflow_settings")

data class UserSettings(
    val language: AppLanguage = AppLanguage.ZH_HANS,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accentTheme: AccentTheme = AccentTheme.INDIGO_PURPLE,
    val is24HourFormat: Boolean = true,
    val isStrictModeEnabled: Boolean = false,
    val isHapticsEnabled: Boolean = true,
    val isReduceMotion: Boolean = false,
    val isAppLockEnabled: Boolean = false,
    val appLockPin: String = "",
    val startScreen: String = "today",
    val dailyTargetFocusMinutes: Int = 300, // 5 hours
    val allowAiReadTasks: Boolean = true,
    val allowAiReadSchedule: Boolean = true,
    val allowAiReadScreenTime: Boolean = true,
    val isOnboardingCompleted: Boolean = true
)

class PreferencesManager(private val context: Context) {
    companion object {
        val KEY_LANGUAGE = stringPreferencesKey("app_language")
        val KEY_THEME_MODE = stringPreferencesKey("app_theme_mode")
        val KEY_ACCENT_THEME = stringPreferencesKey("app_accent_theme")
        val KEY_IS_24H = booleanPreferencesKey("is_24h_format")
        val KEY_STRICT_MODE = booleanPreferencesKey("is_strict_mode")
        val KEY_HAPTICS = booleanPreferencesKey("is_haptics_enabled")
        val KEY_REDUCE_MOTION = booleanPreferencesKey("is_reduce_motion")
        val KEY_APP_LOCK = booleanPreferencesKey("is_app_lock_enabled")
        val KEY_APP_LOCK_PIN = stringPreferencesKey("app_lock_pin")
        val KEY_START_SCREEN = stringPreferencesKey("start_screen")
        val KEY_TARGET_MINUTES = intPreferencesKey("target_focus_minutes")
        val KEY_AI_TASKS = booleanPreferencesKey("ai_allow_tasks")
        val KEY_AI_SCHEDULE = booleanPreferencesKey("ai_allow_schedule")
        val KEY_AI_SCREEN_TIME = booleanPreferencesKey("ai_allow_screen_time")
        val KEY_ONBOARDING_DONE = booleanPreferencesKey("onboarding_completed")
        val KEY_HAS_PURGED_DEMO = booleanPreferencesKey("has_purged_demo_v2")
        val KEY_PINNED_NEXT_TASK_ID = stringPreferencesKey("pinned_next_task_id")
        val KEY_CUSTOM_TOTAL_SCREEN_MINUTES = intPreferencesKey("custom_total_screen_minutes")

        // Saved Focus Timer State keys for persistent restoration across app restarts
        val KEY_TIMER_STATUS = stringPreferencesKey("saved_timer_status")
        val KEY_TIMER_MODE = stringPreferencesKey("saved_timer_mode")
        val KEY_TIMER_SELECTED_MINUTES = intPreferencesKey("saved_timer_selected_minutes")
        val KEY_TIMER_REMAINING_SECONDS = intPreferencesKey("saved_timer_remaining_seconds")
        val KEY_TIMER_TOTAL_SECONDS = intPreferencesKey("saved_timer_total_seconds")
        val KEY_TIMER_PAUSES_COUNT = intPreferencesKey("saved_timer_pauses_count")
        val KEY_TIMER_INTERRUPTIONS_COUNT = intPreferencesKey("saved_timer_interruptions_count")
        val KEY_TIMER_LAST_TIMESTAMP = androidx.datastore.preferences.core.longPreferencesKey("saved_timer_last_timestamp")
    }

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            language = AppLanguage.entries.find { it.name == prefs[KEY_LANGUAGE] } ?: AppLanguage.ZH_HANS,
            themeMode = ThemeMode.entries.find { it.name == prefs[KEY_THEME_MODE] } ?: ThemeMode.SYSTEM,
            accentTheme = AccentTheme.entries.find { it.name == prefs[KEY_ACCENT_THEME] } ?: AccentTheme.INDIGO_PURPLE,
            is24HourFormat = prefs[KEY_IS_24H] ?: true,
            isStrictModeEnabled = prefs[KEY_STRICT_MODE] ?: false,
            isHapticsEnabled = prefs[KEY_HAPTICS] ?: true,
            isReduceMotion = prefs[KEY_REDUCE_MOTION] ?: false,
            isAppLockEnabled = prefs[KEY_APP_LOCK] ?: false,
            appLockPin = prefs[KEY_APP_LOCK_PIN] ?: "",
            startScreen = prefs[KEY_START_SCREEN] ?: "today",
            dailyTargetFocusMinutes = prefs[KEY_TARGET_MINUTES] ?: 300,
            allowAiReadTasks = prefs[KEY_AI_TASKS] ?: true,
            allowAiReadSchedule = prefs[KEY_AI_SCHEDULE] ?: true,
            allowAiReadScreenTime = prefs[KEY_AI_SCREEN_TIME] ?: true,
            isOnboardingCompleted = prefs[KEY_ONBOARDING_DONE] ?: true
        )
    }

    val pinnedNextTaskIdFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_PINNED_NEXT_TASK_ID]
    }

    suspend fun setPinnedNextTaskId(taskId: String?) {
        context.dataStore.edit { prefs ->
            if (taskId != null) {
                prefs[KEY_PINNED_NEXT_TASK_ID] = taskId
            } else {
                prefs.remove(KEY_PINNED_NEXT_TASK_ID)
            }
        }
    }

    val customTotalScreenMinutesFlow: Flow<Int?> = context.dataStore.data.map { prefs ->
        prefs[KEY_CUSTOM_TOTAL_SCREEN_MINUTES]
    }

    suspend fun setCustomTotalScreenMinutes(minutes: Int?) {
        context.dataStore.edit { prefs ->
            if (minutes != null) {
                prefs[KEY_CUSTOM_TOTAL_SCREEN_MINUTES] = minutes
            } else {
                prefs.remove(KEY_CUSTOM_TOTAL_SCREEN_MINUTES)
            }
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[KEY_LANGUAGE] = language.name }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode.name }
    }

    suspend fun setAccentTheme(theme: AccentTheme) {
        context.dataStore.edit { it[KEY_ACCENT_THEME] = theme.name }
    }

    suspend fun set24HourFormat(is24h: Boolean) {
        context.dataStore.edit { it[KEY_IS_24H] = is24h }
    }

    suspend fun setStrictMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_STRICT_MODE] = enabled }
    }

    suspend fun setHaptics(enabled: Boolean) {
        context.dataStore.edit { it[KEY_HAPTICS] = enabled }
    }

    suspend fun setReduceMotion(enabled: Boolean) {
        context.dataStore.edit { it[KEY_REDUCE_MOTION] = enabled }
    }

    suspend fun setAppLock(enabled: Boolean, pin: String) {
        context.dataStore.edit {
            it[KEY_APP_LOCK] = enabled
            it[KEY_APP_LOCK_PIN] = pin
        }
    }

    suspend fun setStartScreen(screen: String) {
        context.dataStore.edit { it[KEY_START_SCREEN] = screen }
    }

    suspend fun setDailyTargetMinutes(minutes: Int) {
        context.dataStore.edit { it[KEY_TARGET_MINUTES] = minutes }
    }

    suspend fun setAiPermissions(tasks: Boolean, schedule: Boolean, screenTime: Boolean) {
        context.dataStore.edit {
            it[KEY_AI_TASKS] = tasks
            it[KEY_AI_SCHEDULE] = schedule
            it[KEY_AI_SCREEN_TIME] = screenTime
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_DONE] = completed }
    }

    suspend fun saveFocusTimerState(
        status: String,
        mode: String,
        selectedMinutes: Int,
        remainingSeconds: Int,
        totalSeconds: Int,
        pausesCount: Int,
        interruptionsCount: Int
    ) {
        context.dataStore.edit { prefs ->
            prefs[KEY_TIMER_STATUS] = status
            prefs[KEY_TIMER_MODE] = mode
            prefs[KEY_TIMER_SELECTED_MINUTES] = selectedMinutes
            prefs[KEY_TIMER_REMAINING_SECONDS] = remainingSeconds
            prefs[KEY_TIMER_TOTAL_SECONDS] = totalSeconds
            prefs[KEY_TIMER_PAUSES_COUNT] = pausesCount
            prefs[KEY_TIMER_INTERRUPTIONS_COUNT] = interruptionsCount
            prefs[KEY_TIMER_LAST_TIMESTAMP] = System.currentTimeMillis()
        }
    }

    suspend fun clearSavedFocusTimerState() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_TIMER_STATUS)
            prefs.remove(KEY_TIMER_MODE)
            prefs.remove(KEY_TIMER_SELECTED_MINUTES)
            prefs.remove(KEY_TIMER_REMAINING_SECONDS)
            prefs.remove(KEY_TIMER_TOTAL_SECONDS)
            prefs.remove(KEY_TIMER_PAUSES_COUNT)
            prefs.remove(KEY_TIMER_INTERRUPTIONS_COUNT)
            prefs.remove(KEY_TIMER_LAST_TIMESTAMP)
        }
    }
}
