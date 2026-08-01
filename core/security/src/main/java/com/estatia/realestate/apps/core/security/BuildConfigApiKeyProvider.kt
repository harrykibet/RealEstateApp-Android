package com.estatia.realestate.apps.core.security

import com.estatia.realestate.apps.core.security.interfaces.ApiKeyProvider
import javax.inject.Inject

class BuildConfigApiKeyProvider @Inject constructor() : ApiKeyProvider {

    override val mapsApiKey: String
        get() = BuildConfig.MAPS_API_KEY
}
