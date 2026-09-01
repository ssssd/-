package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.ai.AICoachMessage
import com.example.core.ai.AICoachService
import com.example.core.ai.MessageSender
import com.example.core.audio.WhiteNoiseSynthesizer
import com.example.core.audio.WhiteNoiseType
import com.example.core.database.AppDatabase
import com.example.core.database.AppUsageEntity
import com.example.core.database.FocusFlowRepository
import com.example.core.database.FocusSessionEntity
import com.example.core.database.GoalEntity
import com.example.core.database.HabitEntity
import com.example.core.database.ProjectEntity
import com.example.core.database.ScheduleEntity
import com.example.core.database.TaskEntity
import com.example.core.localization.AppLanguage
import com.example.core.preferences.PreferencesManager
import com.example.core.preferences.UserSettings
import com.example.core.preferences.dataStore
import androidx.datastore.preferences.core.edit
import com.example.core.screentime.AndroidScreenTimeService
import com.example.core.screentime.ScreenTimeService
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class TimerStatus {
    IDLE,
    RUNNING,
    PAUSED,
    COMPLETED
}

enum class FocusMode(val defaultMinutes: Int, val titleKey: String) {
    POMODORO(25, "pomodoro"),
    DEEP_WORK(50, "deepWork"),
    QUICK_FOCUS(15, "quickFocus"),
    CUSTOM(60, "customMode")
}

