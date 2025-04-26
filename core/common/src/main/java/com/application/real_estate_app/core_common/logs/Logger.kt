package com.application.real_estate_app.core_common.logs

import android.util.Log
import com.application.real_estate_app.core_common.interfaces.LoggerInterface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class Logger @Inject constructor(
) : LoggerInterface {

    private val tag = "AppLogger"
    private val logFileName = "app_logs.txt"

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
    }

    private fun formatMessage(message: String): String {
        val timestamp =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        return "$timestamp $message"
    }
}
