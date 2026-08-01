package com.estatia.realestate.apps.core.network.sources

import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.interfaces.IConfigRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseConfigRemoteDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val networkClient: INetworkClient
) : IConfigRemoteDataSource {

    companion object {
        private const val CONFIG_KEY = "runtime_config_json"
    }

    override suspend fun fetchRemoteConfig(): AppResult<String?> {
        return networkClient.execute {
            remoteConfig.fetchAndActivate().await()
            remoteConfig.getString(CONFIG_KEY).takeIf { it.isNotBlank() }
        }
    }
}
