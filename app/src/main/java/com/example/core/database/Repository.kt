package com.example.core.database

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class FocusFlowRepository(private val database: AppDatabase) {

    val allTasks: Flow<List<TaskEntity>> = database.taskDao().getAllTasks()
    val topPendingTasks: Flow<List<TaskEntity>> = database.taskDao().getTopPendingTasks()
    val allProjects: Flow<List<ProjectEntity>> = database.projectDao().getAllProjects()
    val allSessions: Flow<List<FocusSessionEntity>> = database.focusSessionDao().getAllSessions()
    val allHabits: Flow<List<HabitEntity>> = database.habitDao().getAllHabits()
    val allGoals: Flow<List<GoalEntity>> = database.goalDao().getAllGoals()
    val allSchedules: Flow<List<ScheduleEntity>> = database.scheduleDao().getAllSchedules()
    val allAppUsages: Flow<List<AppUsageEntity>> = database.appUsageDao().getAllAppUsages()

    suspend fun insertTask(task: TaskEntity) = database.taskDao().insertTask(task)
    suspend fun updateTask(task: TaskEntity) = database.taskDao().updateTask(task)
    suspend fun deleteTask(taskId: String) = database.taskDao().deleteTaskById(taskId)

    suspend fun insertProject(project: ProjectEntity) = database.projectDao().insertProject(project)
    suspend fun deleteProject(project: ProjectEntity) = database.projectDao().deleteProject(project)

    suspend fun insertSession(session: FocusSessionEntity) = database.focusSessionDao().insertSession(session)
    suspend fun updateSession(session: FocusSessionEntity) = database.focusSessionDao().updateSession(session)
    suspend fun deleteSession(sessionId: String) = database.focusSessionDao().deleteSessionById(sessionId)
    suspend fun deleteTodaySessions(startOfDay: Long, endOfDay: Long) = database.focusSessionDao().deleteTodaySessions(startOfDay, endOfDay)

    suspend fun insertHabit(habit: HabitEntity) = database.habitDao().insertHabit(habit)
    suspend fun updateHabit(habit: HabitEntity) = database.habitDao().updateHabit(habit)
    suspend fun deleteHabit(habit: HabitEntity) = database.habitDao().deleteHabit(habit)

    suspend fun insertGoal(goal: GoalEntity) = database.goalDao().insertGoal(goal)
    suspend fun updateGoal(goal: GoalEntity) = database.goalDao().updateGoal(goal)
    suspend fun deleteGoal(goal: GoalEntity) = database.goalDao().deleteGoal(goal)

    suspend fun insertSchedule(schedule: ScheduleEntity) = database.scheduleDao().insertSchedule(schedule)
    suspend fun updateSchedule(schedule: ScheduleEntity) = database.scheduleDao().updateSchedule(schedule)
    suspend fun deleteSchedule(schedule: ScheduleEntity) = database.scheduleDao().deleteSchedule(schedule)

    suspend fun updateAppUsage(usage: AppUsageEntity) = database.appUsageDao().updateAppUsage(usage)
    suspend fun insertAppUsage(usage: AppUsageEntity) = database.appUsageDao().insertAppUsage(usage)
    suspend fun deleteAppUsage(packageName: String) = database.appUsageDao().deleteAppUsageByPackage(packageName)

    suspend fun clearAllData() {
        database.taskDao().deleteAllTasks()
        database.projectDao().deleteAllProjects()
        database.focusSessionDao().deleteAllSessions()
        database.habitDao().deleteAllHabits()
        database.goalDao().deleteAllGoals()
        database.scheduleDao().deleteAllSchedules()
        database.appUsageDao().deleteAllAppUsages()
    }

    fun seedInitialDataIfEmpty(scope: CoroutineScope) {
        // No dummy/sample demo data - completely clean initial state for the user
    }
}
