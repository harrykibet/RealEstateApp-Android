package com.estatia.realestate.apps.core.security.models

import kotlinx.serialization.Serializable

@Serializable
data class EncryptedPayload(
    val version:Int,
    val iv:ByteArray,
    val ciphertext:ByteArray
)
