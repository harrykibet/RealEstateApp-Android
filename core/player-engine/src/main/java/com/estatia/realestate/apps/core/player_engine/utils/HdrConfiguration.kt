package com.estatia.realestate.apps.core.player_engine.utils

import android.graphics.PixelFormat
import android.os.Build
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HdrConfiguration @Inject constructor(
    private val deviceUtils: IDeviceUtils
) {
    sealed class HdrMode(val format: Int, val requiresApi: Int) {
        object HDR10 : HdrMode(
            format = PixelFormat.RGBA_1010102,
            requiresApi = 24
        )

        object DolbyVision : HdrMode(
            format = PixelFormat.RGBA_1010102,
            requiresApi = 29
        )

        object None : HdrMode(PixelFormat.RGB_565, 21)
    }

    fun getBestSupportedMode(): HdrMode {
        return when {
            deviceUtils.supportsDolbyVision() && Build.VERSION.SDK_INT >= 29 -> HdrMode.DolbyVision
            deviceUtils.supports10BitHdr() -> HdrMode.HDR10
            else -> HdrMode.None
        }
    }
}