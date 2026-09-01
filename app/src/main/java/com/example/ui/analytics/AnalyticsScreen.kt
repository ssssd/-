package com.example.ui.analytics

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.database.AppUsageEntity
import com.example.core.localization.LocalStrings
import com.example.ui.MainViewModel
import com.example.ui.components.FocusTrendCurveChart
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldAccent
import com.example.ui.theme.PriorityHighColor
import com.example.ui.theme.PriorityUrgentColor
import com.example.ui.theme.VioletAccent
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val sessions by viewModel.sessions.collectAsState()
    val appUsages by viewModel.appUsages.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    var selectedPeriodTab by remember { mutableIntStateOf(1) } // 0: Today, 1: Week, 2: Month, 3: Year
    var editingAppUsage by remember { mutableStateOf<AppUsageEntity?>(null) }
    var showAddAppUsageModal by remember { mutableStateOf(false) }
    var showShareReportModal by remember { mutableStateOf(false) }

    // Aggregate statistics
    val totalFocusMinutes = sessions.sumOf { it.actualSeconds } / 60
    val totalScreenMinutes = appUsages.sumOf { it.todayUsageMinutes }
    val completedTasks = tasks.count { it.status == "COMPLETED" }

    // Dynamic curve data points from real Room sessions
    val chartDataPoints = remember(sessions, selectedPeriodTab) {
        val cal = Calendar.getInstance()
        when (selectedPeriodTab) {
            0 -> {
                // Today (6 intervals)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val todayStart = cal.timeInMillis
                val slots = listOf(
                    "04:00" to (0..3),
                    "08:00" to (4..7),
                    "12:00" to (8..11),
                    "16:00" to (12..15),
                    "20:00" to (16..19),
                    "24:00" to (20..23)
                )
                slots.map { (label, hourRange) ->
                    val minsInSlot = sessions.filter { s ->
                        if (s.timestamp < todayStart) return@filter false
                        val sessionCal = Calendar.getInstance().apply { timeInMillis = s.timestamp }
                        val hour = sessionCal.get(Calendar.HOUR_OF_DAY)
                        hour in hourRange
                    }.sumOf { it.actualSeconds } / 60f
                    label to minsInSlot
                }
            }
            1 -> {
                // Week (Mon to Sun)
                val dayNames = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
                cal.firstDayOfWeek = Calendar.MONDAY
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val mondayStart = cal.timeInMillis
                val oneDayMillis = 86400000L

                (0..6).map { dayIndex ->
                    val dayStart = mondayStart + dayIndex * oneDayMillis
                    val dayEnd = dayStart + oneDayMillis
                    val mins = sessions.filter { it.timestamp in dayStart until dayEnd }
                        .sumOf { it.actualSeconds } / 60f
                    dayNames[dayIndex] to mins
                }
            }
            2 -> {
                // Month (4 weeks)
                val weekLabels = listOf("第1周", "第2周", "第3周", "第4周")
                cal.set(Calendar.DAY_OF_MONTH, 1)
                cal.set(Calendar.HOUR_OF_DAY, 0)
                cal.set(Calendar.MINUTE, 0)
                cal.set(Calendar.SECOND, 0)
                cal.set(Calendar.MILLISECOND, 0)
                val monthStart = cal.timeInMillis
                val oneWeekMillis = 7 * 86400000L

                (0..3).map { weekIndex ->
                    val wStart = monthStart + weekIndex * oneWeekMillis
                    val wEnd = wStart + oneWeekMillis
                    val mins = sessions.filter { it.timestamp in wStart until wEnd }
                        .sumOf { it.actualSeconds } / 60f
                    weekLabels[weekIndex] to mins
                }
            }
            else -> {
                // Year (6 bi-monthly periods)
                val monthLabels = listOf("1-2月", "3-4月", "5-6月", "7-8月", "9-10月", "11-12月")
                val currentYear = cal.get(Calendar.YEAR)
                (0..5).map { idx ->
                    val startMonth = idx * 2
                    val endMonth = startMonth + 1
                    val mins = sessions.filter { s ->
                        val sCal = Calendar.getInstance().apply { timeInMillis = s.timestamp }
                        sCal.get(Calendar.YEAR) == currentYear && sCal.get(Calendar.MONTH) in startMonth..endMonth
                    }.sumOf { it.actualSeconds } / 60f
                    monthLabels[idx] to mins
                }
            }
        }
    }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen")
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = strings.analyticsTitle,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "数据洞察与数字健康报告",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showShareReportModal = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = strings.shareReport, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        // Period Tabs
        item {
            TabRow(
                selectedTabIndex = selectedPeriodTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
            ) {
                listOf(strings.periodToday, strings.periodWeek, strings.periodMonth, strings.periodYear).forEachIndexed { index, title ->
                    Tab(
                        selected = selectedPeriodTab == index,
                        onClick = { selectedPeriodTab = index },
                        text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedPeriodTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }

        // Weekly Insight Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = strings.weeklyReport,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (totalFocusMinutes > 0) {
                                String.format(strings.weeklyReportSummary, "${totalFocusMinutes / 60}h ${totalFocusMinutes % 60}m")
                            } else {
                                "暂无专注记录，开启专注模式记录你的自律轨迹。"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Focus Trend Curve Chart Section (Replacing Heatmap)
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = strings.focusCurve,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = when (selectedPeriodTab) {
                                0 -> "今日时段分布"
                                1 -> "本周 7 天起伏走势"
                                2 -> "本月 4 周走势"
                                else -> "年度周期曲线"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    FocusTrendCurveChart(
                        dataPoints = chartDataPoints,
                        curveColor = MaterialTheme.colorScheme.primary,
                        secondaryColor = VioletAccent,
                        unitLabel = "分钟"
                    )
                }
            }
        }

        // Screen Time & App Limits Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                Text(
                    text = strings.screenTimeStats,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (totalScreenMinutes > 0) {
                        Text(
                            text = "总使用: ${totalScreenMinutes / 60}h ${totalScreenMinutes % 60}m",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    IconButton(
                        onClick = { showAddAppUsageModal = true },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Limit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (appUsages.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "暂无应用限制与使用数据",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "可添加需要防沉迷限制的应用或开启专注屏蔽",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showAddAppUsageModal = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("添加限制应用")
                        }
                    }
                }
            }
        } else {
            items(appUsages, key = { it.packageName }) { usage ->
                AppUsageItemCard(
                    usage = usage,
                    onEditLimit = { editingAppUsage = usage },
                    onToggleBlockInFocus = { isBlocked ->
                        viewModel.updateAppLimit(usage.packageName, usage.dailyLimitMinutes, isBlocked)
                    }
                )
            }
        }
    }

    // App Limit Editor Modal Sheet
    if (editingAppUsage != null) {
        val usage = editingAppUsage!!
        var limitMinutes by remember { mutableIntStateOf(usage.dailyLimitMinutes) }
        var isBlockedInFocus by remember { mutableStateOf(usage.isBlockedInFocus) }

        ModalBottomSheet(onDismissRequest = { editingAppUsage = null }) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "${strings.setLimit} · ${usage.appName}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = strings.dailyLimit, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (limitMinutes == 0) "无限制" else "$limitMinutes 分钟",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Slider(
                    value = limitMinutes.toFloat(),
                    onValueChange = { limitMinutes = it.toInt() },
                    valueRange = 0f..180f,
                    steps = 17
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(text = strings.autoBlockInFocus, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "开启专注模式时自动限制打开该应用",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isBlockedInFocus,
                        onCheckedChange = { isBlockedInFocus = it }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.updateAppLimit(usage.packageName, limitMinutes, isBlockedInFocus)
                        editingAppUsage = null
                    },
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = strings.save, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Add New App Limit Modal Sheet
    if (showAddAppUsageModal) {
        var newAppName by remember { mutableStateOf("") }
        var newLimitMinutes by remember { mutableIntStateOf(45) }
        var newBlockInFocus by remember { mutableStateOf(true) }

        ModalBottomSheet(onDismissRequest = { showAddAppUsageModal = false }) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "添加限制应用",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = newAppName,
                    onValueChange = { newAppName = it },
                    label = { Text("应用名称 (如：抖音、Bilibili、游戏)") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = strings.dailyLimit, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = if (newLimitMinutes == 0) "无限制" else "$newLimitMinutes 分钟",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Slider(
                    value = newLimitMinutes.toFloat(),
                    onValueChange = { newLimitMinutes = it.toInt() },
                    valueRange = 0f..180f,
                    steps = 17
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(text = strings.autoBlockInFocus, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "开启专注模式时自动限制打开该应用",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = newBlockInFocus,
                        onCheckedChange = { newBlockInFocus = it }
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (newAppName.isNotBlank()) {
                            val pkg = "com.custom." + System.currentTimeMillis()
                            viewModel.updateAppLimit(pkg, newLimitMinutes, newBlockInFocus)
                        }
                        showAddAppUsageModal = false
                    },
                    enabled = newAppName.isNotBlank(),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = strings.save, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Share Weekly Report Dialog
    if (showShareReportModal) {
        AlertDialog(
            onDismissRequest = { showShareReportModal = false },
            title = {
                Text(text = "周度专注报告 / Weekly Report", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "🔥 累计总专注时长：${totalFocusMinutes / 60} 小时 ${totalFocusMinutes % 60} 分钟")
                    Text(text = "📱 手机娱乐使用：${totalScreenMinutes / 60} 小时 ${totalScreenMinutes % 60} 分钟")
                    Text(text = "✅ 任务完成数量：$completedTasks 项")
                }
            },
            confirmButton = {
                Button(onClick = { showShareReportModal = false }) {
                    Text(strings.confirm)
                }
            }
        )
    }
}

@Composable
fun AppUsageItemCard(
    usage: AppUsageEntity,
    onEditLimit: () -> Unit,
    onToggleBlockInFocus: (Boolean) -> Unit
) {
    val isLimitExceeded = usage.dailyLimitMinutes > 0 && usage.todayUsageMinutes >= usage.dailyLimitMinutes

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLimitExceeded) PriorityUrgentColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(14.dp)
        ) {
            val icon = when (usage.iconName) {
                "videocam" -> Icons.Default.Videocam
                "camera_alt" -> Icons.Default.CameraAlt
                "smart_display" -> Icons.Default.SmartDisplay
                "chat" -> Icons.Default.Chat
                "sports_esports" -> Icons.Default.SportsEsports
                "description" -> Icons.Default.Description
                else -> Icons.Default.PhoneAndroid
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isLimitExceeded) PriorityUrgentColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                    )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isLimitExceeded) PriorityUrgentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = usage.appName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (usage.isBlockedInFocus) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shielded",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${usage.todayUsageMinutes} 分钟 · ${usage.openCount} 次打开" +
                            if (usage.dailyLimitMinutes > 0) " (限额 ${usage.dailyLimitMinutes}m)" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isLimitExceeded) PriorityUrgentColor else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(onClick = onEditLimit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Limit",
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
