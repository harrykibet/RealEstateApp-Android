package com.estatia.realestate.apps.core.network.interfaces

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.architecture.annotations.Identity.DataSource

@DataSource
interface IConfigRemoteDataSource {
    suspend fun fetchRemoteConfig(): AppResult<String?>
}
