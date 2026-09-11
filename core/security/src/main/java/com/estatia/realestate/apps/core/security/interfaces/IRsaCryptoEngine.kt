package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.security.models.HybridEncryptedPayload
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract

@Contract
interface IRsaCryptoEngine {


    suspend fun encrypt(
        data: ByteArray
    ): AppResult<HybridEncryptedPayload>



    suspend fun decrypt(
        payload: HybridEncryptedPayload
    ): AppResult<ByteArray>
}
