package com.estatia.realestate.apps.core.data

/**
 * Represents encryption modes for security operations.
 */
enum class CryptMode {
    REMOTE,  // Cloud-based encryption (Google KMS)
    LOCAL    // Local device-based encryption
}