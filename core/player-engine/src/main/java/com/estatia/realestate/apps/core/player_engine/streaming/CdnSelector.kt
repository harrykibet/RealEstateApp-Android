package com.estatia.realestate.apps.core.player_engine.streaming

import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CdnSelector @Inject constructor(
    private val policy: ICdnPolicy,
    private val healthMonitor: CdnHealthMonitor,
    private val config: IConfigProvider
) {

    suspend fun select(): CdnEndpoint {

        val endpoints = config.cdnEndpoints

        require(endpoints.isNotEmpty()) {
            "No CDN endpoints configured"
        }

        return policy.select(endpoints, healthMonitor)
    }
}
