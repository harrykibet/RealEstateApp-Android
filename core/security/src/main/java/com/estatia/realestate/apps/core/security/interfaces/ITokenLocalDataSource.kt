package com.estatia.realestate.apps.core.security.interfaces

import com.estatia.realestate.apps.core.common.errors.AppResult

interface ITokenLocalDataSource {
    suspend fun saveToken(token: String): AppResult<Unit>
    suspend fun getToken(): AppResult<String?>
    suspend fun clearToken(): AppResult<Unit>
}