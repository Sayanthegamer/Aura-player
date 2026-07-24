package com.auraplayer.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.auraplayer.app.scrobble.ScrobbleQueueDao
import com.auraplayer.app.scrobble.ScrobbleQueueEntity

@Database(
    entities = [
        LyricEntity::class,
        TrackEntity::class,
        AlbumEntity::class,
        ArtistEntity::class,
        ScrobbleQueueEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AuraDatabase : RoomDatabase() {

    abstract fun lyricDao(): LyricDao
    abstract fun trackDao(): TrackDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun scrobbleQueueDao(): ScrobbleQueueDao

    companion object {
        @Volatile
        private var INSTANCE: AuraDatabase? = null

        fun getInstance(context: Context): AuraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuraDatabase::class.java,
                    "aura_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
