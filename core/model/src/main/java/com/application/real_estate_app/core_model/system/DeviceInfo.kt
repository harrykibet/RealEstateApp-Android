package com.application.real_estate_app.core_model.system

data class DeviceInfo(
    val os: String,
    val browser: String,
    val deviceType: String,
    val screenResolution: String,
    val appVersion: String
)