data class FocusTimerState(
    val status: TimerStatus = TimerStatus.IDLE,
    val mode: FocusMode = FocusMode.POMODORO,
    val selectedMinutes: Int = 25,
    val remainingSeconds: Int = 25 * 60,
    val totalSeconds: Int = 25 * 60,
    val linkedTask: TaskEntity? = null,
    val linkedProject: ProjectEntity? = null,
    val pausesCount: Int = 0,
    val interruptionsCount: Int = 0,
    val unlockProgress: Float = 0f, // For 5s strict long press
    val selectedNoise: WhiteNoiseType? = null,
    val noiseVolume: Float = 0.5f,
    val isNoisePlaying: Boolean = false,
    val isSummaryDialogShown: Boolean = false,
    val completedSessionSummary: FocusSessionEntity? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    val repository = FocusFlowRepository(database)
    val preferencesManager = PreferencesManager(application)
    val screenTimeService: ScreenTimeService = AndroidScreenTimeService(application)
    private val aiCoachService = AICoachService()
    val whiteNoiseSynthesizer = WhiteNoiseSynthesizer()

    val userSettings: StateFlow<UserSettings> = preferencesManager.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val tasks: StateFlow<List<TaskEntity>> = repository.allTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val topPendingTasks: StateFlow<List<TaskEntity>> = repository.topPendingTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val projects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessions: StateFlow<List<FocusSessionEntity>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val habits: StateFlow<List<HabitEntity>> = repository.allHabits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val goals: StateFlow<List<GoalEntity>> = repository.allGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val schedules: StateFlow<List<ScheduleEntity>> = repository.allSchedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appUsages: StateFlow<List<AppUsageEntity>> = repository.allAppUsages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _focusTimerState = MutableStateFlow(FocusTimerState())
    val focusTimerState: StateFlow<FocusTimerState> = _focusTimerState.asStateFlow()

    private val _aiMessages = MutableStateFlow<List<AICoachMessage>>(
        listOf(
            AICoachMessage(
                sender = MessageSender.AI,
                text = "你好！我是你的 AI 专注教练。今天我能为你做些什么？\n\n我可以帮你拆解大任务、生成全天科学作息表、或提供摆脱拖延的专注策略。"
            )
        )
    )
    val aiMessages: StateFlow<List<AICoachMessage>> = _aiMessages.asStateFlow()
    val isAiThinking = MutableStateFlow(false)

    // Search query state
    val searchQuery = MutableStateFlow("")

    private var timerJob: Job? = null

    init {
        cleanDemoDataOnce()
        restoreSavedFocusTimerState()
    }

    private fun cleanDemoDataOnce() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().dataStore.data.first()
            val hasPurged = prefs[PreferencesManager.KEY_HAS_PURGED_DEMO] ?: false
            if (!hasPurged) {
                repository.clearAllData()
                getApplication<Application>().dataStore.edit {
                    it[PreferencesManager.KEY_HAS_PURGED_DEMO] = true
                }
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    private fun restoreSavedFocusTimerState() {
        viewModelScope.launch {
            val prefs = getApplication<Application>().dataStore.data.first()
            val statusStr = prefs[PreferencesManager.KEY_TIMER_STATUS]
            if (statusStr != null && (statusStr == TimerStatus.RUNNING.name || statusStr == TimerStatus.PAUSED.name)) {
                val modeStr = prefs[PreferencesManager.KEY_TIMER_MODE] ?: FocusMode.POMODORO.name
                val mode = try { FocusMode.valueOf(modeStr) } catch (e: Exception) { FocusMode.POMODORO }
                val selectedMinutes = prefs[PreferencesManager.KEY_TIMER_SELECTED_MINUTES] ?: mode.defaultMinutes
                val totalSecs = prefs[PreferencesManager.KEY_TIMER_TOTAL_SECONDS] ?: (selectedMinutes * 60)
                var remainingSecs = prefs[PreferencesManager.KEY_TIMER_REMAINING_SECONDS] ?: totalSecs
                val pausesCount = prefs[PreferencesManager.KEY_TIMER_PAUSES_COUNT] ?: 0
                val interruptionsCount = prefs[PreferencesManager.KEY_TIMER_INTERRUPTIONS_COUNT] ?: 0
                val lastTimestamp = prefs[PreferencesManager.KEY_TIMER_LAST_TIMESTAMP] ?: System.currentTimeMillis()

                if (statusStr == TimerStatus.RUNNING.name) {
                    val elapsedSeconds = ((System.currentTimeMillis() - lastTimestamp) / 1000L).toInt().coerceAtLeast(0)
                    remainingSecs = (remainingSecs - elapsedSeconds).coerceAtLeast(0)
                }

                if (remainingSecs > 0) {
                    _focusTimerState.value = FocusTimerState(
                        status = TimerStatus.PAUSED, // restore as paused so user is in control
                        mode = mode,
                        selectedMinutes = selectedMinutes,
                        remainingSeconds = remainingSecs,
                        totalSeconds = totalSecs,
                        pausesCount = pausesCount,
                        interruptionsCount = interruptionsCount
                    )
                } else {
                    preferencesManager.clearSavedFocusTimerState()
                }
            }
        }
    }

    private fun persistFocusTimerState() {
        viewModelScope.launch {
            val s = _focusTimerState.value
            if (s.status == TimerStatus.RUNNING || s.status == TimerStatus.PAUSED) {
                preferencesManager.saveFocusTimerState(
                    status = s.status.name,
                    mode = s.mode.name,
                    selectedMinutes = s.selectedMinutes,
                    remainingSeconds = s.remainingSeconds,
                    totalSeconds = s.totalSeconds,
                    pausesCount = s.pausesCount,
                    interruptionsCount = s.interruptionsCount
                )
            } else {
                preferencesManager.clearSavedFocusTimerState()
            }
        }
    }

    // --- Focus Timer Logic ---

    fun setFocusMode(mode: FocusMode) {
        if (_focusTimerState.value.status != TimerStatus.RUNNING) {
            val minutes = mode.defaultMinutes
            _focusTimerState.value = _focusTimerState.value.copy(
                mode = mode,
                selectedMinutes = minutes,
                remainingSeconds = minutes * 60,
                totalSeconds = minutes * 60
            )
            persistFocusTimerState()
        }
    }

    fun setCustomMinutes(minutes: Int) {
        if (_focusTimerState.value.status != TimerStatus.RUNNING) {
            val clamped = minutes.coerceIn(1, 240)
            _focusTimerState.value = _focusTimerState.value.copy(
                selectedMinutes = clamped,
                remainingSeconds = clamped * 60,
                totalSeconds = clamped * 60
            )
            persistFocusTimerState()
        }
    }

    fun linkTaskToFocus(task: TaskEntity?) {
        _focusTimerState.value = _focusTimerState.value.copy(linkedTask = task)
    }

    fun linkProjectToFocus(project: ProjectEntity?) {
        _focusTimerState.value = _focusTimerState.value.copy(linkedProject = project)
    }

    fun startTimer() {
        if (_focusTimerState.value.status == TimerStatus.RUNNING) return
        _focusTimerState.value = _focusTimerState.value.copy(status = TimerStatus.RUNNING)
        persistFocusTimerState()

        // Start white noise if selected
        val noise = _focusTimerState.value.selectedNoise
        if (noise != null && !_focusTimerState.value.isNoisePlaying) {
            whiteNoiseSynthesizer.startNoise(noise, _focusTimerState.value.noiseVolume, viewModelScope)
            _focusTimerState.value = _focusTimerState.value.copy(isNoisePlaying = true)
        }

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive && _focusTimerState.value.remainingSeconds > 0) {
                delay(1000L)
                if (_focusTimerState.value.status == TimerStatus.RUNNING) {
                    val current = _focusTimerState.value.remainingSeconds - 1
                    if (current <= 0) {
                        onTimerComplete()
                        break
                    } else {
                        _focusTimerState.value = _focusTimerState.value.copy(remainingSeconds = current)
                        if (current % 10 == 0) {
                            persistFocusTimerState()
                        }
                    }
                }
            }
        }
    }

    fun pauseTimer() {
        if (_focusTimerState.value.status == TimerStatus.RUNNING) {
            _focusTimerState.value = _focusTimerState.value.copy(
                status = TimerStatus.PAUSED,
                pausesCount = _focusTimerState.value.pausesCount + 1
            )
            persistFocusTimerState()
            if (_focusTimerState.value.isNoisePlaying) {
                whiteNoiseSynthesizer.stopNoise()
                _focusTimerState.value = _focusTimerState.value.copy(isNoisePlaying = false)
            }
        }
    }

    fun resumeTimer() {
        if (_focusTimerState.value.status == TimerStatus.PAUSED) {
            _focusTimerState.value = _focusTimerState.value.copy(status = TimerStatus.RUNNING)
            persistFocusTimerState()
            val noise = _focusTimerState.value.selectedNoise
            if (noise != null) {
                whiteNoiseSynthesizer.startNoise(noise, _focusTimerState.value.noiseVolume, viewModelScope)
                _focusTimerState.value = _focusTimerState.value.copy(isNoisePlaying = true)
            }
        }
    }

    fun stopTimer(isGivenUp: Boolean = true) {
        timerJob?.cancel()
        timerJob = null
        whiteNoiseSynthesizer.stopNoise()

        val state = _focusTimerState.value
        val actualSeconds = state.totalSeconds - state.remainingSeconds

        if (actualSeconds >= 60) {
            val session = FocusSessionEntity(
                id = UUID.randomUUID().toString(),
                taskTitle = state.linkedTask?.title ?: "自由专注 / Open Focus",
                taskId = state.linkedTask?.id,
                projectName = state.linkedProject?.name ?: state.linkedTask?.category ?: "General",
                mode = state.mode.name,
                plannedMinutes = state.selectedMinutes,
                actualSeconds = actualSeconds,
                isCompleted = !isGivenUp,
                interruptionsCount = if (isGivenUp) state.interruptionsCount + 1 else state.interruptionsCount,
                pausesCount = state.pausesCount,
                ambientNoise = state.selectedNoise?.name,
                timestamp = System.currentTimeMillis()
            )
            viewModelScope.launch {
                repository.insertSession(session)
            }
        }

        _focusTimerState.value = state.copy(
            status = TimerStatus.IDLE,
            remainingSeconds = state.selectedMinutes * 60,
            isNoisePlaying = false,
            unlockProgress = 0f
        )
        persistFocusTimerState()
    }

    private fun onTimerComplete() {
        timerJob?.cancel()
        timerJob = null
        whiteNoiseSynthesizer.stopNoise()

        val state = _focusTimerState.value
        val session = FocusSessionEntity(
            id = UUID.randomUUID().toString(),
            taskTitle = state.linkedTask?.title ?: "深度专注 / Deep Focus",
            taskId = state.linkedTask?.id,
            projectName = state.linkedProject?.name ?: "General",
            mode = state.mode.name,
            plannedMinutes = state.selectedMinutes,
            actualSeconds = state.totalSeconds,
            isCompleted = true,
            interruptionsCount = state.interruptionsCount,
            pausesCount = state.pausesCount,
            ambientNoise = state.selectedNoise?.name,
            timestamp = System.currentTimeMillis()
        )

        viewModelScope.launch {
            repository.insertSession(session)
            // If linked task exists, update actual minutes
            state.linkedTask?.let { task ->
                val updatedTask = task.copy(
                    actualMinutes = task.actualMinutes + state.selectedMinutes
                )
                repository.updateTask(updatedTask)
            }
        }

        _focusTimerState.value = state.copy(
            status = TimerStatus.COMPLETED,
            remainingSeconds = 0,
            isNoisePlaying = false,
            isSummaryDialogShown = true,
            completedSessionSummary = session
        )
    }

    fun dismissSummaryDialog() {
        val state = _focusTimerState.value
        _focusTimerState.value = state.copy(
            status = TimerStatus.IDLE,
            remainingSeconds = state.selectedMinutes * 60,
            isSummaryDialogShown = false,
            completedSessionSummary = null
        )
    }

    fun setUnlockProgress(progress: Float) {
        _focusTimerState.value = _focusTimerState.value.copy(unlockProgress = progress.coerceIn(0f, 1f))
    }

    fun toggleNoise(type: WhiteNoiseType) {
        val current = _focusTimerState.value.selectedNoise
        if (current == type && _focusTimerState.value.isNoisePlaying) {
            whiteNoiseSynthesizer.stopNoise()
            _focusTimerState.value = _focusTimerState.value.copy(selectedNoise = null, isNoisePlaying = false)
        } else {
            _focusTimerState.value = _focusTimerState.value.copy(selectedNoise = type, isNoisePlaying = true)
            whiteNoiseSynthesizer.startNoise(type, _focusTimerState.value.noiseVolume, viewModelScope)
        }
    }

    fun setNoiseVolume(volume: Float) {
        val vol = volume.coerceIn(0f, 1f)
        _focusTimerState.value = _focusTimerState.value.copy(noiseVolume = vol)
        whiteNoiseSynthesizer.setVolume(vol)
    }

    // --- Task Actions ---

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val isNowCompleted = task.status != "COMPLETED"
            val updated = task.copy(
                status = if (isNowCompleted) "COMPLETED" else "PENDING",
                completedAt = if (isNowCompleted) System.currentTimeMillis() else null
            )
            repository.updateTask(updated)
        }
    }

    fun saveTask(
        id: String?,
        title: String,
        description: String,
        priority: String,
        category: String,
        estimatedMinutes: Int,
        dueDate: Long,
        tags: String
    ) {
        viewModelScope.launch {
            val task = TaskEntity(
                id = id ?: UUID.randomUUID().toString(),
                title = title.ifBlank { "New Task" },
                description = description,
                priority = priority,
                category = category,
                estimatedMinutes = estimatedMinutes,
                dueDate = dueDate,
                tags = tags
            )
            repository.insertTask(task)
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            repository.deleteTask(taskId)
        }
    }

    // --- Habit Actions ---

    fun checkInHabit(habit: HabitEntity) {
        viewModelScope.launch {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val isAlreadyDoneToday = habit.lastCompletedDate == todayStr
            val updated = if (isAlreadyDoneToday) {
                habit.copy(
                    streakCount = (habit.streakCount - 1).coerceAtLeast(0),
                    lastCompletedDate = ""
                )
            } else {
                habit.copy(
                    streakCount = habit.streakCount + 1,
                    lastCompletedDate = todayStr
                )
            }
            repository.updateHabit(updated)
        }
    }

    fun addHabit(name: String, icon: String, colorHex: String) {
        viewModelScope.launch {
            val habit = HabitEntity(
                id = UUID.randomUUID().toString(),
                name = name,
                icon = icon,
                colorHex = colorHex
            )
            repository.insertHabit(habit)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    // --- Goal Actions ---

    fun addGoal(title: String, category: String, targetHours: Int, deadline: Long, colorHex: String) {
        viewModelScope.launch {
            val goal = GoalEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                category = category,
                targetHours = targetHours,
                deadline = deadline,
                colorHex = colorHex
            )
            repository.insertGoal(goal)
        }
    }

    // --- Schedule Actions ---

    fun toggleScheduleDone(schedule: ScheduleEntity) {
        viewModelScope.launch {
            repository.updateSchedule(schedule.copy(isDone = !schedule.isDone))
        }
    }

    fun addSchedule(title: String, category: String, startTime: Long, endTime: Long, colorHex: String) {
        viewModelScope.launch {
            val schedule = ScheduleEntity(
                id = UUID.randomUUID().toString(),
                title = title,
                category = category,
                startTime = startTime,
                endTime = endTime,
                colorHex = colorHex
            )
            repository.insertSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: ScheduleEntity) {
        viewModelScope.launch {
            repository.deleteSchedule(schedule)
        }
    }

    // --- App Limits Actions ---

    fun updateAppLimit(packageName: String, limitMinutes: Int, isBlockedInFocus: Boolean) {
        viewModelScope.launch {
            val existing = appUsages.value.find { it.packageName == packageName }
            if (existing != null) {
                repository.updateAppUsage(
                    existing.copy(
                        dailyLimitMinutes = limitMinutes,
                        isBlockedInFocus = isBlockedInFocus
                    )
                )
            }
        }
    }

    // --- AI Coach Actions ---

    fun sendAiMessage(prompt: String) {
        if (prompt.isBlank()) return
        val userMsg = AICoachMessage(sender = MessageSender.USER, text = prompt)
        _aiMessages.value = _aiMessages.value + userMsg
        isAiThinking.value = true

        viewModelScope.launch {
            delay(600L) // natural thinking cadence
            val response = aiCoachService.getCoachingResponse(
                userPrompt = prompt,
                tasks = tasks.value,
                schedules = schedules.value,
                screenTimeMinutes = appUsages.value.sumOf { it.todayUsageMinutes },
                allowTasks = userSettings.value.allowAiReadTasks,
                allowSchedule = userSettings.value.allowAiReadSchedule
            )
            _aiMessages.value = _aiMessages.value + response
            isAiThinking.value = false
        }
    }

    fun acceptAiSuggestedSchedules(schedulesList: List<ScheduleEntity>) {
        viewModelScope.launch {
            schedulesList.forEach { schedule ->
                repository.insertSchedule(schedule)
            }
        }
    }

    // --- Settings Actions ---

    fun setLanguage(language: AppLanguage) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }

    fun setAccentTheme(theme: AccentTheme) {
        viewModelScope.launch {
            preferencesManager.setAccentTheme(theme)
        }
    }

    fun setStrictMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setStrictMode(enabled)
        }
    }

    fun setHaptics(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setHaptics(enabled)
        }
    }

    fun setAppLock(enabled: Boolean, pin: String) {
        viewModelScope.launch {
            preferencesManager.setAppLock(enabled, pin)
        }
    }

    fun setAiPermissions(tasks: Boolean, schedule: Boolean, screenTime: Boolean) {
        viewModelScope.launch {
            preferencesManager.setAiPermissions(tasks, schedule, screenTime)
        }
    }

    fun setDailyTargetFocusMinutes(minutes: Int) {
        viewModelScope.launch {
            preferencesManager.setDailyTargetMinutes(minutes)
        }
    }

    // --- Manual Focus Session Actions (Editing / Adding / Adjusting Today's Focus) ---

    fun addManualFocusSession(
        taskTitle: String,
        projectName: String,
        mode: String,
        minutes: Int,
        timestamp: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            val session = FocusSessionEntity(
                id = UUID.randomUUID().toString(),
                taskTitle = taskTitle.ifBlank { "手动专注记录 / Manual Focus" },
                projectName = projectName.ifBlank { "General" },
                mode = mode,
                plannedMinutes = minutes,
                actualSeconds = minutes * 60,
                isCompleted = true,
                interruptionsCount = 0,
                pausesCount = 0,
                timestamp = timestamp
            )
            repository.insertSession(session)
        }
    }

    fun updateFocusSession(session: FocusSessionEntity) {
        viewModelScope.launch {
            repository.updateSession(session)
        }
    }

    fun deleteFocusSession(sessionId: String) {
        viewModelScope.launch {
            repository.deleteSession(sessionId)
        }
    }

    fun adjustTodayFocusMinutes(targetMinutes: Int) {
        viewModelScope.launch {
            val now = Calendar.getInstance()
            now.set(Calendar.HOUR_OF_DAY, 0)
            now.set(Calendar.MINUTE, 0)
            now.set(Calendar.SECOND, 0)
            now.set(Calendar.MILLISECOND, 0)
            val startOfDay = now.timeInMillis
            val endOfDay = startOfDay + 86400000L

            val currentSessions = sessions.value.filter { it.timestamp in startOfDay until endOfDay }
            val currentActualSeconds = currentSessions.sumOf { it.actualSeconds }
            val targetSeconds = targetMinutes * 60

            val diffSeconds = targetSeconds - currentActualSeconds
            if (diffSeconds > 0) {
                // Add a manual adjustment session to reach exact target
                val session = FocusSessionEntity(
                    id = UUID.randomUUID().toString(),
                    taskTitle = "专注时长校准 / Focus Time Adjustment",
                    projectName = "General",
                    mode = FocusMode.DEEP_WORK.name,
                    plannedMinutes = diffSeconds / 60,
                    actualSeconds = diffSeconds,
                    isCompleted = true,
                    interruptionsCount = 0,
                    pausesCount = 0,
                    timestamp = System.currentTimeMillis()
                )
                repository.insertSession(session)
            } else if (diffSeconds < 0) {
                // Scale down or delete today's sessions to match the requested target
                if (targetMinutes == 0) {
                    repository.deleteTodaySessions(startOfDay, endOfDay)
                } else {
                    // Replace today's sessions with a single adjusted session
                    repository.deleteTodaySessions(startOfDay, endOfDay)
                    val session = FocusSessionEntity(
                        id = UUID.randomUUID().toString(),
                        taskTitle = "校准今日专注记录 / Calibrated Focus",
                        projectName = "General",
                        mode = FocusMode.POMODORO.name,
                        plannedMinutes = targetMinutes,
                        actualSeconds = targetMinutes * 60,
                        isCompleted = true,
                        interruptionsCount = 0,
                        pausesCount = 0,
                        timestamp = System.currentTimeMillis()
                    )
                    repository.insertSession(session)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        whiteNoiseSynthesizer.stopNoise()
    }
}
