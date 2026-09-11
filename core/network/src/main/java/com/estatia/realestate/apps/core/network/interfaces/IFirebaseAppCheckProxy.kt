package com.estatia.realestate.apps.core.network.interfaces
import com.estatia.realestate.apps.core.architecture.annotations.DataSource

/**
 * Proxy for initializing Firebase AppCheck, which has variant-specific implementations.
 */
@DataSource
interface IFirebaseAppCheckProxy {
    fun initialize()
}
