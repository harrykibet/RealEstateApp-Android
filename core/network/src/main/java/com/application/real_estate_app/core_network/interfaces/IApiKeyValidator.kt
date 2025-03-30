package com.application.real_estate_app.core_network.interfaces

import com.application.real_estate_app.core_network.utils.ServiceNames

interface IApiKeyValidator {
    fun validate(apiKey: String, service: ServiceNames? = null)
    fun sanitizeForLogging(apiKey: String): String
}