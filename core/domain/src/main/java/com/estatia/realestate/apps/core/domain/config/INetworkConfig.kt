package com.estatia.realestate.apps.core.domain.config

import com.estatia.realestate.apps.core.model.api.ApiEndpoint
import com.estatia.realestate.apps.core.model.cdn.CdnEndpoint

interface INetworkConfig : IConfigLifecycle {
    val baseUrl: String
    val apiEndpoints: List<ApiEndpoint>
    val cdnEndpoints: List<CdnEndpoint>
}
