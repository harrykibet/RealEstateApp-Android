package com.application.real_estate_app.security.utils.exceptions

class SecurityException(
    message: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)