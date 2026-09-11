package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract


@Contract
interface ISignatureManager {


    suspend fun sign(
        data: ByteArray,
        keyAlias: String
    ): AppResult<ByteArray>



    suspend fun verify(
        data: ByteArray,
        signature: ByteArray,
        keyAlias: String
    ): AppResult<Boolean>

}
