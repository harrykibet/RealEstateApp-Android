package com.application.real_estate_app

import android.app.Application
import com.application.real_estate_app.core.logs_utils.Logger
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

@HiltAndroidApp
class RealEstateApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
        Logger.initialize(this)
        configureGlobalExceptionHandler()
    }

    /**
     * Initializes Firebase and sets up App Check with Debug Provider.
     */
    private fun initializeFirebase() {
        runCatching {
            FirebaseApp.initializeApp(this)
                ?: throw IllegalStateException("Firebase initialization failed. Check your google-services.json configuration.")
            Logger.info("Firebase initialized successfully.")

            // Configure Firebase App Check with Debug Provider
            FirebaseAppCheck.getInstance().apply {
                installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
                Logger.info("Firebase App Check Debug Provider installed.")
            }
        }.onFailure { exception ->
            "Failed to initialize Firebase or App Check.".handleCriticalError(exception)
        }
    }

    /**
     * Configures a global uncaught exception handler to ensure all crashes are logged to Crashlytics.
     */
    private fun configureGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleUncaughtException(thread, throwable)
        }

        CoroutineExceptionHandler { _, throwable ->
            handleUncaughtException(Thread.currentThread(), throwable)
        }.also { Logger.info("Global exception handlers configured.") }
    }

    /**
     * Handles uncaught exceptions and logs them to Crashlytics.
     */
    private fun handleUncaughtException(thread: Thread, throwable: Throwable) {
        val errorMessage = "Uncaught exception in thread '${thread.name}': ${throwable.localizedMessage}"
        Logger.error(errorMessage, throwable)

        // Log detailed error information to Crashlytics
        FirebaseCrashlytics.getInstance().apply {
            log("[CRITICAL] $errorMessage")
            recordException(throwable)
        }

        // Exit application gracefully if required
        exitApplication()
    }

    /**
     * Handles critical errors during initialization or runtime.
     */
    private fun String.handleCriticalError(throwable: Throwable) {
        Logger.error(this, throwable)
        FirebaseCrashlytics.getInstance().recordException(throwable)
        Logger.warn("Critical error handled gracefully. Application may behave unexpectedly.")
    }

    /**
     * Gracefully exits the application to avoid undefined behavior after critical failures.
     */
    private fun exitApplication() {
        Logger.warn("Application is exiting due to a critical failure.")
        runBlocking(Dispatchers.IO) {
            FirebaseCrashlytics.getInstance().sendUnsentReports() // Ensure all logs are uploaded
        }
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}
