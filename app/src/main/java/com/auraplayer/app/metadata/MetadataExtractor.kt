package com.auraplayer.app.metadata

import com.auraplayer.app.playback.TrackMetadata
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import kotlin.math.min
import kotlin.math.pow

object MetadataExtractor {

    data class AudioMetadataResult(
        val metadata: TrackMetadata,
        val embeddedLyrics: String? = null,
        val isSyncedLyric: Boolean = false
    )

    fun extract(audioFile: File): AudioMetadataResult {
        if (!audioFile.exists()) {
            return AudioMetadataResult(TrackMetadata(id = audioFile.name, title = audioFile.nameWithoutExtension))
        }

        return try {
            val audioFileObj = AudioFileIO.read(audioFile)
            val header = audioFileObj.audioHeader
            val tag = audioFileObj.tag

            val title = tag?.getFirst(FieldKey.TITLE)?.ifBlank { null } ?: audioFile.nameWithoutExtension
            val artist = tag?.getFirst(FieldKey.ARTIST)?.ifBlank { null } ?: "Unknown Artist"
            val album = tag?.getFirst(FieldKey.ALBUM)?.ifBlank { null } ?: "Unknown Album"

            val sampleRate = header.sampleRateAsNumber
            val bitrate = header.bitRateAsNumber.toInt()
            val format = header.format.uppercase()
            val bitDepth = try {
                header.bitsPerSample
            } catch (e: Exception) {
                if (format.contains("FLAC")) 24 else 16
            }

            val gainString = tag?.getFirst("REPLAYGAIN_TRACK_GAIN")?.ifBlank { null }
                ?: tag?.getFirst("REPLAYGAIN_ALBUM_GAIN")?.ifBlank { null }
            val peakString = tag?.getFirst("REPLAYGAIN_TRACK_PEAK")?.ifBlank { null }
                ?: tag?.getFirst("REPLAYGAIN_ALBUM_PEAK")?.ifBlank { null }

            val replayGainDb = parseReplayGainDb(gainString)
            val replayGainPeak = parseReplayGainPeak(peakString)

            val unsyncedLyrics = tag?.getFirst(FieldKey.LYRICS)?.ifBlank { null }

            AudioMetadataResult(
                metadata = TrackMetadata(
                    id = audioFile.absolutePath,
                    title = title,
                    artist = artist,
                    album = album,
                    durationMs = (header.trackLength * 1000).toLong(),
                    codec = format,
                    sampleRate = sampleRate,
                    bitDepth = bitDepth,
                    bitrateKbps = bitrate,
                    replayGainDb = replayGainDb,
                    replayGainPeak = replayGainPeak
                ),
                embeddedLyrics = unsyncedLyrics,
                isSyncedLyric = false
            )
        } catch (e: Exception) {
            AudioMetadataResult(
                metadata = TrackMetadata(
                    id = audioFile.absolutePath,
                    title = audioFile.nameWithoutExtension
                )
            )
        }
    }

    fun calculateReplayGainScale(gainDb: Float?, peak: Float?): Float {
        if (gainDb == null) return 1.0f

        val linearScale = 10f.pow(gainDb / 20f)
        val maxSafeScale = if (peak != null && peak > 0f) (1.0f / peak) else 1.0f

        return min(linearScale, maxSafeScale)
    }

    private fun parseReplayGainDb(gainStr: String?): Float? {
        if (gainStr == null) return null
        return try {
            val cleaned = gainStr.replace("dB", "", ignoreCase = true).trim()
            cleaned.toFloat()
        } catch (e: Exception) {
            null
        }
    }

    private fun parseReplayGainPeak(peakStr: String?): Float? {
        if (peakStr == null) return null
        return try {
            peakStr.trim().toFloat()
        } catch (e: Exception) {
            null
        }
    }
}
