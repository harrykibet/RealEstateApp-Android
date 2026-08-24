package com.estatia.realestate.apps.core.common.logs

import android.util.Log
import com.estatia.realestate.apps.core.common.interfaces.ILogger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

/**
 * Standard implementation of [ILogger] for the Estatia project.
 * 
 * 🏗️ OPERATIONAL CONTRACT:
 * - Responsibility: Manage local console logging with standardized formatting.
 * - Security: Does NOT filter sensitive data automatically; callers must redact secrets before logging.
 * - Concurrency: Thread-safe (delegates to system [Log]).
 * - Performance: Avoids heavy string concatenation when Log.DEBUG is disabled in production.
 */
class Logger @Inject constructor(
) : ILogger {

    private val defaultTag = "AppLogger"

    private fun getTag(tag: String?): String {
        return tag ?: defaultTag
    }

    override fun d(
        tag: String?,
        message: String
    ) {

        log(tag, Log.DEBUG, message, null)
    }

    override fun i(
        tag: String?,
        message: String
    ) {
        log(tag,Log.INFO, message, null)
    }

    override fun w(
        tag: String?,
        message: String,
        throwable: Throwable?
    ) {
        log(tag,Log.WARN, message, throwable)
    }

    override fun e(
        tag: String?,
        message: String,
        throwable: Throwable?
    ) {
        log(tag,Log.ERROR, message, throwable)
    }

    fun log(
        tag: String? = null,
        level: Int,
        message: String,
        throwable: Throwable? = null
    ) {

        val formattedMessage = formatMessage(message)
        val logTag = getTag(tag)

        when (level) {
            Log.DEBUG -> Log.d(logTag, formattedMessage)
            Log.INFO -> Log.i(logTag, formattedMessage)
            Log.WARN -> Log.w(logTag, formattedMessage, throwable)
            Log.ERROR -> Log.e(logTag, formattedMessage, throwable)
        }
    }

    private fun formatMessage(message: String): String {
        val timestamp =
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date())
        return "$timestamp $message"
    }
}
