package com.estatia.realestate.apps.core.model.config

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.model.api.ApiEndpoint

data class RemoteConfigModel(
    val keyPatterns: KeyPatterns,
    val encryptionKeys: EncryptionKeys,
    val cdnEndpoints: List<CdnEndpoint>,
    val apiEndpoints: List<ApiEndpoint> = emptyList(),
    val baseConfig: BaseConfig,
    val chaosConfig: ChaosConfig = ChaosConfig(),
    val playerTuning: PlayerTuningConfig = PlayerTuningConfig()
)

data class KeyPatterns(
    val google: String,
    val generic: String,
    val payments: String
)

data class EncryptionKeys(
    val locationId: String,
    val keyRingId: String,
    val symmetricKeyId: String,
    val asymmetricKeyId: String,
    val asymmetricSigningKeyId: String
)

data class BaseConfig(
    val baseUrl: String,
    val enableLogging: Boolean,
    val enableTelemetry: Boolean = false
)

data class ChaosConfig(
    val simulateStall: Boolean = false,
    val stallDurationMs: Long = 0,
    val failureRate: Float = 0f,
    val throttleBps: Long = 0
)

data class PlayerTuningConfig(
    val dwellTimeDebounceMs: Long = 100,
    val jankAwareDebounceMs: Long = 400,
    val flingDebounceMs: Long = 250,
    val fastScrollThresholdMs: Long = 300,
    val flingCountThreshold: Int = 3,
    val maxWarmedMedia: Int = 8,
    val watchdogTimeoutMs: Long = 7000,
    val lowBufferThresholdS: Double = 2.0,
    val precautionaryBufferThresholdS: Double = 5.0,
    val lowBufferPenalty: Double = 0.5,
    val precautionaryBufferPenalty: Double = 0.8,
    val lowRamMemoryThresholdMb: Long = 150,
    val defaultCacheBytes: Long = 512L * 1024 * 1024,
    val minCacheBytes: Long = 128L * 1024 * 1024,
    val storageBudgetPercent: Double = 0.1,
    val minBufferLiveMs: Int = 800,
    val maxBufferLiveMs: Int = 2500,
    val bufferForPlaybackLiveMs: Int = 400,
    val bufferForPlaybackAfterRebufferLiveMs: Int = 800,
    val minBufferVodMs: Int = 800,
    val maxBufferVodMs: Int = 3000,
    val bufferForPlaybackVodMs: Int = 250,
    val bufferForPlaybackAfterRebufferVodMs: Int = 800,
    val liveBufferMultiplier: Double = 0.6,
    val vodBufferMultiplier: Double = 0.7
)
