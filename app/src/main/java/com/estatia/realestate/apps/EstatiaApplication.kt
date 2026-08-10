package com.estatia.realestate.apps

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.estatia.realestate.apps.core.domain.interfaces.IConfigProvider
import com.estatia.realestate.apps.core.domain.interfaces.ICrashReporter
import com.estatia.realestate.apps.core.common.interfaces.IBackendInitializer
import com.estatia.realestate.apps.core.common.di.ApplicationScope
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.inject.Inject

@HiltAndroidApp
class EstatiaApplication : Application(), Configuration.Provider  {

    @Inject
    lateinit var config: IConfigProvider

    @Inject
    lateinit var crashReporter: ICrashReporter

    @Inject
    lateinit var backendInitializers: Set<@JvmSuppressWildcards IBackendInitializer>

    @Inject
    @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        initializeBackends()
        initializeBouncyCastle()
        configureGlobalExceptionHandler()
        initializeConfig()
    }

    private fun initializeConfig() {
        applicationScope.launch {
            config.initialize()
        }
    }

    private fun initializeBackends() {
        backendInitializers.forEach { it.initialize() }
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

        crashReporter.log("[CRITICAL] $errorMessage")
        crashReporter.recordException(throwable)
    }
}
