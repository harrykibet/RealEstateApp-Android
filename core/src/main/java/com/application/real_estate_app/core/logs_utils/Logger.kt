package com.application.real_estate_app.core.logs_utils

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("UNUSED")
object Logger {

    private const val TAG = "AppLogger"
    private const val LOG_FILE_NAME = "app_logs.txt"
    private lateinit var logFile: File

    // Initializes the log file, called from Application class or main activity
    fun initialize(context: Context) {
        logFile = File(context.filesDir, LOG_FILE_NAME)
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }

    // Public logging methods
    fun debug(message: String) {
        log(Log.DEBUG, message)
    }

    fun info(message: String) {
        log(Log.INFO, message)
    }

    fun warn(message: String, throwable: Throwable? = null) {
        log(Log.WARN, message, throwable)
    }

    fun error(message: String, throwable: Throwable? = null) {
        log(Log.ERROR, message, throwable)
    }

    // Generic log method
    private fun log(level: Int, message: String, throwable: Throwable? = null) {
        val formattedMessage = formatMessage(message)
        when (level) {
            Log.DEBUG -> Log.d(TAG, formattedMessage)
            Log.INFO -> Log.i(TAG, formattedMessage)
            Log.WARN -> Log.w(TAG, formattedMessage, throwable)
            Log.ERROR -> Log.e(TAG, formattedMessage, throwable)
        }
        saveLogToFile(formattedMessage, throwable)
    }

    // Formats the log message with timestamp
    private fun formatMessage(message: String): String {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        return "$timestamp $message"
    }

    // Saves the log message to a file
    private fun saveLogToFile(message: String, throwable: Throwable? = null) {
        try {
            FileWriter(logFile, true).use { writer ->
                writer.append(message).append("\n")
                throwable?.let {
                    writer.append(Log.getStackTraceString(it)).append("\n")
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Failed to write log to file: ${e.message}")
        }
    }

    // Method to retrieve logs from the file (for debugging or support purposes)
    fun getLogs(): String {
        return try {
            logFile.readText()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to read log file: ${e.message}")
            ""
        }
    }

    // Method to clear the log file (useful for resetting logs periodically)
    fun clearLogs() {
        try {
            logFile.writeText("")
        } catch (e: IOException) {
            Log.e(TAG, "Failed to clear log file: ${e.message}")
        }
    }
}
