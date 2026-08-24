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
    data object ConnectionRefused : NetworkBehavior
    data object DnsFailure : NetworkBehavior
    data class Delay(val duration: Duration) : NetworkBehavior
    data class HttpError(val statusCode: Int) : NetworkBehavior
    data object MalformedResponse : NetworkBehavior
    data object EmptyResponse : NetworkBehavior
    data object PartialResponse : NetworkBehavior
    data object UnexpectedSchema : NetworkBehavior
    data object OversizedResponse : NetworkBehavior
    data object DuplicateResponse : NetworkBehavior
    data object OutOfOrderResponse : NetworkBehavior
    data object ServerSuccessClientTimeout : NetworkBehavior
    data class InvalidBody(val payload: String) : NetworkBehavior
}
