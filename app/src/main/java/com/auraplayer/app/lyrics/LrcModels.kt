package com.auraplayer.app.lyrics

data class WordToken(
    val word: String,
    val startMs: Long,
    val endMs: Long
)

data class LyricLine(
    val startMs: Long,
    val content: String,
    val translation: String? = null,
    val romanization: String? = null,
    val wordTokens: List<WordToken> = emptyList()
)

data class ParsedLyrics(
    val lines: List<LyricLine> = emptyList(),
    val isEnhancedWordSynced: Boolean = false,
    val rawContent: String = "",
    val source: String = "Auto"
)
