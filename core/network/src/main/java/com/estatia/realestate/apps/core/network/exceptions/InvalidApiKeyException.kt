package com.estatia.realestate.apps.core.network.exceptions

import com.estatia.realestate.apps.core.network.utils.ServiceNames
import java.lang.SecurityException

// Enhanced exception class
class InvalidApiKeyException(
    message: String,
    val service: ServiceNames? = null,
    cause: Throwable? = null
) : SecurityException(message, cause)