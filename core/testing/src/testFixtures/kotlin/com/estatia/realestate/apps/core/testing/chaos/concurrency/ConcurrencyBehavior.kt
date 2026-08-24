package com.estatia.realestate.apps.core.testing.chaos.concurrency

/**
 * Represents chaotic concurrency scenarios for adversarial testing.
 */
sealed interface ConcurrencyBehavior {
    data object ConcurrentMutation : ConcurrencyBehavior
    data object OutOfOrderResponse : ConcurrencyBehavior
    data object StaleResult : ConcurrencyBehavior
    data object DuplicateRequest : ConcurrencyBehavior
    data object CancellationRace : ConcurrencyBehavior
    data object CallbackRace : ConcurrencyBehavior
    data object DoubleRelease : ConcurrencyBehavior
    data object DoubleInitialization : ConcurrencyBehavior
    data object OperationAfterDisposal : ConcurrencyBehavior
    data object MultipleRefreshOperations : ConcurrencyBehavior
}
