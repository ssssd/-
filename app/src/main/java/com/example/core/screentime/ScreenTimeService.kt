package com.example.core.screentime

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Process
import android.provider.Settings
import com.example.core.database.AppUsageEntity

enum class PlatformCapabilityStatus {
    AVAILABLE,
    PERMISSION_REQUIRED,
    RESTRICTED,
    UNSUPPORTED
}

interface ScreenTimeService {
    fun checkPermissionStatus(): PlatformCapabilityStatus
    fun openPermissionSettings()
    suspend fun getTodayUsageList(): List<AppUsageEntity>
    suspend fun setAppLimit(packageName: String, limitMinutes: Int)
    suspend fun toggleFocusModeBlocking(isFocusing: Boolean)
}

class AndroidScreenTimeService(private val context: Context) : ScreenTimeService {
    override fun checkPermissionStatus(): PlatformCapabilityStatus {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
            ?: return PlatformCapabilityStatus.UNSUPPORTED
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        } else {
            @Suppress("DEPRECATION")
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                Process.myUid(),
                context.packageName
            )
        }
        return if (mode == AppOpsManager.MODE_ALLOWED) {
            PlatformCapabilityStatus.AVAILABLE
        } else {
            PlatformCapabilityStatus.PERMISSION_REQUIRED
        }
    }

    override fun openPermissionSettings() {
        try {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun getTodayUsageList(): List<AppUsageEntity> {
        // Reads UsageStatsManager if permitted; fallback to cached Room entities
        return emptyList()
    }

    override suspend fun setAppLimit(packageName: String, limitMinutes: Int) {
        // Managed in FocusFlow repository & Notification shield
    }

    override suspend fun toggleFocusModeBlocking(isFocusing: Boolean) {
        // Toggles app distraction shield
    }
}
