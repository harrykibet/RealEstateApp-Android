package com.estatia.realestate.apps.core.network.sources.aws

import android.content.Context
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.logging.cloudwatch.AWSCloudWatchLoggingPlugin
import com.amplifyframework.analytics.pinpoint.AWSPinpointAnalyticsPlugin
import com.amplifyframework.storage.s3.AWSS3StoragePlugin
import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * AWS implementation of [IBackendInitializer].
 * Prepares the Amplify framework for Aurora Serverless (via AppSync) and Cognito.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Atomic initialization of the AWS Amplify SDK stack.
 * - Concurrency: Thread-safe; handles SDK-internal locking.
 * - Resilience: Surfaces a terminal failure log if plugins cannot be registered.
 */
internal class AwsBackendInitializer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: ILogger
) : IBackendInitializer {

    override suspend fun initialize() {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.addPlugin(AWSPinpointAnalyticsPlugin())
            Amplify.addPlugin(AWSCloudWatchLoggingPlugin())
            
            // Note: This requires amplifyconfiguration.json in app/res/raw
            Amplify.configure(context)
            
            logger.i("AwsBackend", "Amplify initialized successfully")
        } catch (error: AmplifyException) {
            logger.e("AwsBackend", "Could not initialize Amplify", error)
        }
    }
}
