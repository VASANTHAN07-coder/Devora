package com.enterprise.devicemanager.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Utility class for managing runtime permissions required by the MDM app.
 *
 * Required permissions:
 * - READ_PHONE_STATE: Access serial number, IMEI (Device Owner only)
 */
object PermissionHelper {

    /**
     * Permissions needed for device info collection.
     * READ_PHONE_STATE is required for accessing device identifiers.
     */
    val DEVICE_INFO_PERMISSIONS: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        arrayOf(Manifest.permission.READ_PHONE_STATE)
    } else {
        arrayOf(Manifest.permission.READ_PHONE_STATE)
    }

    /**
     * All permissions required by the MDM app.
     */
    val ALL_PERMISSIONS: Array<String> = arrayOf(
        Manifest.permission.READ_PHONE_STATE
    )

    /**
     * Check if all device info permissions are granted.
     */
    fun hasDeviceInfoPermissions(context: Context): Boolean {
        return DEVICE_INFO_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Check if a specific permission is granted.
     */
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Get a list of permissions that haven't been granted yet.
     */
    fun getMissingPermissions(context: Context, permissions: Array<String>): List<String> {
        return permissions.filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Get a human-readable description for a permission.
     */
    fun getPermissionDescription(permission: String): String {
        return when (permission) {
            Manifest.permission.READ_PHONE_STATE ->
                "Phone state access is needed to collect device identifiers (serial number, IMEI) for enterprise management."
            else -> "This permission is required for MDM functionality."
        }
    }
}
