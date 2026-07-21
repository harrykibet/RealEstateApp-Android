package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.security.models.EncryptedPayload

interface IAesGcmCryptoEngine {


    suspend fun encrypt(
        data: ByteArray
    ): AppResult<EncryptedPayload>



    suspend fun decrypt(
        payload: EncryptedPayload
    ): AppResult<ByteArray>
}