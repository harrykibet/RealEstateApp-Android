package com.estatia.realestate.apps.core.player_engine.utils

import com.estatia.realestate.apps.core.architecture.annotations.Utility

import android.graphics.PixelFormat
import android.os.Build
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Utility
class HdrConfiguration @Inject constructor(
    private val deviceUtils: IDeviceUtils
) {
@Utility
    sealed class HdrMode(val format: Int) {
@Utility
        object HDR10 : HdrMode(PixelFormat.RGBA_1010102)
@Utility
        object DolbyVision : HdrMode(PixelFormat.RGBA_1010102)
@Utility
        object None : HdrMode(PixelFormat.RGB_565)
    }

    fun getBestSupportedMode(thermalStatus: Int = 0): HdrMode {
        // 🌡️ Hardening: Suppress HDR if device is overheating (SEVERE or higher)
        // HDR rendering puts significant stress on the GPU, exacerbating thermal issues.
        if (thermalStatus >= 3) {
            return HdrMode.None
        }

        return when {
            deviceUtils.supportsDolbyVision() && Build.VERSION.SDK_INT >= 29 -> HdrMode.DolbyVision
            deviceUtils.supports10BitHdr() -> HdrMode.HDR10
            else -> HdrMode.None
        }
    }
}
