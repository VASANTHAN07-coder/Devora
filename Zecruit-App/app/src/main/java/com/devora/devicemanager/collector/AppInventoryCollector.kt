package com.devora.devicemanager.collector

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

/**
 * Metadata for a single installed application.
 */
data class AppInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val isSystemApp: Boolean
)

object AppInventoryCollector {

    /**
     * Returns a list of every installed app on the device.
     * Uses only API-26+ compatible, non-deprecated methods.
     */
    fun collect(context: Context): List<AppInfo> {
        val pm = context.packageManager

        val packages: List<PackageInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledPackages(PackageManager.PackageInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            pm.getInstalledPackages(0)
        }

        return packages.map { info -> info.toAppInfo(pm) }
    }

    private fun PackageInfo.toAppInfo(pm: PackageManager): AppInfo {
        val appLabel = applicationInfo?.loadLabel(pm)?.toString().orEmpty()

        val versionCode: Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            longVersionCode
        } else {
            @Suppress("DEPRECATION")
            versionCode.toLong()
        }

        val isSystem = applicationInfo?.let {
            (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0
        } ?: false

        return AppInfo(
            appName = appLabel,
            packageName = packageName.orEmpty(),
            versionName = versionName.orEmpty(),
            versionCode = versionCode,
            isSystemApp = isSystem
        )
    }
}
