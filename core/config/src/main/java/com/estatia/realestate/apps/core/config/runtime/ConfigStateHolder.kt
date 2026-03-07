package com.estatia.realestate.apps.core.config.runtime

import com.estatia.realestate.apps.core.config.model.RemoteConfigModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicLong

class ConfigStateHolder {

    private val _config = MutableStateFlow<RemoteConfigModel?>(null)

    val config: StateFlow<RemoteConfigModel?> = _config

    private val version = AtomicLong(0)

    private val _configVersion = MutableStateFlow(0L)

    val configVersion: StateFlow<Long> = _configVersion

    fun update(config: RemoteConfigModel) {

        _config.value = config

        val newVersion = version.incrementAndGet()

        _configVersion.value = newVersion
    }
}