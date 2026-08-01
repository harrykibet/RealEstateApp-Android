package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import java.security.KeyPair
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.SecretKey

interface IKeyStoreManager {


    suspend fun initialize(): AppResult<Unit>


    suspend fun generateAesKey(
        alias: String
    ): AppResult<Unit>


    suspend fun generateRsaEncryptionKey(
        alias: String
    ): AppResult<Unit>


    suspend fun generateRsaSigningKey(
        alias: String
    ): AppResult<Unit>


    suspend fun rotateKey(
        alias: String
    ): AppResult<Unit>


    fun getSecretKey(
        alias: String
    ): SecretKey?


    fun getPrivateKey(
        alias: String
    ): PrivateKey?


    fun getPublicKey(
        alias: String
    ): PublicKey?


    fun containsKey(
        alias: String
    ): Boolean

    fun getKeyPair(alias: String): AppResult<KeyPair>
    fun deleteKey(alias: String): AppResult<Unit>
}
