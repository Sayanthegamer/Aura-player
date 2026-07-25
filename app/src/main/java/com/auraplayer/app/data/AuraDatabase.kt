package com.auraplayer.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.auraplayer.app.scrobble.ScrobbleQueueDao
import com.auraplayer.app.scrobble.ScrobbleQueueEntity

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE tracks ADD COLUMN hasArtwork INTEGER NOT NULL DEFAULT 1")
    }
}

@Database(
    entities = [
        LyricEntity::class,
        TrackEntity::class,
        AlbumEntity::class,
        ArtistEntity::class,
        ScrobbleQueueEntity::class
    ],
    version = 5,
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
                    .addMigrations(MIGRATION_4_5)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

