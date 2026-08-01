package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult

interface IConfigRemoteDataSource {
    suspend fun fetchRemoteConfig(): AppResult<String?>
}
