package com.estatia.realestate.apps.core.testing.chaos.network

import kotlin.time.Duration

/**
 * Represents the behavior of a network request in a chaos scenario.
 * 
 * 🏎️ OPERATIONAL MODES:
 * - Exception Injection: Traditional "throw Throwable" behavior for protocol/IO failures.
 * - Semantic Chaos: Controlled execution semantics (Reordering, Duplication, Truncation).
 */
sealed interface NetworkBehavior {
    // --- Exception Injection ---
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
    data object UnexpectedSchema : NetworkBehavior
    data object OversizedResponse : NetworkBehavior
    
    // --- Semantic Chaos (Advanced) ---
    /** 
     * The call succeeds but the response is truncated or incomplete.
     * Requires the client to simulate a stream end before the full payload is read.
     */
    data object PartialResponse : NetworkBehavior
    
    /**
     * The call is held back until a subsequent request completes, simulating reordering.
     */
    data object OutOfOrderResponse : NetworkBehavior
    
    /**
     * The call succeeds, but the caller receives the response twice (simulating replays).
     */
    data object DuplicateResponse : NetworkBehavior
    
    /**
     * Specific case: Server processed the request but the client didn't wait long enough
     * to receive the 'success' acknowledgement.
     */
    data object ServerSuccessClientTimeout : NetworkBehavior
    
    data class InvalidBody(val payload: String) : NetworkBehavior
}
