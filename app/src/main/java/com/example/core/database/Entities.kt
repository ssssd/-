package com.example.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String = "",
    val priority: String = "NORMAL", // LOW, NORMAL, HIGH, URGENT
    val status: String = "PENDING", // PENDING, IN_PROGRESS, COMPLETED, OVERDUE, CANCELLED
    val projectId: String = "default",
    val category: String = "General",
    val tags: String = "",
    val dueDate: Long = 0L,
    val estimatedMinutes: Int = 30,
    val actualMinutes: Int = 0,
    val subtasksJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val sortOrder: Int = 0
)

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey val id: String,
    val name: String,
    val colorHex: String,
    val icon: String = "folder",
    val targetMinutes: Int = 600,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey val id: String,
    val taskTitle: String,
    val taskId: String? = null,
    val projectName: String = "General",
    val mode: String = "POMODORO", // POMODORO, DEEP_WORK, QUICK_FOCUS, CUSTOM
    val plannedMinutes: Int = 25,
    val actualSeconds: Int = 0,
    val isCompleted: Boolean = true,
    val interruptionsCount: Int = 0,
    val pausesCount: Int = 0,
    val ambientNoise: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey val id: String,
    val name: String,
    val frequency: String = "DAILY",
    val streakCount: Int = 0,
    val lastCompletedDate: String = "",
    val targetDaysPerWeek: Int = 7,
    val icon: String = "check_circle",
    val colorHex: String = "#6366F1",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String = "General",
    val targetHours: Int = 50,
    val currentHours: Float = 0f,
    val deadline: Long = 0L,
    val colorHex: String = "#8B5CF6",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "schedules")
data class ScheduleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val taskId: String? = null,
    val category: String = "Work",
    val startTime: Long,
    val endTime: Long,
    val colorHex: String = "#3B82F6",
    val isDone: Boolean = false
)

@Entity(tableName = "app_usages")
data class AppUsageEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val category: String = "Social",
    val todayUsageMinutes: Int = 0,
    val openCount: Int = 0,
    val dailyLimitMinutes: Int = 0,
    val isBlockedInFocus: Boolean = true,
    val iconName: String = "phone_android"
)
