package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.errors.AppResult
import com.estatia.realestate.apps.core.security.models.HybridEncryptedPayload

interface IRsaCryptoEngine {


    suspend fun encrypt(
        data: ByteArray
    ): AppResult<HybridEncryptedPayload>



    suspend fun decrypt(
        payload: HybridEncryptedPayload
    ): AppResult<ByteArray>
}