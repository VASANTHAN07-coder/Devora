package com.enterprise.devicemanager.data.network

import com.enterprise.devicemanager.data.model.AppInventoryRequest
import com.enterprise.devicemanager.data.model.DashboardStats
import com.enterprise.devicemanager.data.model.DeviceInfoRequest
import com.enterprise.devicemanager.data.model.DeviceResponse
import com.enterprise.devicemanager.data.model.EnrollRequest
import com.enterprise.devicemanager.data.model.EnrollResponse
import com.enterprise.devicemanager.data.model.AppInventoryItem
import com.enterprise.devicemanager.data.model.DeviceInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // Enrollment
    @POST("api/enroll")
    suspend fun enrollDevice(@Body request: EnrollRequest): Response<EnrollResponse>

    @GET("api/devices")
    suspend fun getAllDevices(): Response<List<DeviceResponse>>

    @GET("api/devices/{deviceId}")
    suspend fun getDevice(@Path("deviceId") deviceId: String): Response<DeviceResponse>

    // Device Info
    @POST("api/device-info")
    suspend fun sendDeviceInfo(@Body request: DeviceInfoRequest): Response<Map<String, Any>>

    @GET("api/device-info/{deviceId}")
    suspend fun getDeviceInfo(@Path("deviceId") deviceId: String): Response<List<DeviceInfoResponse>>

    // App Inventory
    @POST("api/app-inventory")
    suspend fun sendAppInventory(@Body request: AppInventoryRequest): Response<Map<String, Any>>

    @GET("api/app-inventory/{deviceId}")
    suspend fun getAppInventory(@Path("deviceId") deviceId: String): Response<List<AppInventoryItem>>

    // Dashboard
    @GET("api/dashboard/stats")
    suspend fun getDashboardStats(): Response<DashboardStats>
}
