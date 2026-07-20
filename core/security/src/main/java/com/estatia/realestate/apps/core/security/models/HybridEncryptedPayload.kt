package com.estatia.realestate.apps.core.security.models

data class HybridEncryptedPayload(
    val version:Int,
    val encryptedKey:ByteArray,
    val iv:ByteArray,
    val ciphertext:ByteArray
)