package com.estatia.realestate.apps.core.config.datasource

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRemoteConfigDataSource @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig
) {

    companion object {
        private const val CONFIG_KEY = "runtime_config_json"
    }

    suspend fun fetch(): String? {
        return try {

            remoteConfig.fetchAndActivate().await()

            remoteConfig.getString(CONFIG_KEY)
                .takeIf { it.isNotBlank() }

        } catch (_: Exception) {
            null
        }
    }
}