package com.auraplayer.app.playback

import com.auraplayer.app.data.TrackEntity
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.max

object AutoplayScorer {

    data class ScoringWeights(
        val genreWeight: Float = 0.35f,
        val artistWeight: Float = 0.20f,
        val bpmWeight: Float = 0.25f,
        val moodWeight: Float = 0.20f
    )

    fun calculateScore(
        seedTrack: TrackEntity,
        candidateTrack: TrackEntity,
        currentTimeMs: Long = System.currentTimeMillis(),
        weights: ScoringWeights = ScoringWeights(),
        // artistName -> total scrobble play count (0 if no scrobble data)
        scrobblePlayCounts: Map<String, Int> = emptyMap(),
        maxScrobbleCount: Int = 1
    ): Float {
        if (seedTrack.id == candidateTrack.id) return -1f

        // 1. Genre Match Score (S_genre)
        val genreScore = if (seedTrack.genre.isNotBlank() && seedTrack.genre.equals(candidateTrack.genre, ignoreCase = true)) {
            1.0f
        } else if (seedTrack.genre.isNotBlank() && candidateTrack.genre.isNotBlank() &&
            (seedTrack.genre.contains(candidateTrack.genre, ignoreCase = true) || candidateTrack.genre.contains(seedTrack.genre, ignoreCase = true))) {
            0.5f
        } else {
            0.0f
        }

        // 2. Artist Match Score (S_artist)
        val artistScore = if (seedTrack.artistId == candidateTrack.artistId || seedTrack.artistName.equals(candidateTrack.artistName, ignoreCase = true)) {
            1.0f
        } else {
            0.0f
        }

        // 3. BPM Delta Score (S_bpm)
        val bpmScore = if (seedTrack.bpm > 0 && candidateTrack.bpm > 0) {
            max(0.0f, 1.0f - (abs(seedTrack.bpm - candidateTrack.bpm).toFloat() / 40.0f))
        } else {
            0.5f // Default neutral score when BPM metadata missing
        }

        // 4. Mood Tag Match Score (S_mood) via Jaccard similarity
        val seedMoods = seedTrack.moodTags.lowercase().split(",", " ", ";").filter { it.isNotBlank() }.toSet()
        val candidateMoods = candidateTrack.moodTags.lowercase().split(",", " ", ";").filter { it.isNotBlank() }.toSet()
        val moodScore = if (seedMoods.isNotEmpty() && candidateMoods.isNotEmpty()) {
            val intersection = seedMoods.intersect(candidateMoods).size
            val union = seedMoods.union(candidateMoods).size
            if (union > 0) intersection.toFloat() / union.toFloat() else 0.0f
        } else {
            0.0f
        }

        // Calculate Raw Weighted Match Score
        val rawScore = (weights.genreWeight * genreScore) +
                (weights.artistWeight * artistScore) +
                (weights.bpmWeight * bpmScore) +
                (weights.moodWeight * moodScore)

        // 5. Recency Penalty (P_recency): Reduce score if played within last 24h
        val hoursSinceLastPlayed = if (candidateTrack.lastPlayedTimestamp > 0) {
            max(0.0f, (currentTimeMs - candidateTrack.lastPlayedTimestamp).toFloat() / (1000f * 60f * 60f))
        } else {
            999.0f // Never played
        }

        val recencyPenalty = if (hoursSinceLastPlayed < 24.0f) {
            0.5f / (1.0f + hoursSinceLastPlayed)
        } else {
            0.0f
        }

        // 6. Scrobble Frequency Bonus (B_scrobble): log-normalized so 1 play != 100 plays
        //    Capped at +0.15 so scrobble history nudges but doesn't dominate genre/bpm logic.
        val artistPlayCount = scrobblePlayCounts[candidateTrack.artistName] ?: 0
        val scrobbleBonus = if (artistPlayCount > 0 && maxScrobbleCount > 0) {
            0.15f * (ln(artistPlayCount.toFloat() + 1f) / ln(maxScrobbleCount.toFloat() + 1f))
        } else {
            0.0f
        }

        return max(0.0f, rawScore - recencyPenalty + scrobbleBonus)
    }

    fun selectNextTrack(
        seedTrack: TrackEntity,
        candidates: List<TrackEntity>,
        recentQueueIds: Set<Long> = emptySet(),
        currentTimeMs: Long = System.currentTimeMillis(),
        scrobblePlayCounts: Map<String, Int> = emptyMap()
    ): TrackEntity? {
        val eligibleCandidates = candidates.filter { it.id != seedTrack.id && !recentQueueIds.contains(it.id) }
        if (eligibleCandidates.isEmpty()) return null

        val maxCount = scrobblePlayCounts.values.maxOrNull() ?: 1

        val scoredList = eligibleCandidates.map { candidate ->
            candidate to calculateScore(
                seedTrack, candidate, currentTimeMs,
                scrobblePlayCounts = scrobblePlayCounts,
                maxScrobbleCount = maxCount
            )
        }.sortedByDescending { it.second }

        val topCandidate = scoredList.firstOrNull() ?: return null

        // High or medium score match
        if (topCandidate.second >= 0.35f) {
            return topCandidate.first
        }

        // Fallback 1: Same Genre Match if available
        val sameGenre = eligibleCandidates.filter { it.genre.isNotBlank() && it.genre.equals(seedTrack.genre, ignoreCase = true) }
        if (sameGenre.isNotEmpty()) {
            return sameGenre.shuffled().first()
        }

        // Fallback 2: Non-recently played library track
        return eligibleCandidates.minByOrNull { it.lastPlayedTimestamp }
    }
}

