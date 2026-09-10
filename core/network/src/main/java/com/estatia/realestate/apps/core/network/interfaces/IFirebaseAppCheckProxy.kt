package com.estatia.realestate.apps.core.network.interfaces
import com.estatia.realestate.apps.core.architecture.annotations.Contract

/**
 * Proxy for initializing Firebase AppCheck, which has variant-specific implementations.
 */
@Contract
interface IFirebaseAppCheckProxy {
    fun initialize()
}
