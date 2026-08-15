package com.estatia.realestate.apps.core.data.mappers.room

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

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
