package com.auraplayer.app.repository

import com.auraplayer.app.data.LyricDao
import com.auraplayer.app.data.LyricEntity
import com.auraplayer.app.lyrics.LrcParser
import com.auraplayer.app.lyrics.ParsedLyrics
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LrclibResponse(
    val id: Long? = null,
    val trackName: String? = null,
    val artistName: String? = null,
    val albumName: String? = null,
    val duration: Double? = null,
    val instrumental: Boolean? = false,
    val plainLyrics: String? = null,
    val syncedLyrics: String? = null
)

class LrclibRepository(
    private val lyricDao: LyricDao
) {
    private val httpClient = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getLyrics(
        trackId: String,
        title: String,
        artist: String,
        durationSeconds: Double = 0.0,
        embeddedLyrics: String? = null
    ): ParsedLyrics {
        // 1. Check embedded tags first
        if (!embeddedLyrics.isNullOrBlank()) {
            val parsed = LrcParser.parse(embeddedLyrics)
            if (parsed.lines.isNotEmpty()) {
                return parsed.copy(source = "Embedded Tag")
            }
        }

        // 2. Check local Room DB cache
        val cached = lyricDao.getLyricForTrack(trackId)
        if (cached != null) {
            val lrcText = cached.syncedLyrics ?: cached.plainLyrics ?: ""
            if (lrcText.isNotBlank()) {
                return LrcParser.parse(lrcText).copy(source = "Local Database Cache")
            }
        }

        // 3. Direct query from remote LRCLIB API
        try {
            val response: LrclibResponse = httpClient.get("https://lrclib.net/api/get") {
                parameter("track_name", title)
                parameter("artist_name", artist)
                if (durationSeconds > 0) {
                    parameter("duration", durationSeconds)
                }
            }.body()

            val syncedLrc = response.syncedLyrics
            val plainLrc = response.plainLyrics

            if (syncedLrc != null || plainLrc != null) {
                lyricDao.insertLyric(
                    LyricEntity(
                        trackId = trackId,
                        trackTitle = title,
                        artistName = artist,
                        syncedLyrics = syncedLrc,
                        plainLyrics = plainLrc
                    )
                )
                val targetText = syncedLrc ?: plainLrc ?: ""
                return LrcParser.parse(targetText).copy(source = "LRCLIB API")
            }
        } catch (e: Exception) {
            // Fallback to search endpoint below
        }

        // 4. Search query fallback from LRCLIB API
        return try {
            val searchResults: List<LrclibResponse> = httpClient.get("https://lrclib.net/api/search") {
                parameter("q", "$title $artist")
            }.body()

            val bestMatch = searchResults.firstOrNull { it.syncedLyrics != null || it.plainLyrics != null }
            val syncedLrc = bestMatch?.syncedLyrics
            val plainLrc = bestMatch?.plainLyrics

            if (syncedLrc != null || plainLrc != null) {
                lyricDao.insertLyric(
                    LyricEntity(
                        trackId = trackId,
                        trackTitle = title,
                        artistName = artist,
                        syncedLyrics = syncedLrc,
                        plainLyrics = plainLrc
                    )
                )
                val targetText = syncedLrc ?: plainLrc ?: ""
                LrcParser.parse(targetText).copy(source = "LRCLIB Global Search")
            } else {
                ParsedLyrics(source = "Not Found")
            }
        } catch (e: Exception) {
            ParsedLyrics(source = "Offline")
        }
    }

    suspend fun updateLyricOffset(trackId: String, offsetMs: Long) {
        lyricDao.updateLyricOffset(trackId, offsetMs)
    }
}
