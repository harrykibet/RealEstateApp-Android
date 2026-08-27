package com.estatia.realestate.apps.core.model.config

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.model.api.ApiEndpoint
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteConfigModel(
    val network: NetworkConfigModel,
    val security: SecurityConfigModel,
    val player: PlayerTuningConfig = PlayerTuningConfig()
)

@Serializable
data class NetworkConfigModel(
    @SerialName("base_url")
    val baseUrl: String,
    @SerialName("api_endpoints")
    val apiEndpoints: List<ApiEndpoint> = emptyList(),
    @SerialName("cdn_endpoints")
    val cdnEndpoints: List<CdnEndpoint> = emptyList(),
    @SerialName("enable_http_logging")
    val enableHttpLogging: Boolean = false
)

@Serializable
data class SecurityConfigModel(
    @SerialName("key_patterns")
    val keyPatterns: KeyPatterns,
    @SerialName("encryption_keys")
    val encryptionKeys: EncryptionKeys,
    @SerialName("enable_logging")
    val enableLogging: Boolean,
    @SerialName("enable_telemetry")
    val enableTelemetry: Boolean = false
)

@Serializable
data class KeyPatterns(
    val google: String,
    val generic: String,
    val payments: String
)

@Serializable
data class EncryptionKeys(
    @SerialName("location_id")
    val locationId: String,
    @SerialName("key_ring_id")
    val keyRingId: String,
    @SerialName("symmetric_key_id")
    val symmetricKeyId: String,
    @SerialName("asymmetric_key_id")
    val asymmetricKeyId: String,
    @SerialName("asymmetric_signing_key_id")
    val asymmetricSigningKeyId: String
)

@Serializable
data class PlayerTuningConfig(
    @SerialName("dwell_time_debounce_ms")
    val dwellTimeDebounceMs: Long = 100,
    @SerialName("jank_aware_debounce_ms")
    val jankAwareDebounceMs: Long = 400,
    @SerialName("fling_debounce_ms")
    val flingDebounceMs: Long = 250,
    @SerialName("fast_scroll_threshold_ms")
    val fastScrollThresholdMs: Long = 300,
    @SerialName("fling_count_threshold")
    val flingCountThreshold: Int = 3,
    @SerialName("max_warmed_media")
    val maxWarmedMedia: Int = 8,
    @SerialName("watchdog_timeout_ms")
    val watchdogTimeoutMs: Long = 3500,
    @SerialName("low_buffer_threshold_s")
    val lowBufferThresholdS: Double = 2.0,
    @SerialName("precautionary_buffer_threshold_s")
    val precautionaryBufferThresholdS: Double = 5.0,
    @SerialName("low_buffer_penalty")
    val lowBufferPenalty: Double = 0.5,
    @SerialName("precautionary_buffer_penalty")
    val precautionaryBufferPenalty: Double = 0.8,
    @SerialName("low_ram_memory_threshold_mb")
    val lowRamMemoryThresholdMb: Long = 150,
    @SerialName("default_cache_bytes")
    val defaultCacheBytes: Long = 512L * 1024 * 1024,
    @SerialName("min_cache_bytes")
    val minCacheBytes: Long = 128L * 1024 * 1024,
    @SerialName("storage_budget_percent")
    val storageBudgetPercent: Double = 0.1,
    @SerialName("min_buffer_live_ms")
    val minBufferLiveMs: Int = 800,
    @SerialName("max_buffer_live_ms")
    val maxBufferLiveMs: Int = 2500,
    @SerialName("buffer_for_playback_live_ms")
    val bufferForPlaybackLiveMs: Int = 400,
    @SerialName("buffer_for_playback_after_rebuffer_live_ms")
    val bufferForPlaybackAfterRebufferLiveMs: Int = 800,
    @SerialName("min_buffer_vod_ms")
    val minBufferVodMs: Int = 800,
    @SerialName("max_buffer_vod_ms")
    val maxBufferVodMs: Int = 3000,
    @SerialName("buffer_for_playback_vod_ms")
    val bufferForPlaybackVodMs: Int = 250,
    @SerialName("buffer_for_playback_after_rebuffer_vod_ms")
    val bufferForPlaybackAfterRebufferVodMs: Int = 800,
    @SerialName("live_buffer_multiplier")
    val liveBufferMultiplier: Double = 0.6,
    @SerialName("vod_buffer_multiplier")
    val vodBufferMultiplier: Double = 0.7
)

