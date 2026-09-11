package com.estatia.realestate.apps.core.security.models

import kotlinx.serialization.Serializable
import com.estatia.realestate.apps.core.architecture.annotations.Utility

@Serializable
@Utility
data class EncryptedPayload(
    val version:Int,
    val iv:ByteArray,
    val ciphertext:ByteArray
)
