package com.application.real_estate_app.core.domain.interfaces

import com.application.real_estate_app.core.domain.models.DeviceInfo

interface IDeviceUtils {
    fun getDeviceInfo(): DeviceInfo
    fun supportsAV1(): Boolean
    fun supports10BitHdr(): Boolean
    fun supportsDolbyVision(): Boolean
    fun getMaxSupportedBitrate(): Long
    fun getOptimalVideoResolution(): Pair<Int, Int>
    fun isHighEndDevice(): Boolean
    fun isMidRangeDevice(): Boolean
    fun isLowRamDevice(): Boolean
    fun getRefreshRate(): Float
    fun getAvailableMemoryMB(): Long
}