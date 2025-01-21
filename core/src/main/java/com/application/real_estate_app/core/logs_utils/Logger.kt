package com.application.real_estate_app.core.logs_utils

import android.content.Context
import android.util.Log
import com.application.real_estate_app.core.data_utils.data_models.*
import com.application.real_estate_app.core.interfaces.AnalyticsApiInterface
import com.application.real_estate_app.core.interfaces.AuthApiInterface
import com.application.real_estate_app.core.interfaces.LoggerInterface
import com.application.real_estate_app.core.system_utils.DeviceInfoUtil
import com.application.real_estate_app.core.system_utils.LocationInfoUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class Logger @Inject constructor(
    @ApplicationContext context: Context,
    private val analyticsApi: AnalyticsApiInterface,
    private val authApi: AuthApiInterface
) : LoggerInterface {

    private val tag = "AppLogger"
    private val logFileName = "app_logs.txt"
    private val logFile: File = File(context.filesDir, logFileName)
    private val deviceInfo: DeviceInfo = DeviceInfoUtil.getDeviceInfo(context)
    private val locationInfo: UserLocation = LocationInfoUtil.getLocationInfo(context)

    override fun debug(
        message: String,
        eventType: String?,
        customMetadata: Map<String, String>?
    ) {
        log(Log.DEBUG, message, eventType, null, customMetadata)
    }

    override fun info(
        message: String,
        eventType: String?,
        customMetadata: Map<String, String>?
    ) {
        log(Log.INFO, message, eventType, null, customMetadata)
    }

    override fun warn(
        message: String,
        throwable: Throwable? ,
        eventType: String?,
        customMetadata: Map<String, String>?
    ) {
        log(Log.WARN, message, eventType, throwable, customMetadata)
    }

    override fun error(
        message: String,
        throwable: Throwable?,
        eventType: String?,
        customMetadata: Map<String, String>?
    ) {
        log(Log.ERROR, message, eventType, throwable, customMetadata)
    }

    private fun log(
        level: Int,
        message: String,
        eventType: String? = null,
        throwable: Throwable? = null,
        customMetadata: Map<String, String>? = null
    ) {
        val formattedMessage = formatMessage(message)
        when (level) {
            Log.DEBUG -> Log.d(tag, formattedMessage)
            Log.INFO -> Log.i(tag, formattedMessage)
            Log.WARN -> Log.w(tag, formattedMessage, throwable)
            Log.ERROR -> Log.e(tag, formattedMessage, throwable)
        }
        saveLogToFile(formattedMessage, throwable)

        if (eventType != null) {
            handleAnalyticsEvent(level, formattedMessage, eventType, customMetadata)
        }
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
            Log.e(tag, "Failed to write log to file: ${e.message}")
        }
    }

    private fun handleAnalyticsEvent(
        level: Int,
        message: String,
        eventType: String,
        customMetadata: Map<String, String>? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            val userId = authApi.getCurrentUserId()
            userId?.let {
                val metadata = customMetadata?.toMutableMap() ?: mutableMapOf()
                metadata["message"] = message  // Add default message if not already present

                val analyticsEvent = AnalyticsEvent(
                    eventId = "${level}-${System.currentTimeMillis()}",
                    eventType = eventType,
                    userId = it,
                    timestamp = System.currentTimeMillis(),
                    metadata = metadata,
                    deviceInfo = deviceInfo,
                    userLocation = locationInfo
                )

                sendAnalyticsEvent(analyticsEvent)
            }
        }
    }

    private suspend fun sendAnalyticsEvent(event: AnalyticsEvent) {
        try {
            analyticsApi.logEvent(event) { exception ->
                error("Failed to log analytics event: ${exception.message}")
            }
        } catch (e: Exception) {
            error("Error while sending analytics event: ${e.message}")
        }
    }

    override fun getLogs(): String {
        return try {
            logFile.readText()
        } catch (e: IOException) {
            error("Failed to read log file: ${e.message}")
            ""
        }
    }

    override fun clearLogs() {
        try {
            logFile.writeText("")
        } catch (e: IOException) {
            error("Failed to clear log file: ${e.message}")
        }
    }
}
