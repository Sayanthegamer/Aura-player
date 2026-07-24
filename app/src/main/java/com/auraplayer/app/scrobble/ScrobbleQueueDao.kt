package com.auraplayer.app.scrobble

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScrobbleQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScrobble(entity: ScrobbleQueueEntity): Long

    @Query("SELECT * FROM scrobble_queue WHERE status = 'PENDING' ORDER BY timestampMs ASC")
    suspend fun getPendingScrobbles(): List<ScrobbleQueueEntity>

    @Query("UPDATE scrobble_queue SET status = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<Long>)

    @Query("UPDATE scrobble_queue SET status = 'FAILED' WHERE id IN (:ids)")
    suspend fun markFailed(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM scrobble_queue WHERE artistName = :artistName")
    suspend fun getScrobbleCountForArtist(artistName: String): Int

    @Query("SELECT COUNT(*) FROM scrobble_queue WHERE trackTitle = :title AND artistName = :artistName")
    suspend fun getScrobbleCountForTrack(title: String, artistName: String): Int

    @Query("SELECT COUNT(*) FROM scrobble_queue WHERE status = 'PENDING'")
    fun getPendingCountFlow(): Flow<Int>
}
