package com.estatia.realestate.apps.core.common.interfaces
import com.estatia.realestate.apps.core.architecture.annotations.Contract

@Contract
interface ILogger {

    fun d(tag: String? = null, message: String)
    fun i(tag: String? = null, message: String)
    fun w(tag: String? = null, message: String, throwable: Throwable? = null)

    fun e(tag: String? = null, message: String, throwable: Throwable? = null)
}
