package com.application.real_estate_app.feature_mediaplayer.services

import com.application.real_estate_app.core.utils.system.DeviceUtils
import javax.inject.Inject

// Dynamic manifest adjustments
class ManifestGenerator @Inject constructor(
    private val deviceUtils: DeviceUtils
) {
    fun filterManifest(originalManifest: String): String {
        return if (deviceUtils.supportsAV1()) {
            originalManifest.replace("codecs=\"avc1", "codecs=\"av01")
        } else {
            originalManifest
        }
    }
}