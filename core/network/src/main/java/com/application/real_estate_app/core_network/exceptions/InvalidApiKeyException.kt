package com.application.real_estate_app.core_network.exceptions

import com.application.real_estate_app.security.utils.extensions.ServiceNames
import java.lang.SecurityException

// Enhanced exception class
class InvalidApiKeyException(
    message: String,
    val service: ServiceNames? = null,
    cause: Throwable? = null
) : SecurityException(message, cause)