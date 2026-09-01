package com.example.ui.settings

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.localization.AppLanguage
import com.example.core.localization.LocalStrings
import com.example.ui.MainViewModel
import com.example.ui.theme.AccentTheme
import com.example.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val strings = LocalStrings.current
    val userSettings by viewModel.userSettings.collectAsState()

    var showLanguagePicker by remember { mutableStateOf(false) }
    var showPlatformDocs by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }

    LazyColumn(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen")
    ) {
        // Top Header
        item {
            Text(
                text = strings.settingsTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // 1. Profile & Account Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                                    )
                                )
                        ) {
                            Text(
                                text = "FF",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = strings.profileName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = strings.profileEmail,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CloudDone,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "已同步",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { showExportDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "导出数据", style = MaterialTheme.typography.labelSmall)
                        }

                        Button(
                            onClick = { showPlatformDocs = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "平台说明", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // 2. Section: General & Localization
        item {
            SettingsSectionHeader(title = strings.sectionGeneral)
        }

        item {
            SettingsContainer {
                SettingsItem(
                    icon = Icons.Default.Language,
                    title = strings.language,
                    subtitle = userSettings.language.displayName + " (${userSettings.language.nativeName})",
                    onClick = { showLanguagePicker = true },
                    modifier = Modifier.testTag("setting_language_item")
                )
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                SettingsItem(
                    icon = Icons.Default.Timer,
                    title = strings.timeFormat,
                    subtitle = if (userSettings.is24HourFormat) strings.timeFormat24 else strings.timeFormat12,
                    onClick = { viewModel.preferencesManager }
                )
            }
        }

        // 3. Section: Appearance & Themes
        item {
            SettingsSectionHeader(title = strings.sectionAppearance)
        }

        item {
            SettingsContainer {
                // Theme Mode Switcher
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = strings.themeMode,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            ThemeMode.SYSTEM to strings.themeSystem,
                            ThemeMode.LIGHT to strings.themeLight,
                            ThemeMode.DARK to strings.themeDark
                        ).forEach { (mode, label) ->
                            val isSelected = userSettings.themeMode == mode
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.setThemeMode(mode) }
                            ) {
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 8.dp)) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))

                // Accent Color Swatches
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = strings.themeColor,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(AccentTheme.entries) { theme ->
                            val isSelected = userSettings.accentTheme == theme
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(theme.primary)
                                    .clickable { viewModel.setAccentTheme(theme) }
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Section: Focus & Timer Controls
        item {
            SettingsSectionHeader(title = strings.sectionFocus)
        }

        item {
            SettingsContainer {
                SettingsSwitchItem(
                    icon = Icons.Default.Lock,
                    title = strings.strictMode,
                    subtitle = "长按 5 秒方可中途退出，防止刷手机",
                    checked = userSettings.isStrictModeEnabled,
                    onCheckedChange = { viewModel.setStrictMode(it) }
                )
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                SettingsSwitchItem(
                    icon = Icons.Default.Vibration,
                    title = strings.hapticFeedback,
                    subtitle = "计时开始、完成与按键触感震动",
                    checked = userSettings.isHapticsEnabled,
                    onCheckedChange = { viewModel.setHaptics(it) }
                )
            }
        }

        // 5. Section: Privacy, AI Permissions & Security
        item {
            SettingsSectionHeader(title = strings.sectionDataPrivacy)
        }

        item {
            SettingsContainer {
                SettingsSwitchItem(
                    icon = Icons.Default.Fingerprint,
                    title = strings.appLockPin,
                    subtitle = "保护个人专注与日程隐私",
                    checked = userSettings.isAppLockEnabled,
                    onCheckedChange = { viewModel.setAppLock(it, "1234") }
                )
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                SettingsSwitchItem(
                    icon = Icons.Default.Security,
                    title = "允许 AI 读取本地任务",
                    subtitle = "用于智能拆解任务与时间估算",
                    checked = userSettings.allowAiReadTasks,
                    onCheckedChange = {
                        viewModel.setAiPermissions(it, userSettings.allowAiReadSchedule, userSettings.allowAiReadScreenTime)
                    }
                )
                Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
                SettingsSwitchItem(
                    icon = Icons.Default.Shield,
                    title = "允许 AI 读取时间表",
                    subtitle = "用于自动规划全天作息安排",
                    checked = userSettings.allowAiReadSchedule,
                    onCheckedChange = {
                        viewModel.setAiPermissions(userSettings.allowAiReadTasks, it, userSettings.allowAiReadScreenTime)
                    }
                )
            }
        }

        // 6. About
        item {
            SettingsSectionHeader(title = strings.sectionAbout)
        }

        item {
            SettingsContainer {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = strings.appName,
                    subtitle = "${strings.version} 1.0.0 (Commercial Ready)",
                    onClick = {}
                )
            }
        }
    }

    // Language Selector Sheet
    if (showLanguagePicker) {
        ModalBottomSheet(onDismissRequest = { showLanguagePicker = false }) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = strings.language,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.height(350.dp)) {
                    items(AppLanguage.entries) { lang ->
                        val isSelected = userSettings.language == lang
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setLanguage(lang)
                                    showLanguagePicker = false
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Column {
                                    Text(
                                        text = lang.nativeName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        ),
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = lang.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                if (isSelected) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Platform Limitations Doc Dialog
    if (showPlatformDocs) {
        AlertDialog(
            onDismissRequest = { showPlatformDocs = false },
            title = {
                Text(text = "系统平台能力与限制说明", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "📱 1. Android 原生能力：\n• 支持 UsageStatsManager 屏幕使用统计\n• 支持 Foreground Service 前台保活计时\n• 支持 AudioTrack 纯本地白噪音物理混音")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "🍎 2. iOS 对应能力：\n• Screen Time API / FamilyControls 深度应用限制\n• DeviceActivityMonitor 统计监控\n• 本地 CoreData / SQLite 同步")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "🔒 3. 隐私与数据安全：\n• 严格遵循本地优先 (Offline-First) 架构\n• AI 助手仅调用本地授权数据，绝不上传私密数据")
                }
            },
            confirmButton = {
                Button(onClick = { showPlatformDocs = false }) {
                    Text(strings.confirm)
                }
            }
        )
    }

    // Export Data Dialog
    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = {
                Text(text = "数据导出成功", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            },
            text = {
                Text(text = "已生成 FocusFlow_Backup_${System.currentTimeMillis()}.json\n包含全部任务、项目、专注时长记录与日程数据。")
            },
            confirmButton = {
                Button(onClick = { showExportDialog = false }) {
                    Text(strings.confirm)
                }
            }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
fun SettingsContainer(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
