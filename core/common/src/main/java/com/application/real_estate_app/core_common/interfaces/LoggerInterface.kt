package com.application.real_estate_app.core_common.interfaces

interface LoggerInterface {
    fun d(message: String)
    fun i(message: String)
    fun w(message: String, throwable: Throwable? = null)
    fun e(message: String, throwable: Throwable? = null)
}