package com.estatia.realestate.apps.core.security.models

import kotlinx.serialization.Serializable

@Serializable
data class HybridEncryptedPayload(
    val version:Int,
    val encryptedKey:ByteArray,
    val iv:ByteArray,
    val ciphertext:ByteArray
)
