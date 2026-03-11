package com.devora.devicemanager

import android.app.AppOpsManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devora.devicemanager.sync.SyncManager
import com.devora.devicemanager.sync.SyncWorker
import com.devora.devicemanager.sync.DeviceInfoSyncWorker
import com.devora.devicemanager.sync.PolicySyncWorker
import com.devora.devicemanager.sync.LocationSyncWorker
import com.devora.devicemanager.ui.navigation.AppNavigation
import com.devora.devicemanager.ui.theme.DevoraTheme
import com.devora.devicemanager.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // If launched after Device Owner provisioning, mark enrollment as pending
        if (intent?.getBooleanExtra("enrollment_complete", false) == true) {
            Log.d("MainActivity", "Launched after Device Owner provisioning — enrollment pending")
        }

        // Schedule background sync (15-min interval via WorkManager)
        SyncWorker.schedule(this)
        DeviceInfoSyncWorker.schedule(this)
        PolicySyncWorker.schedule(this)
        LocationSyncWorker.schedule(this)

        // Start foreground heartbeat service — sends heartbeat every 30s so the
        // backend can detect uninstall within ~30–90 seconds.
        com.devora.devicemanager.sync.HeartbeatService.start(this)

        // Prompt for Usage Access permission (needed for app restriction enforcement
        // when Device Owner is not available)
        requestUsageStatsPermissionIfNeeded()

        setContent {
            val themeVm: ThemeViewModel = viewModel()
            val isDark = themeVm.isDark

            // Sync status: null = not started, "syncing" / "success" / "failed:reason"
            var syncStatus by remember { mutableStateOf<String?>(null) }

            // Trigger sync on first composition if Device Owner
            LaunchedEffect(Unit) {
                val isDeviceOwner = AdminReceiver.isDeviceOwner(this@MainActivity)
                Log.d("SYNC", "Device Owner status: $isDeviceOwner")

                if (isDeviceOwner) {
                    syncStatus = "syncing"
                    val result = SyncManager.syncDeviceData(
                        context = applicationContext,
                        employeeId = getStoredEmployeeId()
                    )
                    syncStatus = if (result.success) "success" else "failed:${result.message}"
                }
            }

            DevoraTheme(isDark = isDark) {
                Box {
                    // Main app navigation
                    AppNavigation(
                        isDark = isDark,
                        onThemeToggle = themeVm::toggle
                    )

                    // Sync status banner at the top
                    syncStatus?.let { status ->
                        val (text, bgColor) = when {
                            status == "syncing" -> "Syncing..." to Color(0xFF1976D2)
                            status == "success" -> "Synced" to Color(0xFF388E3C)
                            status.startsWith("failed") -> {
                                val reason = status.removePrefix("failed:")
                                "Sync Failed: $reason" to Color(0xFFD32F2F)
                            }
                            else -> return@let
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 48.dp)
                                .background(
                                    color = bgColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp)
                                .align(Alignment.TopCenter)
                        ) {
                            Text(
                                text = text,
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves the stored employee ID from SharedPreferences.
     * Falls back to "unknown" if not yet set.
     */
    private fun getStoredEmployeeId(): String {
        val prefs = getSharedPreferences("devora_enrollment", MODE_PRIVATE)
        return prefs.getString("employee_id", "unknown") ?: "unknown"
    }

    private fun requestUsageStatsPermissionIfNeeded() {
        // Only prompt on employee devices (enrolled)
        val prefs = getSharedPreferences("devora_enrollment", MODE_PRIVATE)
        val isEnrolled = prefs.getString("device_id", null) != null

        if (!isEnrolled) return

        // Check if Device Owner — if so, no need for UsageStats
        val dpm = getSystemService(DEVICE_POLICY_SERVICE) as android.app.admin.DevicePolicyManager
        if (dpm.isDeviceOwnerApp(packageName)) return

        // Check if already granted
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            packageName
        )
        if (mode == AppOpsManager.MODE_ALLOWED) return

        // Open Usage Access settings
        try {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (_: Exception) {
            Log.w("MainActivity", "Could not open Usage Access settings")
        }
    }
}
