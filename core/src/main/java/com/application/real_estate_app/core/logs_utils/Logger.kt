package com.application.real_estate_app.core.logs_utils

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import com.application.real_estate_app.core.interfaces.LoggerInterface
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class Logger @Inject constructor(
): LoggerInterface {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val tag = "AppLogger"
    private val logFileName = "app_logs.txt"
    private val logFile: File = File(context.filesDir, logFileName)

    override fun debug(
        message: String
    ) {
        log(Log.DEBUG, message, null)
    }

    override fun info(
        message: String
    ) {
        log(Log.INFO, message, null)
    }

    override fun warn(
        message: String,
        throwable: Throwable?
    ) {
        log(Log.WARN, message, throwable)
    }

    override fun error(
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
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        return "$timestamp $message"
    }

    private fun saveLogToFile(message: String, throwable: Throwable? = null) {
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
            error("Failed to write log to file: ${e.message}")
        }
    }

    fun getLogs(): String {
        return try {
            logFile.readText()
        } catch (e: IOException) {
            error("Failed to read log file: ${e.message}")
        }.toString()
    }

    fun clearLogs() {
        try {
            logFile.writeText("")
        } catch (e: IOException) {
            error("Failed to clear log file: ${e.message}")
        }
    }
}
