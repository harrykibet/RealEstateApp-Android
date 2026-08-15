package com.estatia.realestate.apps.core.config.parser

import com.estatia.realestate.apps.core.model.config.RemoteConfigModel
import kotlinx.serialization.json.Json

/**
 * Robust config parser using Kotlin Serialization.
 * Automatically maps nested JSON fragments to Typed Kotlin objects.
 */
class ConfigParser {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true // Handle nulls in JSON for non-nullable Kotlin properties
        encodeDefaults = true
    }

    /**
     * Parses the raw configuration JSON string into a [RemoteConfigModel].
     * @param jsonString The raw JSON from backend or assets.
     */
    fun parse(jsonString: String): RemoteConfigModel {
        return json.decodeFromString<RemoteConfigModel>(jsonString)
    }
}
