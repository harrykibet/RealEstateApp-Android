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
 * TRULY AWS READY: This implementation uses the AWS AppConfig Data SDK
 * to retrieve dynamic configuration profiles.
 */
class AwsConfigRemoteDataSource @Inject constructor(
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
