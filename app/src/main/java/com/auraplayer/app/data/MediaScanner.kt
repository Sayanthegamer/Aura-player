package com.auraplayer.app.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.auraplayer.app.metadata.MetadataExtractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File

sealed class ScanState {
    object Idle : ScanState()
    data class Scanning(val current: Int, val total: Int) : ScanState()
    data class Completed(val trackCount: Int) : ScanState()
    data class Error(val message: String) : ScanState()
}

class MediaScanner(
    private val context: Context,
    private val database: AuraDatabase
) {
    private val _scanState = MutableStateFlow<ScanState>(ScanState.Idle)
    val scanState: StateFlow<ScanState> = _scanState.asStateFlow()

    suspend fun scanLibrary(blacklistedFolders: Set<String> = emptySet()): Int = withContext(Dispatchers.IO) {
        try {
            _scanState.value = ScanState.Scanning(0, 0)
            val contentResolver: ContentResolver = context.contentResolver

            val collection = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } else {
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST_ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DATE_ADDED
            )

            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 5000"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            val tracksList = mutableListOf<TrackEntity>()
            val albumMap = mutableMapOf<Long, AlbumEntity>()
            val artistMap = mutableMapOf<Long, ArtistEntity>()

            contentResolver.query(collection, projection, selection, null, sortOrder)?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)

                val totalCount = cursor.count
                var processedCount = 0

                _scanState.value = ScanState.Scanning(0, totalCount)

                while (cursor.moveToNext()) {
                    val filePath = cursor.getString(dataColumn) ?: ""

                    val isBlacklisted = blacklistedFolders.any { folder ->
                        folder.isNotBlank() && filePath.lowercase().contains(folder.lowercase())
                    }
                    if (isBlacklisted) {
                        processedCount++
                        if (processedCount % 10 == 0 || processedCount == totalCount) {
                            _scanState.value = ScanState.Scanning(processedCount, totalCount)
                        }
                        continue
                    }

                    val id = cursor.getLong(idColumn)
                    val title = cursor.getString(titleColumn) ?: "Unknown Track"
                    val artist = cursor.getString(artistColumn) ?: "Unknown Artist"
                    val album = cursor.getString(albumColumn) ?: "Unknown Album"
                    val artistId = cursor.getLong(artistIdColumn)
                    val albumId = cursor.getLong(albumIdColumn)
                    val duration = cursor.getLong(durationColumn)
                    val mimeType = cursor.getString(mimeTypeColumn) ?: "audio/*"
                    val dateAdded = cursor.getLong(dateAddedColumn)

                    val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                    val albumArtUri = ContentUris.withAppendedId(
                        Uri.parse("content://media/external/audio/albumart"),
                        albumId
                    ).toString()

                    // Extract actual audio tags via jaudiotagger
                    val audioFile = File(filePath)
                    val extracted = if (audioFile.exists()) {
                        try { MetadataExtractor.extract(audioFile) } catch (e: Exception) { null }
                    } else null

                    val metadata = extracted?.metadata

                    val codec = metadata?.codec?.takeIf { it.isNotBlank() && it != "UNKNOWN" } ?: when {
                        mimeType.contains("flac", ignoreCase = true) || filePath.endsWith(".flac", ignoreCase = true) -> "FLAC"
                        mimeType.contains("aac", ignoreCase = true) || filePath.endsWith(".aac", ignoreCase = true) -> "AAC"
                        mimeType.contains("m4a", ignoreCase = true) || filePath.endsWith(".m4a", ignoreCase = true) -> "M4A"
                        mimeType.contains("wav", ignoreCase = true) || filePath.endsWith(".wav", ignoreCase = true) -> "WAV"
                        mimeType.contains("ogg", ignoreCase = true) || filePath.endsWith(".ogg", ignoreCase = true) -> "OGG"
                        else -> "MP3"
                    }

                    val sampleRate = if ((metadata?.sampleRate ?: 0) > 0) metadata!!.sampleRate else 44100
                    val bitDepth = if ((metadata?.bitDepth ?: 0) > 0) metadata!!.bitDepth else if (codec == "FLAC" || codec == "WAV") 24 else 16
                    val bitrate = if ((metadata?.bitrateKbps ?: 0) > 0) metadata!!.bitrateKbps else 320
                    val replayGainTrackGain = metadata?.replayGainDb
                    val replayGainTrackPeak = metadata?.replayGainPeak

                    val trackEntity = TrackEntity(
                        id = id,
                        mediaStoreId = id,
                        title = title,
                        artistName = artist,
                        albumName = album,
                        artistId = artistId,
                        albumId = albumId,
                        durationMs = if (duration > 0) duration else (metadata?.durationMs ?: 0L),
                        filePath = filePath,
                        uriString = contentUri.toString(),
                        albumArtUri = albumArtUri,
                        mimeType = mimeType,
                        codec = codec,
                        bitrate = bitrate,
                        sampleRate = sampleRate,
                        bitDepth = bitDepth,
                        replayGainTrackGain = replayGainTrackGain,
                        replayGainTrackPeak = replayGainTrackPeak,
                        dateAdded = dateAdded
                    )

                    tracksList.add(trackEntity)

                    // Track counts for album & artist
                    val existingAlbum = albumMap[albumId]
                    if (existingAlbum == null) {
                        albumMap[albumId] = AlbumEntity(
                            id = albumId,
                            title = album,
                            artistName = artist,
                            trackCount = 1,
                            albumArtUri = albumArtUri
                        )
                    } else {
                        albumMap[albumId] = existingAlbum.copy(trackCount = existingAlbum.trackCount + 1)
                    }

                    val existingArtist = artistMap[artistId]
                    if (existingArtist == null) {
                        artistMap[artistId] = ArtistEntity(
                            id = artistId,
                            name = artist,
                            trackCount = 1,
                            albumCount = 1
                        )
                    } else {
                        artistMap[artistId] = existingArtist.copy(trackCount = existingArtist.trackCount + 1)
                    }

                    processedCount++
                    if (processedCount % 10 == 0 || processedCount == totalCount) {
                        _scanState.value = ScanState.Scanning(processedCount, totalCount)
                    }
                }
            }

            // Transaction / DB Insert
            database.trackDao().deleteAllTracks()
            database.albumDao().deleteAllAlbums()
            database.artistDao().deleteAllArtists()

            database.trackDao().insertTracks(tracksList)
            database.albumDao().insertAlbums(albumMap.values.toList())
            database.artistDao().insertArtists(artistMap.values.toList())

            val finalCount = tracksList.size
            _scanState.value = ScanState.Completed(finalCount)
            finalCount
        } catch (e: Exception) {
            _scanState.value = ScanState.Error(e.message ?: "Failed to scan media library")
            0
        }
    }
}
