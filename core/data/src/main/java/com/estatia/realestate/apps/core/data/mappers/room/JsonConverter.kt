package com.estatia.realestate.apps.core.data.mappers.room

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Internal utility for serializing/deserializing Room entity fields.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Map complex objects to/from JSON strings for SQLite persistence.
 * - Concurrency: Stateless and thread-safe.
 * - Resilience: Surfaces an empty list as a fallback for malformed JSON.
 */
internal object JsonConverter {
    private val json = Json { ignoreUnknownKeys = true }

    // Convert a list of strings to JSON
    fun toJson(list: List<String>): String = json.encodeToString(list)

    // Convert a JSON string to a list of strings
    fun fromJson(jsonStr: String): List<String> =
        try {
            json.decodeFromString<List<String>>(jsonStr)
        } catch (e: Exception) {
            emptyList()
        }
}
