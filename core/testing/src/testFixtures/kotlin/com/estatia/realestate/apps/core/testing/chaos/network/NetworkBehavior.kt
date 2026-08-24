package com.estatia.realestate.apps.core.testing.chaos.network

import kotlin.time.Duration

/**
 * Represents the behavior of a network request in a chaos scenario.
 */
sealed interface NetworkBehavior {
    data object Success : NetworkBehavior
    
    data object Offline : NetworkBehavior
    
    data object Timeout : NetworkBehavior
    
    data object ConnectionReset : NetworkBehavior

    data class Delay(val duration: Duration) : NetworkBehavior

    data class HttpError(val statusCode: Int) : NetworkBehavior

    data class InvalidBody(val payload: String) : NetworkBehavior
}
