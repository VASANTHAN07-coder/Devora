package com.enterprise.devicemanager.data.repository

import android.content.Context
import android.util.Log
import com.enterprise.devicemanager.data.collector.DeviceInfoCollector
import com.enterprise.devicemanager.data.model.AppInventoryItem
import com.enterprise.devicemanager.data.model.AppInventoryRequest
import com.enterprise.devicemanager.data.model.DashboardStats
import com.enterprise.devicemanager.data.model.DeviceInfoRequest
import com.enterprise.devicemanager.data.model.DeviceInfoResponse
import com.enterprise.devicemanager.data.model.DeviceResponse
import com.enterprise.devicemanager.data.model.EnrollRequest
import com.enterprise.devicemanager.data.model.EnrollResponse
import com.enterprise.devicemanager.data.network.RetrofitClient

/**
 * Repository handling all device-related backend communication.
 */
class DeviceRepository(private val context: Context) {

    companion object {
        private const val TAG = "DeviceRepository"
    }

    private val api = RetrofitClient.apiService

    // --- Enrollment ---

    suspend fun enrollDevice(token: String?): Result<EnrollResponse> {
        return try {
            val deviceId = DeviceInfoCollector.getDeviceId(context)
            val request = EnrollRequest(
                deviceId = deviceId,
                enrollmentToken = token,
                enrollmentMethod = if (!token.isNullOrBlank()) "TOKEN" else "MANUAL"
            )
            val response = api.enrollDevice(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Enrollment failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Enrollment error", e)
            Result.failure(e)
        }
    }

    // --- Device Info ---

    suspend fun collectAndSendDeviceInfo(): Result<Unit> {
        return try {
            val deviceInfo = DeviceInfoCollector.collect(context)
            val response = api.sendDeviceInfo(deviceInfo)
            if (response.isSuccessful) {
                Log.i(TAG, "Device info sent successfully")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to send device info: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Device info error", e)
            Result.failure(e)
        }
    }

    fun getLocalDeviceInfo(): DeviceInfoRequest {
        return DeviceInfoCollector.collect(context)
    }

    suspend fun fetchDeviceInfo(deviceId: String): Result<List<DeviceInfoResponse>> {
        return try {
            val response = api.getDeviceInfo(deviceId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- App Inventory ---

    suspend fun sendAppInventory(apps: List<AppInventoryItem>): Result<Unit> {
        return try {
            val deviceId = DeviceInfoCollector.getDeviceId(context)
            val request = AppInventoryRequest(deviceId = deviceId, apps = apps)
            val response = api.sendAppInventory(request)
            if (response.isSuccessful) {
                Log.i(TAG, "App inventory sent successfully")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to send inventory: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "App inventory error", e)
            Result.failure(e)
        }
    }

    // --- Devices List ---

    suspend fun fetchAllDevices(): Result<List<DeviceResponse>> {
        return try {
            val response = api.getAllDevices()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Dashboard ---

    suspend fun fetchDashboardStats(): Result<DashboardStats> {
        return try {
            val response = api.getDashboardStats()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}