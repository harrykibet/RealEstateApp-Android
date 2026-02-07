package com.estatia.realestate.apps

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

@HiltAndroidApp
class EstatiaApplication : Application()  {

    override fun onCreate() {
        super.onCreate()
        initializeFirebase()
        initializeBouncyCastle()
        configureGlobalExceptionHandler()
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
}
