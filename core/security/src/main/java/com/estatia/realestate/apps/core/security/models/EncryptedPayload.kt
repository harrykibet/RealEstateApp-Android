package com.estatia.realestate.apps.core.security.models

data class EncryptedPayload(
    val version:Int,
    val iv:ByteArray,
    val ciphertext:ByteArray
)
