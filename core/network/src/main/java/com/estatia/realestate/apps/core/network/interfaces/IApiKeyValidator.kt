package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.network.utils.ServiceNames
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface IApiKeyValidator {
    fun validate(apiKey: String, service: ServiceNames? = null)
    fun sanitizeForLogging(apiKey: String): String
}
