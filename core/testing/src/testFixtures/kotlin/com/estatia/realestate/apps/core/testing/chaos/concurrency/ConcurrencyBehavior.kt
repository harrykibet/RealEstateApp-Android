package com.estatia.realestate.apps.core.testing.chaos.concurrency

/**
 * Represents chaotic concurrency scenarios for adversarial testing.
 */
sealed interface ConcurrencyBehavior {
    
    /**
     * Deterministic chaos: reproducible behaviors for CI tests.
     */
    sealed interface Deterministic : ConcurrencyBehavior
    
    /**
     * Probabilistic stress: for fuzzing and stress testing.
     */
    sealed interface Probabilistic : ConcurrencyBehavior

    data object Success : Deterministic
    data class ConcurrentMutation(val delayMillis: Long = 100) : Deterministic
    data class OutOfOrderResponse(val delayMillis: Long = 200) : Deterministic
    data class StaleResult(val delayMillis: Long = 500) : Deterministic
    data object DuplicateRequest : Deterministic
    data object CancellationRace : Deterministic
    data object CallbackRace : Deterministic
    data object DoubleRelease : Deterministic
    data object DoubleInitialization : Deterministic
    data object OperationAfterDisposal : Deterministic
    data class MultipleRefreshOperations(val delayMillis: Long = 50) : Deterministic
    
    /**
     * Probabilistic behaviors that use internal non-determinism.
     */
    data class RandomInterleaving(
        val probability: Double = 0.5,
        val delayMillis: Long = 200
    ) : Probabilistic
}
