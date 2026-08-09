package com.estatia.realestate.apps.core.network.sources.aws

import com.amplifyframework.core.Amplify
import com.estatia.realestate.apps.core.domain.interfaces.ICrashReporter
import javax.inject.Inject

/**
 * AWS implementation of [ICrashReporter] using CloudWatch via Amplify Logging.
 */
internal class AwsCrashReporter @Inject constructor() : ICrashReporter {

    override fun log(message: String) {
        Amplify.Logging.logger("Estatia").info(message)
    }

    override fun recordException(throwable: Throwable) {
        Amplify.Logging.logger("Estatia").error(throwable.message, throwable)
    }

    override fun setCustomKey(key: String, value: String) {
        // CloudWatch logging doesn't have direct custom keys like Crashlytics,
        // but we can log them as structured context.
        log("Context [$key]: $value")
    }
}
