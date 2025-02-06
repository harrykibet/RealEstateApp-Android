package com.application.real_estate_app.core.domain.interfaces

interface LoggerInterface {
    fun d(message: String)
    fun i(message: String)
    fun w(message: String, throwable: Throwable? = null)
    fun e(message: String, throwable: Throwable? = null)
    fun saveLogToFile(message: String, throwable: Throwable? = null)
}