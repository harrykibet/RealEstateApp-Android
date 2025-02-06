package com.application.real_estate_app.feature_mediaplayer.streaming

import android.graphics.PixelFormat
import android.os.Build
import com.application.real_estate_app.core.domain.interfaces.IDeviceUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HdrConfiguration @Inject constructor(
    private val deviceUtils: IDeviceUtils
) {
    sealed class HdrMode(val format: Int, val requiresApi: Int) {
        object HDR10 : HdrMode(
            format = if (Build.VERSION.SDK_INT >= 26) PixelFormat.RGBA_1010102 else 8,
            requiresApi = 24
        )

        object DolbyVision : HdrMode(
            format = if (Build.VERSION.SDK_INT >= 26) PixelFormat.RGBA_1010102 else 8,
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