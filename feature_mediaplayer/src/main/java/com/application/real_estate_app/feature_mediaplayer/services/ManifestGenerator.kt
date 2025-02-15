package com.application.real_estate_app.feature_mediaplayer.services

import com.application.real_estate_app.core.domain.interfaces.IDeviceUtils
import javax.inject.Inject

// Dynamic manifest adjustments
@Suppress("Unused")
class ManifestGenerator @Inject constructor(
    private val deviceUtils: IDeviceUtils
) {
    fun filterManifest(originalManifest: String): String {
        return if (deviceUtils.supportsAV1()) {
            originalManifest.replace("codecs=\"avc1", "codecs=\"av01")
        } else {
            originalManifest
        }
    }
}