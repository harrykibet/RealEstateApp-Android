package com.estatia.realestate.apps.core.common.interfaces

interface LoggerInterface {
    fun d(message: String)
    fun i(message: String)
    fun w(message: String, throwable: Throwable? = null)
    fun e(message: String, throwable: Throwable? = null)
}