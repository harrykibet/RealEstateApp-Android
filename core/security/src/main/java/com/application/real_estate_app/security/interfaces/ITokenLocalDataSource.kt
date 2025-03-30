package com.application.real_estate_app.security.interfaces
import com.application.real_estate_app.core_common.errors.Result

interface ITokenLocalDataSource {
    suspend fun saveToken(token: String): Result<Unit>
    suspend fun getToken(): Result<String?>
    suspend fun clearToken(): Result<Unit>
}