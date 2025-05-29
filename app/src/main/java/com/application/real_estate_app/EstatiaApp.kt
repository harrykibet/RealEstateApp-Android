package com.application.real_estate_app

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

@HiltAndroidApp
class EstatiaApp : Application()  {

    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
        initializeBouncyCastle()
        configureGlobalExceptionHandler()
    }


    private fun initializeFirebase() {
        runCatching {
            FirebaseApp.initializeApp(this)
                ?: throw IllegalStateException("Firebase initialization failed. Check your google-services.json configuration.")

            // Configure Firebase App Check with Debug Provider
            FirebaseAppCheck.getInstance().apply {
                installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
            }
        }.onFailure { exception ->
            "Failed to initialize Firebase or App Check.".handleCriticalError(exception)
        }
    }

    private fun initializeBouncyCastle() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }


    private fun configureGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleUncaughtException(thread, throwable)
        }

        CoroutineExceptionHandler { _, throwable ->
            handleUncaughtException(Thread.currentThread(), throwable)
        }
    }


    private fun handleUncaughtException(thread: Thread, throwable: Throwable) {
        val errorMessage = "Uncaught exception in thread '${thread.name}': ${throwable.localizedMessage}"

        // Log detailed error information to Crashlytics
        FirebaseCrashlytics.getInstance().apply {
            log("[CRITICAL] $errorMessage")
            recordException(throwable)
        }
    }


    private fun String.handleCriticalError(throwable: Throwable) {
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }
}
