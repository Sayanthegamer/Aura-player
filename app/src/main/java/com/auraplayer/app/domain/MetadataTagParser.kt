package com.auraplayer.app.domain

object MetadataTagParser {

    data class ParsedMetadata(
        val genre: String = "",
        val bpm: Int = 0,
        val hasEmbeddedArtwork: Boolean = false
    )

    fun parseGenre(rawGenre: String?): String {
        if (rawGenre.isNullOrBlank()) return ""
        val trimmed = rawGenre.trim()
        if (trimmed.equals("Unknown", ignoreCase = true) || trimmed.equals("Other", ignoreCase = true)) {
            return ""
        }
        // Handle ID3 v1 genre numbers like "(13)" -> "Pop" if enclosed in parens, or return raw genre name
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            val genreNum = trimmed.substring(1, trimmed.length - 1).toIntOrNull()
            if (genreNum != null) {
                return getStandardId3GenreName(genreNum) ?: ""
            }
        }
        return trimmed
    }

    fun parseBpm(rawBpm: String?): Int {
        if (rawBpm.isNullOrBlank()) return 0
        // Clean up floats like "120.0" or strings like "120 BPM"
        val numericPart = rawBpm.takeWhile { it.isDigit() || it == '.' }
        val parsedFloat = numericPart.toFloatOrNull() ?: return 0
        return parsedFloat.toInt().coerceIn(0, 300)
    }

    private fun getStandardId3GenreName(code: Int): String? {
        val genres = arrayOf(
            "Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop",
            "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae",
            "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks",
            "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion",
            "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip",
            "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative",
            "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave",
            "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock",
            "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle",
            "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes",
            "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical",
            "Rock & Roll", "Hard Rock"
        )
        return if (code in genres.indices) genres[code] else null
    }
}
