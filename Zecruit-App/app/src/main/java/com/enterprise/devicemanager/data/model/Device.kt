package com.enterprise.devicemanager.data.model

import com.google.gson.annotations.SerializedName

// --- Enrollment ---

data class EnrollRequest(
    val deviceId: String,
    val enrollmentToken: String?,
    val enrollmentMethod: String
)

data class EnrollResponse(
    val message: String,
    val deviceId: String,
    val status: String
)

// --- Device (from backend) ---

data class DeviceResponse(
    val id: Long,
    val deviceId: String,
    val enrollmentToken: String?,
    val enrollmentMethod: String?,
    val enrolledAt: String?,
    val status: String
)

// --- Device Info ---

data class DeviceInfoRequest(
    val deviceId: String,
    val model: String,
    val manufacturer: String,
    val osVersion: String,
    val sdkVersion: String,
    val serialNumber: String?,
    val imei: String?,
    val deviceType: String?
)

data class DeviceInfoResponse(
    val id: Long,
    val deviceId: String,
    val model: String?,
    val manufacturer: String?,
    val osVersion: String?,
    val sdkVersion: String?,
    val serialNumber: String?,
    val imei: String?,
    val deviceType: String?,
    val collectedAt: String?
)

// --- App Inventory ---

data class AppInventoryItem(
    val appName: String,
    val packageName: String,
    val versionName: String?,
    val versionCode: Long?,
    val installSource: String?,
    @SerializedName("isSystemApp")
    val isSystemApp: Boolean
)

data class AppInventoryRequest(
    val deviceId: String,
    val apps: List<AppInventoryItem>
)

// --- Dashboard ---

data class DashboardStats(
    val totalDevices: Long,
    val activeDevices: Long,
    val inactiveDevices: Long,
    val totalApps: Long
)
