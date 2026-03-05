package com.enterprise.devicemanager.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.enterprise.devicemanager.data.collector.AppInventoryCollector
import com.enterprise.devicemanager.data.repository.DeviceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * WorkManager Worker that periodically syncs device info and app inventory
 * to the MDM backend. Runs every 6 hours by default.
 *
 * Sync operations:
 * 1. Collect and send device info (model, OS, serial, etc.)
 * 2. Collect and send app inventory (all installed packages)
 */
class DeviceSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "DeviceSyncWorker"
        const val WORK_NAME = "device_sync_periodic"
        private const val SYNC_INTERVAL_HOURS = 6L

        /**
         * Schedule periodic device sync using WorkManager.
         * Uses KEEP policy so existing work isn't replaced.
         */
        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<DeviceSyncWorker>(
                SYNC_INTERVAL_HOURS, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .addTag("mdm_sync")
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )

            Log.i(TAG, "Periodic sync scheduled: every $SYNC_INTERVAL_HOURS hours")
        }

        /**
         * Trigger an immediate one-time sync.
         */
        fun syncNow(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val oneTimeRequest = OneTimeWorkRequestBuilder<DeviceSyncWorker>()
                .setConstraints(constraints)
                .addTag("mdm_sync_immediate")
                .build()

            WorkManager.getInstance(context).enqueue(oneTimeRequest)
            Log.i(TAG, "Immediate sync triggered")
        }

        /**
         * Cancel all scheduled sync work.
         */
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            Log.i(TAG, "Periodic sync cancelled")
        }
    }

    override suspend fun doWork(): Result {
        Log.i(TAG, "Starting device sync (attempt ${runAttemptCount + 1})")

        return withContext(Dispatchers.IO) {
            try {
                val repo = DeviceRepository(applicationContext)

                // Step 1: Sync device info
                val deviceInfoResult = repo.collectAndSendDeviceInfo()
                if (deviceInfoResult.isSuccess) {
                    Log.i(TAG, "Device info synced successfully")
                } else {
                    Log.w(TAG, "Device info sync failed: ${deviceInfoResult.exceptionOrNull()?.message}")
                }

                // Step 2: Sync app inventory
                val apps = AppInventoryCollector.collect(applicationContext)
                val inventoryResult = repo.sendAppInventory(apps)
                if (inventoryResult.isSuccess) {
                    Log.i(TAG, "App inventory synced successfully (${apps.size} apps)")
                } else {
                    Log.w(TAG, "App inventory sync failed: ${inventoryResult.exceptionOrNull()?.message}")
                }

                // Consider success if at least one sync succeeded
                if (deviceInfoResult.isSuccess || inventoryResult.isSuccess) {
                    Log.i(TAG, "Sync completed successfully")
                    Result.success()
                } else {
                    Log.w(TAG, "Both sync operations failed, will retry")
                    Result.retry()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sync worker error", e)
                if (runAttemptCount < 3) {
                    Result.retry()
                } else {
                    Log.e(TAG, "Max retry attempts reached, marking as failure")
                    Result.failure()
                }
            }
        }
    }
}
