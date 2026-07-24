package com.auraplayer.app.repository

import com.auraplayer.app.data.AlbumEntity
import com.auraplayer.app.data.ArtistEntity
import com.auraplayer.app.data.AuraDatabase
import com.auraplayer.app.data.MediaScanner
import com.auraplayer.app.data.ScanState
import com.auraplayer.app.data.TrackEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class MusicRepository(
    private val database: AuraDatabase,
    private val mediaScanner: MediaScanner
) {
    val scanState: StateFlow<ScanState> = mediaScanner.scanState

    fun getAllTracks(): Flow<List<TrackEntity>> = database.trackDao().getAllTracksFlow()

    fun getAllAlbums(): Flow<List<AlbumEntity>> = database.albumDao().getAllAlbumsFlow()

    fun getAllArtists(): Flow<List<ArtistEntity>> = database.artistDao().getAllArtistsFlow()

    fun getTracksByAlbum(albumId: Long): Flow<List<TrackEntity>> = database.trackDao().getTracksByAlbum(albumId)

    fun getTracksByArtist(artistId: Long): Flow<List<TrackEntity>> = database.trackDao().getTracksByArtist(artistId)

    fun searchTracks(query: String): Flow<List<TrackEntity>> = database.trackDao().searchTracks(query)

    suspend fun getTrackById(id: Long): TrackEntity? = database.trackDao().getTrackById(id)

    suspend fun rescanLibrary(blacklistedFolders: Set<String> = emptySet()): Int {
        return mediaScanner.scanLibrary(blacklistedFolders)
    }
}
