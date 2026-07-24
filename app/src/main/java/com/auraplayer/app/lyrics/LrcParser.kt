package com.auraplayer.app.lyrics

import java.util.regex.Pattern

object LrcParser {

    private val LINE_TIMESTAMP_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)")
    private val WORD_ANGLE_PATTERN = Pattern.compile("<(\\d{2}):(\\d{2})\\.(\\d{2,3})>([^<]*)")
    private val WORD_PAREN_PATTERN = Pattern.compile("\\((\\d{2}):(\\d{2})\\.(\\d{2,3})\\)([^\\)]*)")
    private val WORD_OFFSET_PATTERN = Pattern.compile("<(\\d+)>(.[^<]*)")

    fun parse(lrcContent: String): ParsedLyrics {
        if (lrcContent.isBlank()) return ParsedLyrics()

        val rawLines = mutableListOf<RawLine>()
        var isWordSynced = false

        lrcContent.lines().forEach { rawLine ->
            val matcher = LINE_TIMESTAMP_PATTERN.matcher(rawLine.trim())
            if (matcher.matches()) {
                val min = matcher.group(1)?.toLongOrNull() ?: 0L
                val sec = matcher.group(2)?.toLongOrNull() ?: 0L
                val fracStr = matcher.group(3) ?: "0"
                val ms = parseFractionalMs(fracStr)
                val lineStartMs = (min * 60 * 1000) + (sec * 1000) + ms

                val remainingText = matcher.group(4) ?: ""
                if (remainingText.isNotBlank()) {
                    rawLines.add(RawLine(lineStartMs, remainingText))
                }
            }
        }

        val sortedRaw = rawLines.sortedBy { it.startMs }
        val parsedLines = mutableListOf<LyricLine>()

        var idx = 0
        while (idx < sortedRaw.size) {
            val currentRaw = sortedRaw[idx]
            val lineStartMs = currentRaw.startMs
            val text = currentRaw.text
            val wordTokens = mutableListOf<WordToken>()
            var cleanContent = ""

            // Pattern 1: <mm:ss.xx>Word
            val angleMatcher = WORD_ANGLE_PATTERN.matcher(text)
            if (angleMatcher.find()) {
                isWordSynced = true
                angleMatcher.reset()
                val sb = StringBuilder()
                while (angleMatcher.find()) {
                    val wMin = angleMatcher.group(1)?.toLongOrNull() ?: 0L
                    val wSec = angleMatcher.group(2)?.toLongOrNull() ?: 0L
                    val wFrac = angleMatcher.group(3) ?: "0"
                    val wStartMs = (wMin * 60 * 1000) + (wSec * 1000) + parseFractionalMs(wFrac)
                    val wordText = angleMatcher.group(4) ?: ""
                    sb.append(wordText)
                    wordTokens.add(WordToken(word = wordText, startMs = wStartMs, endMs = wStartMs + 400L))
                }
                cleanContent = sb.toString()
            } else {
                // Pattern 2: (mm:ss.xx)Word
                val parenMatcher = WORD_PAREN_PATTERN.matcher(text)
                if (parenMatcher.find()) {
                    isWordSynced = true
                    parenMatcher.reset()
                    val sb = StringBuilder()
                    while (parenMatcher.find()) {
                        val wMin = parenMatcher.group(1)?.toLongOrNull() ?: 0L
                        val wSec = parenMatcher.group(2)?.toLongOrNull() ?: 0L
                        val wFrac = parenMatcher.group(3) ?: "0"
                        val wStartMs = (wMin * 60 * 1000) + (wSec * 1000) + parseFractionalMs(wFrac)
                        val wordText = parenMatcher.group(4) ?: ""
                        sb.append(wordText)
                        wordTokens.add(WordToken(word = wordText, startMs = wStartMs, endMs = wStartMs + 400L))
                    }
                    cleanContent = sb.toString()
                } else {
                    // Pattern 3: <ms_offset>Word
                    val offsetMatcher = WORD_OFFSET_PATTERN.matcher(text)
                    if (offsetMatcher.find()) {
                        isWordSynced = true
                        offsetMatcher.reset()
                        val sb = StringBuilder()
                        while (offsetMatcher.find()) {
                            val offsetMs = offsetMatcher.group(1)?.toLongOrNull() ?: 0L
                            val wordText = offsetMatcher.group(2) ?: ""
                            val wStartMs = lineStartMs + offsetMs
                            sb.append(wordText)
                            wordTokens.add(WordToken(word = wordText, startMs = wStartMs, endMs = wStartMs + 400L))
                        }
                        cleanContent = sb.toString()
                    }
                }
            }

            if (wordTokens.isEmpty()) {
                cleanContent = text.trim()
            } else {
                // Compute precise endMs for word tokens
                for (wIdx in 0 until wordTokens.size - 1) {
                    val currentWord = wordTokens[wIdx]
                    val nextWord = wordTokens[wIdx + 1]
                    wordTokens[wIdx] = currentWord.copy(endMs = nextWord.startMs)
                }
            }

            // Check if next raw line has identical timestamp (Romanization / Translation line)
            var romanization: String? = null
            var translation: String? = null

            if (idx + 1 < sortedRaw.size && Math.abs(sortedRaw[idx + 1].startMs - lineStartMs) <= 150) {
                val nextText = sortedRaw[idx + 1].text.trim()
                if (isLatinText(nextText) && !isLatinText(cleanContent)) {
                    romanization = nextText
                } else {
                    translation = nextText
                }
                idx++
            }

            parsedLines.add(
                LyricLine(
                    startMs = lineStartMs,
                    content = cleanContent,
                    translation = translation,
                    romanization = romanization,
                    wordTokens = wordTokens
                )
            )
            idx++
        }

        return ParsedLyrics(
            lines = parsedLines,
            isEnhancedWordSynced = isWordSynced,
            rawContent = lrcContent
        )
    }

    private fun isLatinText(text: String): Boolean {
        return text.all { it.code in 0..0x024F || it.isWhitespace() || !it.isLetter() }
    }

    private fun parseFractionalMs(fracStr: String): Long {
        return when (fracStr.length) {
            2 -> fracStr.toLong() * 10
            3 -> fracStr.toLong()
            else -> fracStr.toLongOrNull() ?: 0L
        }
    }

    private data class RawLine(val startMs: Long, val text: String)
}
