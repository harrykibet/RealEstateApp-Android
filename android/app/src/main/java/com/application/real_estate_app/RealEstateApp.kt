package com.application.real_estate_app

import android.app.Application
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltAndroidApp
class RealEstateApp : Application()  {

    @Inject
    lateinit var logger: LoggerInterface

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
            logger.i("Firebase initialized successfully.")

            // Configure Firebase App Check with Debug Provider
            FirebaseAppCheck.getInstance().apply {
                installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
                logger.i("Firebase App Check Debug Provider installed.")
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
        }.also { logger.i("Global exception handlers configured.") }
    }


    private fun handleUncaughtException(thread: Thread, throwable: Throwable) {
        val errorMessage = "Uncaught exception in thread '${thread.name}': ${throwable.localizedMessage}"
        logger.e(errorMessage, throwable)

        // Log detailed error information to Crashlytics
        FirebaseCrashlytics.getInstance().apply {
            log("[CRITICAL] $errorMessage")
            recordException(throwable)
        }

        // Exit application gracefully if required
        exitApplication()
    }


    private fun String.handleCriticalError(throwable: Throwable) {
        logger.e(this, throwable)
        FirebaseCrashlytics.getInstance().recordException(throwable)
        logger.w("Critical error handled gracefully. Application may behave unexpectedly.")
    }


    private fun exitApplication() {
        logger.w("Application is exiting due to a critical failure.")
        runBlocking(Dispatchers.IO) {
            FirebaseCrashlytics.getInstance().sendUnsentReports() // Ensure all logs are uploaded
        }
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}
