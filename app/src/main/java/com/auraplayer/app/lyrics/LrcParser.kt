package com.auraplayer.app.lyrics

import java.util.regex.Pattern

object LrcParser {

    private val LINE_TIMESTAMP_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)")
    private val WORD_TIMESTAMP_PATTERN = Pattern.compile("<(\\d{2}):(\\d{2})\\.(\\d{2,3})>([^<]*)")

    fun parse(lrcContent: String): ParsedLyrics {
        if (lrcContent.isBlank()) return ParsedLyrics()

        val lyricLines = mutableListOf<LyricLine>()
        var isWordSynced = false

        lrcContent.lines().forEach { line ->
            val matcher = LINE_TIMESTAMP_PATTERN.matcher(line.trim())
            if (matcher.matches()) {
                val min = matcher.group(1)?.toLongOrNull() ?: 0L
                val sec = matcher.group(2)?.toLongOrNull() ?: 0L
                val fracStr = matcher.group(3) ?: "0"
                val ms = parseFractionalMs(fracStr)
                val lineStartMs = (min * 60 * 1000) + (sec * 1000) + ms

                val remainingText = matcher.group(4) ?: ""

                // Check for word-by-word timestamps <mm:ss.xx>Word
                val wordMatcher = WORD_TIMESTAMP_PATTERN.matcher(remainingText)
                val wordTokens = mutableListOf<WordToken>()

                var cleanLineContent = StringBuilder()

                while (wordMatcher.find()) {
                    isWordSynced = true
                    val wMin = wordMatcher.group(1)?.toLongOrNull() ?: 0L
                    val wSec = wordMatcher.group(2)?.toLongOrNull() ?: 0L
                    val wFrac = wordMatcher.group(3) ?: "0"
                    val wStartMs = (wMin * 60 * 1000) + (wSec * 1000) + parseFractionalMs(wFrac)
                    val wordText = wordMatcher.group(4) ?: ""

                    cleanLineContent.append(wordText)
                    wordTokens.add(
                        WordToken(
                            word = wordText,
                            startMs = wStartMs,
                            endMs = wStartMs + 500L
                        )
                    )
                }

                val finalContent = if (wordTokens.isNotEmpty()) {
                    cleanLineContent.toString()
                } else {
                    remainingText.trim()
                }

                lyricLines.add(
                    LyricLine(
                        startMs = lineStartMs,
                        content = finalContent,
                        wordTokens = wordTokens
                    )
                )
            }
        }

        val sortedLines = lyricLines.sortedBy { it.startMs }
        return ParsedLyrics(
            lines = sortedLines,
            isEnhancedWordSynced = isWordSynced,
            rawContent = lrcContent
        )
    }

    private fun parseFractionalMs(fracStr: String): Long {
        return when (fracStr.length) {
            2 -> fracStr.toLong() * 10
            3 -> fracStr.toLong()
            else -> fracStr.toLongOrNull() ?: 0L
        }
    }
}
