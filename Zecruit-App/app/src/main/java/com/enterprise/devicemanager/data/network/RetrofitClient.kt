package com.enterprise.devicemanager.data.network

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // For emulator use 10.0.2.2, for physical device use your PC's local IP
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Basic Auth credentials matching backend SecurityConfig
    private const val USERNAME = "mdm-device"
    private const val PASSWORD = "SecurePass123"

    private val authInterceptor = Interceptor { chain ->
        val credentials = "$USERNAME:$PASSWORD"
        val basicAuth = "Basic " + Base64.encodeToString(
            credentials.toByteArray(), Base64.NO_WRAP
        )
        val request = chain.request().newBuilder()
            .header("Authorization", basicAuth)
            .header("Content-Type", "application/json")
            .build()
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
