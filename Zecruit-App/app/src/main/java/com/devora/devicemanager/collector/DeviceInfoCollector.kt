package com.devora.devicemanager.collector

import android.content.Context
import android.os.Build
import java.util.UUID

/**
 * Holds device-level metadata.  All fields are non-null and safe for API 26+.
 */
data class DeviceInfo(
    val deviceId: String,        // App-scoped UUID persisted in SharedPreferences
    val model: String,           // Build.MODEL
    val manufacturer: String,    // Build.MANUFACTURER
    val brand: String,           // Build.BRAND
    val board: String,           // Build.BOARD
    val osVersion: String,       // Build.VERSION.RELEASE
    val sdkVersion: Int          // Build.VERSION.SDK_INT
)

object DeviceInfoCollector {

    private const val PREFS_NAME = "device_manager_prefs"
    private const val KEY_DEVICE_UUID = "device_uuid"

    /**
     * Collects device information using only public, non-deprecated Build fields.
     * A stable UUID is generated once and reused across calls.
     */
    fun collect(context: Context): DeviceInfo {
        val deviceId = getOrCreateDeviceId(context)

        return DeviceInfo(
            deviceId = deviceId,
            model = Build.MODEL.orEmpty(),
            manufacturer = Build.MANUFACTURER.orEmpty(),
            brand = Build.BRAND.orEmpty(),
            board = Build.BOARD.orEmpty(),
            osVersion = Build.VERSION.RELEASE.orEmpty(),
            sdkVersion = Build.VERSION.SDK_INT
        )
    }

    /**
     * Returns a persistent UUID for this app installation.
     * Creates one on the first call and stores it in SharedPreferences.
     */
    private fun getOrCreateDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_DEVICE_UUID, null) ?: run {
            val newId = UUID.randomUUID().toString()
            prefs.edit().putString(KEY_DEVICE_UUID, newId).apply()
            newId
        }
    }
}
