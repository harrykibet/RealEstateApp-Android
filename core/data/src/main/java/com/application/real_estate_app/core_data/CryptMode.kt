package com.application.real_estate_app.core_data

/**
 * Represents encryption modes for security operations.
 */
enum class CryptMode {
    REMOTE,  // Cloud-based encryption (Google KMS)
    LOCAL    // Local device-based encryption
}