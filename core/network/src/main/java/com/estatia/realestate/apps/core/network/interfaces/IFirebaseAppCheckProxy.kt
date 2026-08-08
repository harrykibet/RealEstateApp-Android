package com.estatia.realestate.apps.core.network.interfaces

/**
 * Proxy for initializing Firebase AppCheck, which has variant-specific implementations.
 */
interface IFirebaseAppCheckProxy {
    fun initialize()
}
