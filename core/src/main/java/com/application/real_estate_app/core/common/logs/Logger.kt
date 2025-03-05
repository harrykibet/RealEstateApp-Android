package com.application.real_estate_app.core.common.logs

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.application.real_estate_app.core.domain.interfaces.IRemoteConfigManager
import com.application.real_estate_app.core.domain.interfaces.LoggerInterface
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class Logger @Inject constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val firebaseRemoteConfig: IRemoteConfigManager
) : LoggerInterface {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val tag = "AppLogger"
    private val logFileName = "app_logs.txt"
    private val logFile: File = File(context.filesDir, logFileName)

    override fun d(
        message: String
    ) {
        log(Log.DEBUG, message, null)
    }

    override fun i(
        message: String
    ) {
        log(Log.INFO, message, null)
    }

    override fun w(
        message: String,
        throwable: Throwable?
    ) {
        log(Log.WARN, message, throwable)
    }

    override fun e(
        message: String,
        throwable: Throwable?
    ) {
        log(Log.ERROR, message, throwable)
    }

    fun log(
        level: Int,
        message: String,
        throwable: Throwable? = null
    ) {
        val formattedMessage = formatMessage(message)
        when (level) {
            Log.DEBUG -> Log.d(tag, formattedMessage)
            Log.INFO -> Log.i(tag, formattedMessage)
            Log.WARN -> Log.w(tag, formattedMessage, throwable)
            Log.ERROR -> Log.e(tag, formattedMessage, throwable)
        }
        saveLogToFile(formattedMessage, throwable)
    }

    private fun formatMessage(message: String): String {
        val timestamp =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        return "$timestamp $message"
    }

    override fun saveLogToFile(message: String, throwable: Throwable?) {
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
        try {
            FileWriter(logFile, true).use { writer ->
                writer.append(message).append("\n")
                throwable?.let {
                    writer.append(Log.getStackTraceString(it)).append("\n")
                }
            }
        } catch (e: IOException) {
            e("Failed to write log to file: ${e.message}")
        }
    }

    @Suppress("UNUSED")
    fun getLogs(): String {
        return try {
            logFile.readText()
        } catch (e: IOException) {
            e("Failed to read log file: ${e.message}")
        }.toString()
    }

    @Suppress("UNUSED")
    fun clearLogs() {
        try {
            logFile.writeText("")
        } catch (e: IOException) {
            e("Failed to clear log file: ${e.message}")
        }
    }
}
