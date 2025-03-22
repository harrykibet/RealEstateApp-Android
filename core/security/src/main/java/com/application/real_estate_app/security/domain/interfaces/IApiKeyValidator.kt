package com.application.real_estate_app.security.domain.interfaces

import com.application.real_estate_app.security.utils.extensions.ServiceNames

interface IApiKeyValidator {
    fun validate(apiKey: String, service: ServiceNames? = null)
    fun sanitizeForLogging(apiKey: String): String
}