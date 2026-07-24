package com.auraplayer.app.lyrics

import java.util.regex.Pattern

object LrcParser {

    private val LINE_TIMESTAMP_PATTERN = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2,3})\\](.*)")
    private val WORD_ANGLE_PATTERN = Pattern.compile("<(\\d{2}):(\\d{2})\\.(\\d{2,3})>([^<]*)")
    private val WORD_PAREN_PATTERN = Pattern.compile("\\((\\d{2}):(\\d{2})\\.(\\d{2,3})\\)([^\\)]*)")
    private val WORD_OFFSET_PATTERN = Pattern.compile("<(\\d+)>(.[^<]*)")

    private val TTML_P_PATTERN = Pattern.compile("<p\\s+[^>]*begin=\"([^\"]+)\"[^>]*>(.*?)</p>", Pattern.DOTALL or Pattern.CASE_INSENSITIVE)
    private val TTML_SPAN_PATTERN = Pattern.compile("<span\\s+[^>]*begin=\"([^\"]+)\"[^>]*end=\"([^\"]+)\"[^>]*>(.*?)</span>", Pattern.DOTALL or Pattern.CASE_INSENSITIVE)

    fun parse(lrcContent: String): ParsedLyrics {
        if (lrcContent.isBlank()) return ParsedLyrics()

        val trimmed = lrcContent.trim()
        if (trimmed.contains("<ttml") || trimmed.contains("<p begin=") || trimmed.contains("xmlns=\"http://www.w3.org/ns/ttml\"")) {
            return parseTtml(trimmed)
        }

        if (trimmed.startsWith("{") && trimmed.contains("\"lines\"")) {
            return parseJsonLyrics(trimmed)
        }

        return parseStandardLrc(lrcContent)
    }

    private fun parseStandardLrc(lrcContent: String): ParsedLyrics {
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
                for (wIdx in 0 until wordTokens.size - 1) {
                    val currentWord = wordTokens[wIdx]
                    val nextWord = wordTokens[wIdx + 1]
                    wordTokens[wIdx] = currentWord.copy(endMs = nextWord.startMs)
                }
            }

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

    private fun parseTtml(ttmlContent: String): ParsedLyrics {
        val lines = mutableListOf<LyricLine>()
        val pMatcher = TTML_P_PATTERN.matcher(ttmlContent)

        while (pMatcher.find()) {
            val beginStr = pMatcher.group(1) ?: "0"
            val lineStartMs = parseTimeStringToMs(beginStr)
            val bodyText = pMatcher.group(2) ?: ""

            val wordTokens = mutableListOf<WordToken>()
            val spanMatcher = TTML_SPAN_PATTERN.matcher(bodyText)
            val sb = StringBuilder()

            while (spanMatcher.find()) {
                val wBeginStr = spanMatcher.group(1) ?: "0"
                val wEndStr = spanMatcher.group(2) ?: "0"
                val rawWord = spanMatcher.group(3)?.replace("<[^>]*>".toRegex(), "") ?: ""

                val wStartMs = parseTimeStringToMs(wBeginStr)
                val wEndMs = parseTimeStringToMs(wEndStr)

                sb.append(rawWord)
                wordTokens.add(WordToken(word = rawWord.trim(), startMs = wStartMs, endMs = wEndMs))
            }

            val cleanContent = if (wordTokens.isNotEmpty()) sb.toString().trim() else bodyText.replace("<[^>]*>".toRegex(), "").trim()

            if (cleanContent.isNotBlank()) {
                lines.add(LyricLine(startMs = lineStartMs, content = cleanContent, wordTokens = wordTokens))
            }
        }

        return ParsedLyrics(
            lines = lines.sortedBy { it.startMs },
            isEnhancedWordSynced = lines.any { it.wordTokens.isNotEmpty() },
            rawContent = ttmlContent,
            source = "Apple Music TTML"
        )
    }

    private fun parseJsonLyrics(jsonContent: String): ParsedLyrics {
        val lines = mutableListOf<LyricLine>()

        val lineRegex = Pattern.compile("\"startTimeMs\"\\s*:\\s*\"?(\\d+)\"?,?\\s*\"words\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL)
        val wordRegex = Pattern.compile("\"word\"\\s*:\\s*\"([^\"]+)\",?\\s*\"offsetMs\"\\s*:\\s*(\\d+)")

        val lineMatcher = lineRegex.matcher(jsonContent)
        while (lineMatcher.find()) {
            val lineStartMs = lineMatcher.group(1)?.toLongOrNull() ?: 0L
            val wordsBlock = lineMatcher.group(2) ?: ""

            val wordTokens = mutableListOf<WordToken>()
            val wordMatcher = wordRegex.matcher(wordsBlock)
            val sb = StringBuilder()

            while (wordMatcher.find()) {
                val wordStr = wordMatcher.group(1) ?: ""
                val offsetMs = wordMatcher.group(2)?.toLongOrNull() ?: 0L
                val wStartMs = lineStartMs + offsetMs

                sb.append(wordStr).append(" ")
                wordTokens.add(WordToken(word = wordStr, startMs = wStartMs, endMs = wStartMs + 350L))
            }

            val cleanText = sb.toString().trim()
            if (cleanText.isNotBlank()) {
                lines.add(LyricLine(startMs = lineStartMs, content = cleanText, wordTokens = wordTokens))
            }
        }

        return ParsedLyrics(
            lines = lines.sortedBy { it.startMs },
            isEnhancedWordSynced = lines.any { it.wordTokens.isNotEmpty() },
            rawContent = jsonContent,
            source = "Spotify/Musixmatch Syllables"
        )
    }

    private fun parseTimeStringToMs(timeStr: String): Long {
        val clean = timeStr.replace("s", "").trim()
        if (clean.contains(":")) {
            val parts = clean.split(":")
            if (parts.size >= 2) {
                val min = parts[0].toLongOrNull() ?: 0L
                val secParts = parts[1].split(".")
                val sec = secParts[0].toLongOrNull() ?: 0L
                val ms = if (secParts.size > 1) parseFractionalMs(secParts[1]) else 0L
                return (min * 60 * 1000) + (sec * 1000) + ms
            }
        }
        val secDouble = clean.toDoubleOrNull() ?: 0.0
        return (secDouble * 1000).toLong()
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
