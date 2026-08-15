package com.estatia.realestate.apps.core.player_engine.streaming

import android.net.Uri
import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamingUriResolver @Inject constructor(
    private val deviceUtils: IDeviceUtils
) {
    /**
     * Resolves a media URI by appending device-specific codec hints if necessary.
     */
    fun resolve(uri: Uri, forceLegacy: Boolean = false): Uri {
        if (!needsCdnResolution(uri)) return uri

        val builder = uri.buildUpon()
        when {
            forceLegacy -> builder.appendQueryParameter("codec", "baseline")
            deviceUtils.supportsAV1() -> builder.appendQueryParameter("codec", "av1")
            deviceUtils.supportsHEVC() -> builder.appendQueryParameter("codec", "hevc")
            else -> builder.appendQueryParameter("codec", "h264_high")
        }
        return builder.build()
    }

    private fun needsCdnResolution(uri: Uri): Boolean {
        val host = uri.host ?: return false
        return host == "estatia.com" || host.endsWith(".estatia.com")
    }
}
