package com.enterprise.devicemanager.data.collector

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.enterprise.devicemanager.admin.DeviceOwnerHelper
import com.enterprise.devicemanager.data.model.DeviceInfoRequest
import java.util.UUID

/**
 * Collects device information respecting Android 10+ API restrictions.
 *
 * Mandatory fields: model, manufacturer, osVersion, sdkVersion, serialNumber, UUID
 * Optional fields: IMEI, deviceType
 *
 * Android 10+ restrictions:
 * - IMEI requires READ_PRIVILEGED_PHONE_STATE (Device Owner only)
 * - Serial number requires READ_PRIVILEGED_PHONE_STATE or Device Owner
 * - For non-privileged apps, a generated UUID is used as unique identifier
 */
object DeviceInfoCollector {

    private const val TAG = "DeviceInfoCollector"

    fun collect(context: Context): DeviceInfoRequest {
        val deviceId = getDeviceId(context)
        val model = Build.MODEL ?: "Unknown"
        val manufacturer = Build.MANUFACTURER ?: "Unknown"
        val osVersion = Build.VERSION.RELEASE ?: "Unknown"
        val sdkVersion = Build.VERSION.SDK_INT.toString()
        val serialNumber = getSerialNumber(context)
        val imei = getImei(context)
        val deviceType = getDeviceType(context)

        Log.i(TAG, "Collected device info - Model: $model, Manufacturer: $manufacturer, " +
                "OS: $osVersion, SDK: $sdkVersion, Serial: $serialNumber, IMEI: $imei")

        return DeviceInfoRequest(
            deviceId = deviceId,
            model = model,
            manufacturer = manufacturer,
            osVersion = osVersion,
            sdkVersion = sdkVersion,
            serialNumber = serialNumber,
            imei = imei,
            deviceType = deviceType
        )
    }

    /**
     * Generate a stable device identifier.
     * Uses ANDROID_ID as base, falls back to generated UUID.
     */
    fun getDeviceId(context: Context): String {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        return if (!androidId.isNullOrBlank()) {
            "EDM-$androidId"
        } else {
            "EDM-${UUID.randomUUID()}"
        }
    }

    /**
     * Get hardware serial number.
     * Android 10+: Requires READ_PRIVILEGED_PHONE_STATE or Device Owner.
     * Falls back to "RESTRICTED" if not available.
     */
    @Suppress("DEPRECATION")
    private fun getSerialNumber(context: Context): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+: Restricted API
                if (DeviceOwnerHelper.isDeviceOwner(context) ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Build.getSerial()
                } else {
                    "RESTRICTED-API-LEVEL-${Build.VERSION.SDK_INT}"
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Android 8-9
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    Build.getSerial()
                } else {
                    Build.SERIAL
                }
            } else {
                Build.SERIAL
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Cannot read serial number: ${e.message}")
            "RESTRICTED"
        } catch (e: Exception) {
            Log.e(TAG, "Error reading serial number", e)
            null
        }
    }

    /**
     * Get IMEI (International Mobile Equipment Identity).
     * Android 10+: Only available to Device Owner apps.
     * Returns null if not available.
     */
    private fun getImei(context: Context): String? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+: IMEI only available to Device Owner / system apps
                if (DeviceOwnerHelper.isDeviceOwner(context)) {
                    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    tm.imei
                } else {
                    null // Not available to regular apps on Android 10+
                }
            } else {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    @Suppress("DEPRECATION")
                    tm.deviceId
                } else {
                    null
                }
            }
        } catch (e: SecurityException) {
            Log.w(TAG, "Cannot read IMEI: ${e.message}")
            null
        } catch (e: Exception) {
            Log.e(TAG, "Error reading IMEI", e)
            null
        }
    }

    /**
     * Classify the device type based on screen and telephony capabilities.
     */
    private fun getDeviceType(context: Context): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return when (tm?.phoneType) {
            TelephonyManager.PHONE_TYPE_GSM -> "GSM Phone"
            TelephonyManager.PHONE_TYPE_CDMA -> "CDMA Phone"
            TelephonyManager.PHONE_TYPE_SIP -> "SIP Phone"
            TelephonyManager.PHONE_TYPE_NONE -> "Tablet/WiFi-Only"
            else -> "Unknown"
        }
    }
}
