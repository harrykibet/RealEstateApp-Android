package com.estatia.realestate.apps.core.testing.chaos.time

/**
 * Represents chaotic time scenarios for adversarial testing.
 */
sealed interface TimeBehavior {
    data object Success : TimeBehavior
    data object ClockSkipForward : TimeBehavior
    data object ClockSkipBackward : TimeBehavior
    data object FrozenClock : TimeBehavior
    data class HighJitter(val offsetMillis: Long = 500) : TimeBehavior
    data class ExtremeDrift(val driftMillis: Long = 10000) : TimeBehavior
    data object Expiration : TimeBehavior
    data object RetryDeadlineExceeded : TimeBehavior
    data object BoundaryAtExpiration : TimeBehavior
    data object JustBeforeExpiration : TimeBehavior
    data object JustAfterExpiration : TimeBehavior
    data object ClockSkew : TimeBehavior
    data object LongRunningOperation : TimeBehavior
}
