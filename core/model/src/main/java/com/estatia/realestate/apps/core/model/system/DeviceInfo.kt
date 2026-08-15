package com.estatia.realestate.apps.core.model.system

import kotlinx.serialization.Serializable

@Serializable
data class DeviceInfo(
    val os: String,
    val browser: String,
    val deviceType: String,
    val screenResolution: String,
    val appVersion: String
)
