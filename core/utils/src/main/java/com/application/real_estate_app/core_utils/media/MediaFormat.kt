package com.application.real_estate_app.core_utils.media

/**
 * Enum defining supported media formats with their MIME types.
 */
enum class MediaFormat(val extension: String, val mimeType: String) {

    // ───────────────────────────────────────────────────────────────────
    // Image Formats
    // ───────────────────────────────────────────────────────────────────
    JPEG("jpg", "image/jpeg"),
    PNG("png", "image/png"),
    WEBP("webp", "image/webp"),
    HEIC("heic", "image/heic"),
    AVIF("avif", "image/avif"),

    // ───────────────────────────────────────────────────────────────────
    // Video Formats
    // ───────────────────────────────────────────────────────────────────
    MP4("mp4", "video/mp4"),
    MKV("mkv", "video/x-matroska"),
    AVI("avi", "video/x-msvideo"),
    MOV("mov", "video/quicktime"),
    FLV("flv", "video/x-flv"),
    WEBM("webm", "video/webm"),

    // ───────────────────────────────────────────────────────────────────
    // Audio Formats
    // ───────────────────────────────────────────────────────────────────
    MP3("mp3", "audio/mpeg"),
    WAV("wav", "audio/wav"),
    FLAC("flac", "audio/flac"),
    AAC("aac", "audio/aac"),
    OGG("ogg", "audio/ogg");

    companion object {
        // Fast lookup maps for extensions and MIME types
        private val extensionMap = entries.associateBy { it.extension }
        private val mimeTypeMap = entries.associateBy { it.mimeType }

        // Sets for grouping media types
        val IMAGE_FORMATS = setOf(JPEG, PNG, WEBP, HEIC, AVIF)
        val VIDEO_FORMATS = setOf(MP4, MKV, AVI, MOV, FLV, WEBM)
        val AUDIO_FORMATS = setOf(MP3, WAV, FLAC, AAC, OGG)

        /**
         * Finds a `MediaFormat` by file extension.
         */
        fun fromExtension(ext: String): MediaFormat? = extensionMap[ext.lowercase()]

        /**
         * Finds a `MediaFormat` by MIME type.
         */
        fun fromMimeType(mimeType: String): MediaFormat? = mimeTypeMap[mimeType.lowercase()]

        /**
         * Checks if a given extension is a supported media format.
         */
        fun isSupported(extension: String): Boolean = extensionMap.containsKey(extension.lowercase())

        /**
         * Checks if a given MIME type is a supported media format.
         */
        fun isSupportedMimeType(mimeType: String): Boolean = mimeTypeMap.containsKey(mimeType.lowercase())
    }
}
