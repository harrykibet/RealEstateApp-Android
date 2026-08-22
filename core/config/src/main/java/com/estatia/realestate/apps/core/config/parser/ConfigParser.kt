package com.estatia.realestate.apps.core.config.parser

import com.estatia.realestate.apps.core.model.config.ChaosConfig
import com.estatia.realestate.apps.core.model.config.NetworkConfigModel
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import com.estatia.realestate.apps.core.model.config.RemoteConfigModel
import com.estatia.realestate.apps.core.model.config.SecurityConfigModel
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

    fun parseNetwork(jsonString: String): NetworkConfigModel = json.decodeFromString(jsonString)
    fun parseSecurity(jsonString: String): SecurityConfigModel = json.decodeFromString(jsonString)
    fun parsePlayer(jsonString: String): PlayerTuningConfig = 
        json.decodeFromString<PlayerConfigFragment>(jsonString).playerTuning
    fun parseChaos(jsonString: String): ChaosConfig = 
        json.decodeFromString<ChaosConfigFragment>(jsonString).chaosConfig

    // Helper wrappers to match the JSON structure in assets
    @kotlinx.serialization.Serializable
    private data class PlayerConfigFragment(
        @kotlinx.serialization.SerialName("player_tuning") val playerTuning: PlayerTuningConfig
    )

    @kotlinx.serialization.Serializable
    private data class ChaosConfigFragment(
        @kotlinx.serialization.SerialName("chaos_config") val chaosConfig: ChaosConfig
    )
}
