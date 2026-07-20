package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.errors.AppResult


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