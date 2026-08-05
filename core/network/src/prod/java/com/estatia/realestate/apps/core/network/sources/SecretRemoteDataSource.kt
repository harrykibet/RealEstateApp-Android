package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.model.security.SecretId
import com.estatia.realestate.apps.core.network.api.SecretApi
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.estatia.realestate.apps.core.network.interfaces.ISecretRemoteDataSource
import javax.inject.Inject

/**
 * Remote data source that fetches secrets from a secure backend using [SecretApi].
 * Uses [INetworkClient] for standardized error handling and logging.
 */
class SecretRemoteDataSource @Inject constructor(
    private val api: SecretApi,
    private val networkClient: INetworkClient
) : ISecretRemoteDataSource {

    override suspend fun fetchSecret(secretId: SecretId): AppResult<String> {
        return networkClient.execute {
            api.getSecret(secretId.value)
        }
    }
}
