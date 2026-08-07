package com.estatia.realestate.apps.core.data.repositories

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.domain.interfaces.IConfigDataRepository
import com.estatia.realestate.apps.core.network.interfaces.IConfigRemoteDataSource
import javax.inject.Inject

internal class ConfigDataRepository @Inject constructor(
    private val remoteDataSource: IConfigRemoteDataSource
) : IConfigDataRepository {

    override suspend fun fetchRemoteConfig(): AppResult<String?> {
        return remoteDataSource.fetchRemoteConfig()
    }
}
