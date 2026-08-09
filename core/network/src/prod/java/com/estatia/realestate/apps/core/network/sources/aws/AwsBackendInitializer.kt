package com.estatia.realestate.apps.core.network.sources.aws

import android.content.Context
import android.util.Log
import com.estatia.realestate.apps.core.network.interfaces.IBackendInitializer
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * AWS implementation of [IBackendInitializer].
 * Prepares the Amplify framework for Aurora Serverless (via AppSync) and Cognito.
 */
internal class AwsBackendInitializer @Inject constructor(
    @ApplicationContext private val context: Context
) : IBackendInitializer {

    override fun initialize() {
        /*
        // TRULY AWS READY: Uncomment once amplifyconfiguration.json is added to res/raw
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(context)
            Log.i("AwsBackend", "Amplify initialized successfully")
        } catch (error: AmplifyException) {
            Log.e("AwsBackend", "Could not initialize Amplify", error)
        }
        */
        Log.i("AwsBackend", "AWS Initializer in passive mode (waiting for configuration)")
    }
}
