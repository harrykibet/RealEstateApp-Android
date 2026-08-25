package com.estatia.realestate.apps.core.testing.chaos.lifecycle

/**
 * Represents chaotic lifecycle scenarios for adversarial testing.
 */
sealed interface LifecycleBehavior {
    data object Success : LifecycleBehavior
    data object ScreenDestructionDuringOp : LifecycleBehavior
    data object NavigationAway : LifecycleBehavior
    data object NavigationBack : LifecycleBehavior
    data object ProcessDeath : LifecycleBehavior
    data object ConfigurationChange : LifecycleBehavior
    data object AppBackgrounded : LifecycleBehavior
    data object AppForegrounded : LifecycleBehavior
    data object ViewModelCleared : LifecycleBehavior
    data object DependencyDisposed : LifecycleBehavior
}
