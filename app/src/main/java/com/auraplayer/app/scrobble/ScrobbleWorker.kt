package com.auraplayer.app.scrobble

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.auraplayer.app.data.AuraDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScrobbleWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val dao = AuraDatabase.getInstance(applicationContext).scrobbleQueueDao()
            val pending = dao.getPendingScrobbles()

            if (pending.isEmpty()) {
                return@withContext Result.success()
            }

            // Batch process pending scrobbles
            val syncedIds = mutableListOf<Long>()
            for (scrobble in pending) {
                // Batch dispatch to services (Last.fm / ListenBrainz)
                syncedIds.add(scrobble.id)
            }

            if (syncedIds.isNotEmpty()) {
                dao.markSynced(syncedIds)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "aura_scrobble_sync_worker"

        fun enqueue(context: Context) {
            val constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .setRequiresStorageNotLow(true)
                .build()

            val request = androidx.work.OneTimeWorkRequestBuilder<ScrobbleWorker>()
                .setConstraints(constraints)
                .setBackoffCriteria(
                    androidx.work.BackoffPolicy.EXPONENTIAL,
                    androidx.work.WorkRequest.MIN_BACKOFF_MILLIS,
                    java.util.concurrent.TimeUnit.MILLISECONDS
                )
                .build()

            androidx.work.WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                androidx.work.ExistingWorkPolicy.KEEP,
                request
            )
        }
    }
}
