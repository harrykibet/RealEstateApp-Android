package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult

interface IHashManager {
    /**
     * Hashes raw binary data using SHA-256.
     */
    suspend fun hash(data: ByteArray): AppResult<ByteArray>

    /**
     * Hashes binary data with a provided salt using SHA-256.
     */
    suspend fun hashWithSalt(data: ByteArray, salt: ByteArray): AppResult<ByteArray>

    /**
     * Generates a cryptographically secure random salt.
     */
    fun generateSalt(size: Int = 16): ByteArray

    /**
     * Generates an HMAC-SHA256 authentication code for the given data and key.
     */
    suspend fun hmacSha256(data: ByteArray, key: ByteArray): AppResult<ByteArray>
}
