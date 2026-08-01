package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.utils.ServiceNames

interface IApiKeyValidator {
    fun validate(apiKey: String, service: ServiceNames? = null)
    fun sanitizeForLogging(apiKey: String): String
}
