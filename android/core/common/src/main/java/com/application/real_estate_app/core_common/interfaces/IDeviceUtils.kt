package com.application.real_estate_app.core_common.interfaces

import com.application.real_estate_app.core_model.system.DeviceInfo

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