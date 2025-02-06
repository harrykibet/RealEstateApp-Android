package com.application.real_estate_app

import android.app.Application
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.application.real_estate_app.core.utils.media_players.exoplayer.MediaPlayer
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.system.exitProcess

@UnstableApi
@HiltAndroidApp
class RealEstateApp : Application()  {

    @Inject
    lateinit var logger: LoggerInterface

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer.create(
            context = this,
            config = MediaPlayer.PlayerConfig(
                drmConfig = MediaPlayer.DrmConfig(
                    uuid = C.WIDEVINE_UUID,
                    multiSession = true
                )
            )
        )
        initializeFirebase()
        configureGlobalExceptionHandler()
    }

    /**
     * Initializes Firebase and sets up App Check with Debug Provider.
     */
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

    /**
     * Configures a global uncaught exception handler to ensure all crashes are logged to Crashlytics.
     */
    private fun configureGlobalExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleUncaughtException(thread, throwable)
        }

        CoroutineExceptionHandler { _, throwable ->
            handleUncaughtException(Thread.currentThread(), throwable)
        }.also { logger.i("Global exception handlers configured.") }
    }

    /**
     * Handles uncaught exceptions and logs them to Crashlytics.
     */
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

    /**
     * Handles critical errors during initialization or runtime.
     */
    private fun String.handleCriticalError(throwable: Throwable) {
        logger.e(this, throwable)
        FirebaseCrashlytics.getInstance().recordException(throwable)
        logger.w("Critical error handled gracefully. Application may behave unexpectedly.")
    }

    /**
     * Gracefully exits the application to avoid undefined behavior after critical failures.
     */
    private fun exitApplication() {
        logger.w("Application is exiting due to a critical failure.")
        runBlocking(Dispatchers.IO) {
            FirebaseCrashlytics.getInstance().sendUnsentReports() // Ensure all logs are uploaded
        }
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(10)
    }
}
