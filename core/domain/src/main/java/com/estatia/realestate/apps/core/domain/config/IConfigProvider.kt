package com.estatia.realestate.apps.core.domain.config

import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import com.estatia.realestate.apps.core.model.api.ApiEndpoint
import com.estatia.realestate.apps.core.model.config.PlayerTuningConfig
import kotlinx.coroutines.flow.StateFlow

interface IConfigProvider : IConfigLifecycle, INetworkConfig, ISecurityConfig, IPlayerTuningConfig {

    override val isReady: StateFlow<Boolean>

    override suspend fun awaitReady()

    override val isInitialized: Boolean

    override suspend fun initialize()

    override suspend fun refresh()

    override val configVersion: StateFlow<Long>
}
