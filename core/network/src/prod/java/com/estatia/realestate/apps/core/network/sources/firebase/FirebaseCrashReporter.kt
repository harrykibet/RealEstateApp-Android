package com.estatia.realestate.apps.core.network.sources.firebase

import com.estatia.realestate.apps.core.domain.analytics.ICrashReporter
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject

/**
 * Firebase implementation of [ICrashReporter] using Crashlytics.
 */
internal class FirebaseCrashReporter @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) : ICrashReporter {

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }

    override fun setCustomKey(key: String, value: String) {
        crashlytics.setCustomKey(key, value)
    }
}
