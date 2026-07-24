package com.auraplayer.app.lyrics

data class WordToken(
    val word: String,
    val startMs: Long,
    val endMs: Long
)

data class LyricLine(
    val startMs: Long,
    val content: String,
    val wordTokens: List<WordToken> = emptyList()
)

data class ParsedLyrics(
    val lines: List<LyricLine> = emptyList(),
    val isEnhancedWordSynced: Boolean = false,
    val rawContent: String = ""
)
