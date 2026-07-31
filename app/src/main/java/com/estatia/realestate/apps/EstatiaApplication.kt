package com.estatia.realestate.apps

import android.app.Application
import com.estatia.realestate.apps.core.config.repository.ConfigRepository
import com.estatia.realestate.apps.feature.search.PlacesManager
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.inject.Inject

@HiltAndroidApp
class EstatiaApplication : Application()  {

    @Inject
    lateinit var config: ConfigRepository

    @Inject
    lateinit var placesManager: PlacesManager

    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
        initializeBouncyCastle()
        configureGlobalExceptionHandler()
        initializeConfig()
        initializePlaces()
    }

    private fun initializePlaces() {
        placesManager.initialize(this)
    }

    private fun initializeConfig() {
        CoroutineScope(Dispatchers.IO).launch {
            config.initialize()
        }
    }

    private fun initializeFirebase() {
        FirebaseApp.initializeApp(this)
            ?: error("Firebase initialization failed")

        FirebaseAppCheckInitializer.init()
    }


    private fun initializeBouncyCastle() {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }


    private fun configureGlobalExceptionHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            handleUncaughtException(thread, throwable)
            defaultHandler?.uncaughtException(thread, throwable)
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
}
