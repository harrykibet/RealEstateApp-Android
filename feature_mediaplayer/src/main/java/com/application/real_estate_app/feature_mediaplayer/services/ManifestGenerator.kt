package com.application.real_estate_app.feature_mediaplayer.services

import javax.inject.Inject

// Dynamic manifest adjustments
class ManifestGenerator @Inject constructor(
    private val deviceCapabilityChecker: DeviceCapabilityChecker
) {
    fun filterManifest(originalManifest: String): String {
        return if (deviceCapabilityChecker.supportsAV1()) {
            originalManifest.replace("codecs=\"avc1", "codecs=\"av01")
        } else {
            originalManifest
        }
    }
}