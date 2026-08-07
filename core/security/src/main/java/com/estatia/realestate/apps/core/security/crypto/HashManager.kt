package com.estatia.realestate.apps.core.security.crypto

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.common.exceptions.SecurityException
import com.estatia.realestate.apps.core.common.exceptions.map
import com.estatia.realestate.apps.core.security.interfaces.ICryptoExecutor
import com.estatia.realestate.apps.core.security.interfaces.IHashManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HashManager @Inject constructor(
    private val cryptoExecutor: ICryptoExecutor
) : IHashManager {

    private val secureRandom = SecureRandom()

    override suspend fun hash(data: ByteArray): AppResult<ByteArray> =
        cryptoExecutor.execute(SecurityException.HashGenerationFailed()) {
            withContext(Dispatchers.Default) {
                val digest = MessageDigest.getInstance("SHA-256")
                digest.digest(data)
            }
        }

    override suspend fun hashWithSalt(data: ByteArray, salt: ByteArray): AppResult<ByteArray> =
        cryptoExecutor.execute(SecurityException.HashGenerationFailed()) {
            withContext(Dispatchers.Default) {
                val digest = MessageDigest.getInstance("SHA-256")
                digest.update(salt)
                digest.digest(data)
            }
        }

    override fun generateSalt(size: Int): ByteArray {
        val salt = ByteArray(size)
        secureRandom.nextBytes(salt)
        return salt
    }

    override suspend fun hmacSha256(data: ByteArray, key: ByteArray): AppResult<ByteArray> =
        cryptoExecutor.execute(SecurityException.HmacGenerationFailed()) {
            withContext(Dispatchers.Default) {
                val mac = Mac.getInstance("HmacSHA256")
                val secretKey = SecretKeySpec(key, "HmacSHA256")
                mac.init(secretKey)
                mac.doFinal(data)
            }
        }
}
