package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.security.models.EncryptedPayload
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface IAesGcmCryptoEngine {


    suspend fun encrypt(
        data: ByteArray
    ): AppResult<EncryptedPayload>



    suspend fun decrypt(
        payload: EncryptedPayload
    ): AppResult<ByteArray>
}
