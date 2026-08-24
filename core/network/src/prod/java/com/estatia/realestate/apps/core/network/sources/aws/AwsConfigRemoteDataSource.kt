package com.estatia.realestate.apps.core.network.sources.aws

import aws.sdk.kotlin.services.appconfigdata.AppConfigDataClient
import aws.sdk.kotlin.services.appconfigdata.model.GetLatestConfigurationRequest
import aws.sdk.kotlin.services.appconfigdata.model.StartConfigurationSessionRequest
import com.estatia.realestate.apps.core.common.exceptions.AppResult
import com.estatia.realestate.apps.core.network.interfaces.IConfigRemoteDataSource
import com.estatia.realestate.apps.core.network.interfaces.INetworkClient
import javax.inject.Inject

/**
 * AWS implementation of [IConfigRemoteDataSource].
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Retrieve dynamic configuration profiles via AWS AppConfig.
 * - Concurrency: Thread-safe.
 * - Resilience: Transparently uses [networkClient] for retries.
 */
internal class AwsConfigRemoteDataSource @Inject constructor(
    private val networkClient: INetworkClient
) : IConfigRemoteDataSource {

    override suspend fun fetchRemoteConfig(): AppResult<String?> = networkClient.execute {
        AppConfigDataClient { region = "us-east-1" }.use { client ->
            
            // 1. Start a configuration session
            val sessionResponse = client.startConfigurationSession(
                StartConfigurationSessionRequest {
                    applicationIdentifier = "Estatia"
                    environmentIdentifier = "Prod"
                    configurationProfileIdentifier = "RuntimeConfig"
                }
            )

            // 2. Get the latest configuration
            val configResponse = client.getLatestConfiguration(
                GetLatestConfigurationRequest {
                    configurationToken = sessionResponse.initialConfigurationToken
                }
            )

            // 3. Convert content to string
            configResponse.configuration?.decodeToString()
        }
    }
}
