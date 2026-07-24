package com.auraplayer.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LyricDao {

    @Query("SELECT * FROM lyrics WHERE trackId = :trackId LIMIT 1")
    suspend fun getLyricForTrack(trackId: String): LyricEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLyric(lyric: LyricEntity)

    @Query("UPDATE lyrics SET userOffsetMs = :offsetMs WHERE trackId = :trackId")
    suspend fun updateLyricOffset(trackId: String, offsetMs: Long)
}
