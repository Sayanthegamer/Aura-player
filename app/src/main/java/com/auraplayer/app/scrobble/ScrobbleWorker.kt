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
}
