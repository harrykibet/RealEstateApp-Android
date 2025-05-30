package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.errors.Result

interface ITokenLocalDataSource {
    suspend fun saveToken(token: String): Result<Unit>
    suspend fun getToken(): Result<String?>
    suspend fun clearToken(): Result<Unit>
}