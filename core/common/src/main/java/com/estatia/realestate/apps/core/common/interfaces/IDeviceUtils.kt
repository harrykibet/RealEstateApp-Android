package com.estatia.realestate.apps.core.common.interfaces

import com.estatia.realestate.apps.core.model.system.DeviceInfo

interface IDeviceUtils {
    fun getDeviceInfo(): DeviceInfo
    fun supportsAV1(): Boolean
    fun supportsHEVC(): Boolean
    fun supports10BitHdr(): Boolean
    fun supportsDolbyVision(): Boolean
    fun getMaxSupportedBitrate(): Long
    fun getOptimalVideoResolution(): Pair<Int, Int>
    fun isHighEndDevice(): Boolean
    fun isMidRangeDevice(): Boolean
    fun isLowRamDevice(): Boolean
    fun getRefreshRate(): Float
    fun getAvailableMemoryMB(): Long
    fun getAvailableStorageMB(): Long
    fun getMaxSupportedVideoDecoders(): Int

    /**
     * Resolves a stable string identifier for the device's video playback capability.
     */
    fun getVideoQualityHint(): String
}
