package com.application.real_estate_app.core.interfaces

interface LoggerInterface {
    fun debug(
        message: String,
        eventType: String? = null,
        customMetadata: Map<String,
        String>? = null)

    fun info(
        message: String,
        eventType: String? = null,
        customMetadata: Map<String,
        String>? = null)

    fun warn(
        message: String,
        throwable: Throwable? = null,
        eventType: String? = null,
        customMetadata: Map<String,
        String>? = null)

    fun error(
        message: String,
        throwable: Throwable? = null,
        eventType: String? = null,
        customMetadata: Map<String,
        String>? = null)

    fun getLogs(): String

    fun clearLogs()
}