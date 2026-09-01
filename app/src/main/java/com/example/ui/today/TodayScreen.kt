package com.example.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.database.FocusSessionEntity
import com.example.core.database.TaskEntity
import com.example.core.localization.LocalStrings
import com.example.ui.FocusMode
import com.example.ui.MainViewModel
import com.example.ui.components.EmptyStatePlaceholder
import com.example.ui.components.GlassCard
import com.example.ui.components.ImmersiveHeroProgressRing
import com.example.ui.components.MetricRingCard
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.LavenderPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    viewModel: MainViewModel,
    onNavigateToFocus: (TaskEntity?) -> Unit,
    onNavigateToAiCoach: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val tasks by viewModel.tasks.collectAsState()
    val topPendingTasks by viewModel.topPendingTasks.collectAsState()
    val sessions by viewModel.sessions.collectAsState()
    val appUsages by viewModel.appUsages.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()

    // Computations
    val todayFocusMinutes = sessions.sumOf { it.actualSeconds } / 60
    val targetMinutes = userSettings.dailyTargetFocusMinutes
    val focusProgress = if (targetMinutes > 0) (todayFocusMinutes.toFloat() / targetMinutes) else 0.7f

    val totalScreenTimeMinutes = appUsages.sumOf { it.todayUsageMinutes }
    val completedTasksCount = tasks.count { it.status == "COMPLETED" }
    val totalTasksCount = tasks.size
    val taskProgress = if (totalTasksCount > 0) (completedTasksCount.toFloat() / totalTasksCount) else 0.75f

    val nextImportantTask = topPendingTasks.firstOrNull() ?: tasks.firstOrNull { it.status != "COMPLETED" }

    // Dialog & sheet states for editing focus time and sessions
    var showAdjustFocusDialog by remember { mutableStateOf(false) }
    var showAddSessionModal by remember { mutableStateOf(false) }
    var showHistoryModal by remember { mutableStateOf(false) }
    var sessionToEdit by remember { mutableStateOf<FocusSessionEntity?>(null) }

    // Greeting logic
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (currentHour) {
        in 5..11 -> strings.greetingMorning
        in 12..17 -> strings.greetingAfternoon
        else -> strings.greetingEvening
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("today_screen")
    ) {
        // 1. Immersive Header Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFA855F7), // purple-500
                                        Color(0xFF6366F1)  // indigo-500
                                    )
                                )
                            )
                    ) {
                        Text(
                            text = "J",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        )
                    }

                    Column {
                        Text(
                            text = "$greeting，君浩",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 18.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "今天也要专注于重要的事情。",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E293B).copy(alpha = 0.5f))
                        .border(1.dp, Color(0xFF334155).copy(alpha = 0.5f), CircleShape)
                        .clickable { onNavigateToAiCoach() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. Hero Progress Circle (Glowing Obsidian Ring with Quick Editing Affordances)
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .clickable { showAdjustFocusDialog = true }
                        .testTag("hero_progress_ring_box")
                ) {
                    val formattedFocusTime = "${todayFocusMinutes / 60}h ${todayFocusMinutes % 60}m"

                    ImmersiveHeroProgressRing(
                        progress = focusProgress,
                        timeText = formattedFocusTime,
                        labelText = "今日专注目标 (${targetMinutes / 60}h ${targetMinutes % 60}m)",
                        primaryColor = LavenderPrimary,
                        sizeDp = 192.dp
                    )
                }

                // Action Bar: Modify / Record / History
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showAdjustFocusDialog = true }
                            .testTag("btn_adjust_focus_time")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Focus Time",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "修改今日时间",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showAddSessionModal = true }
                            .testTag("btn_add_manual_session")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Session",
                                tint = LavenderPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "补录记录",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { showHistoryModal = true }
                            .testTag("btn_view_sessions_history")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "History",
                                tint = EmeraldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "明细 (${sessions.size})",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // 3. Quick Stats Grid (2-column glass cards)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val screenHours = totalScreenTimeMinutes / 60
                val screenMins = totalScreenTimeMinutes % 60
                val screenTimeText = if (totalScreenTimeMinutes > 0) "${screenHours}h ${screenMins}m" else "2h 18m"

                MetricRingCard(
                    title = "手机使用",
                    valueText = screenTimeText,
                    subtitle = "短视频: 42m",
                    progress = if (totalScreenTimeMinutes > 0) (totalScreenTimeMinutes / 300f).coerceIn(0f, 1f) else 0.45f,
                    ringColor = LavenderPrimary,
                    deltaText = "-12%",
                    isDeltaPositive = true,
                    modifier = Modifier.weight(1f)
                )

                val displayCompleted = if (totalTasksCount > 0) completedTasksCount else 6
                val displayTotal = if (totalTasksCount > 0) totalTasksCount else 8
                val displayPercent = if (totalTasksCount > 0) (taskProgress * 100).toInt() else 75

                MetricRingCard(
                    title = "今日任务",
                    valueText = "$displayCompleted / $displayTotal",
                    subtitle = "完成率 $displayPercent%",
                    progress = if (totalTasksCount > 0) taskProgress else 0.75f,
                    ringColor = Color(0xFF6366F1),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 4. Next Important Task Card (Indigo tinted hero card)
        item {
            Column {
                Text(
                    text = "下一件重要任务",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                )

                val activeTaskTitle = nextImportantTask?.title ?: "写毕业论文 - 重点章节"
                val activeTaskCategory = nextImportantTask?.category ?: "学习项目"
                val activeTaskEstimated = nextImportantTask?.estimatedMinutes ?: 60

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF6366F1).copy(alpha = 0.18f))
                        .border(1.dp, Color(0xFF6366F1).copy(alpha = 0.35f), RoundedCornerShape(24.dp))
                        .clickable {
                            if (nextImportantTask != null) {
                                viewModel.linkTaskToFocus(nextImportantTask)
                                viewModel.setFocusMode(FocusMode.DEEP_WORK)
                                onNavigateToFocus(nextImportantTask)
                            } else {
                                onNavigateToFocus(null)
                            }
                        }
                        .testTag("next_task_hero_card")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF6366F1))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FormatListBulleted,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = activeTaskTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "预计 $activeTaskEstimated 分钟 · $activeTaskCategory",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = Color(0xFFA5B4FC)
                                )
                            }
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6366F1))
                                .testTag("start_focus_next_task_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Start Focus",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Analytics Preview / AI Focus Suggestion
        item {
            GlassCard(
                cornerRadius = 24.dp,
                onClick = onNavigateToAiCoach,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_insight_glass_card")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "AI 专注建议",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "你最容易在 21:00 分心，建议开启严格模式。",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A2A2A))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Insight",
                            tint = LavenderPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // 6. Today's Priority Task Checklist
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = "${strings.allTasks} (${completedTasksCount}/$totalTasksCount)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = strings.scheduleMyDay,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier
                        .clickable { onNavigateToSchedule() }
                        .padding(4.dp)
                )
            }
        }

        if (tasks.isEmpty()) {
            item {
                EmptyStatePlaceholder(
                    title = strings.noTasksTitle,
                    subtitle = strings.noTasksDesc
                )
            }
        } else {
            items(tasks.take(6), key = { it.id }) { task ->
                val isCompleted = task.status == "COMPLETED"
                GlassCard(
                    cornerRadius = 18.dp,
                    onClick = { viewModel.toggleTaskCompletion(task) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("task_item_${task.id}")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(14.dp)
                    ) {
                        IconButton(
                            onClick = { viewModel.toggleTaskCompletion(task) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = "Toggle Complete",
                                tint = if (isCompleted) EmeraldAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.SemiBold
                                ),
                                color = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (task.tags.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = task.tags.split(",").joinToString(" · ") { "#$it" },
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        PriorityBadge(priority = task.priority)
                    }
                }
            }
        }
    }

    // --- Adjust Today's Focus Time Dialog ---
    if (showAdjustFocusDialog) {
        var sliderMinutes by remember { mutableFloatStateOf(todayFocusMinutes.toFloat().coerceIn(0f, 600f)) }
        var targetInputMinutes by remember { mutableFloatStateOf(targetMinutes.toFloat().coerceIn(30f, 720f)) }

        AlertDialog(
            onDismissRequest = { showAdjustFocusDialog = false },
            title = {
                Text(
                    text = "修改今日专注时间与目标",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "你可以直接调整今天的累计专注时长，或修改每日专注目标：",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "今日已专注时长", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${sliderMinutes.toInt() / 60}小时 ${sliderMinutes.toInt() % 60}分钟 (${sliderMinutes.toInt()} 分钟)",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = LavenderPrimary
                            )
                        }
                        Slider(
                            value = sliderMinutes,
                            onValueChange = { sliderMinutes = it },
                            valueRange = 0f..600f,
                            steps = 39,
                            colors = SliderDefaults.colors(
                                thumbColor = LavenderPrimary,
                                activeTrackColor = LavenderPrimary
                            )
                        )
                    }

                    Column {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "每日目标时长", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = "${targetInputMinutes.toInt() / 60}小时 ${targetInputMinutes.toInt() % 60}分钟",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = targetInputMinutes,
                            onValueChange = { targetInputMinutes = it },
                            valueRange = 30f..720f,
                            steps = 22,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    // Quick presets
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(60, 120, 180, 240).forEach { presetMins ->
                            OutlinedButton(
                                onClick = { sliderMinutes = presetMins.toFloat() },
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(text = "${presetMins / 60}h", fontSize = 12.sp)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.adjustTodayFocusMinutes(sliderMinutes.toInt())
                        viewModel.setDailyTargetFocusMinutes(targetInputMinutes.toInt())
                        showAdjustFocusDialog = false
                    }
                ) {
                    Text("保存更改")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdjustFocusDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // --- Manual Add / Edit Focus Session Modal ---
    if (showAddSessionModal || sessionToEdit != null) {
        val isEditing = sessionToEdit != null
        var title by remember { mutableStateOf(sessionToEdit?.taskTitle ?: "") }
        var project by remember { mutableStateOf(sessionToEdit?.projectName ?: "General") }
        var minutesText by remember { mutableStateOf((sessionToEdit?.actualSeconds?.div(60) ?: 45).toString()) }
        var selectedMode by remember { mutableStateOf(sessionToEdit?.mode ?: FocusMode.DEEP_WORK.name) }

        ModalBottomSheet(
            onDismissRequest = {
                showAddSessionModal = false
                sessionToEdit = null
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (isEditing) "编辑专注记录" else "手动补录专注记录",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("专注任务名称") },
                    placeholder = { Text("例如：完成毕业论文核心章节") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = project,
                    onValueChange = { project = it },
                    label = { Text("所属项目 / 分类") },
                    placeholder = { Text("例如：毕业论文 / Thesis") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { if (it.all { char -> char.isDigit() }) minutesText = it },
                    label = { Text("专注时长 (分钟)") },
                    placeholder = { Text("45") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "专注模式",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        FocusMode.POMODORO.name to "番茄钟",
                        FocusMode.DEEP_WORK.name to "深度工作",
                        FocusMode.QUICK_FOCUS.name to "快速专注"
                    ).forEach { (mode, label) ->
                        FilterChip(
                            selected = selectedMode == mode,
                            onClick = { selectedMode = mode },
                            label = { Text(label) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isEditing) {
                        OutlinedButton(
                            onClick = {
                                sessionToEdit?.id?.let { viewModel.deleteFocusSession(it) }
                                sessionToEdit = null
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("删除")
                        }
                    }

                    Button(
                        onClick = {
                            val mins = minutesText.toIntOrNull() ?: 30
                            if (isEditing) {
                                sessionToEdit?.let { existing ->
                                    viewModel.updateFocusSession(
                                        existing.copy(
                                            taskTitle = title.ifBlank { "专注任务" },
                                            projectName = project.ifBlank { "General" },
                                            mode = selectedMode,
                                            plannedMinutes = mins,
                                            actualSeconds = mins * 60
                                        )
                                    )
                                }
                            } else {
                                viewModel.addManualFocusSession(
                                    taskTitle = title,
                                    projectName = project,
                                    mode = selectedMode,
                                    minutes = mins
                                )
                            }
                            showAddSessionModal = false
                            sessionToEdit = null
                        },
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(if (isEditing) "保存修改" else "立即添加")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // --- Session History & Records List Modal ---
    if (showHistoryModal) {
        ModalBottomSheet(
            onDismissRequest = { showHistoryModal = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "专注历史记录明细",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = {
                        showHistoryModal = false
                        showAddSessionModal = true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add session", tint = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (sessions.isEmpty()) {
                    EmptyStatePlaceholder(
                        title = "暂无专注记录",
                        subtitle = "点击上方加号或开启专注计时即可生成记录"
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
                        items(sessions, key = { it.id }) { session ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable {
                                        sessionToEdit = session
                                        showHistoryModal = false
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.padding(14.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = session.taskTitle,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${session.projectName} · ${dateFormat.format(Date(session.timestamp))}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "${session.actualSeconds / 60}m",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = LavenderPrimary
                                            )
                                        )
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

