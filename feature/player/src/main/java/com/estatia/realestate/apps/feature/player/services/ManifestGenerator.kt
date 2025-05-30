package com.estatia.realestate.apps.feature.player.services

import com.estatia.realestate.apps.core.common.interfaces.IDeviceUtils
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