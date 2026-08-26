package com.estatia.realestate.apps.core.testing.chaos.lifecycle

import java.io.IOException

/**
 * Controller for simulating lifecycle-related chaos (process death, screen destruction, etc.)
 */
class LifecycleChaosController {
    
    private var nextBehavior: LifecycleBehavior = LifecycleBehavior.Success
    private val eventHistory = mutableListOf<LifecycleBehavior>()
    
    fun setNextBehavior(behavior: LifecycleBehavior) {
        nextBehavior = behavior
    }
    
    /**
     * Checks if a lifecycle-related failure should be injected.
     * Often called at the start or end of an operation.
     */
    fun checkChaos() {
        val behavior = nextBehavior
        eventHistory.add(behavior)
        
        when (behavior) {
            LifecycleBehavior.ProcessDeath -> throw IOException("Simulated Process Death (Chaos)")
            LifecycleBehavior.ViewModelCleared -> throw IllegalStateException("ViewModel already cleared (Chaos)")
            LifecycleBehavior.DependencyDisposed -> throw IllegalStateException("Dependency already disposed (Chaos)")
            LifecycleBehavior.ScreenDestructionDuringOp -> throw IllegalStateException("Screen destroyed (Chaos)")
            else -> Unit
        }
        
        if (behavior != LifecycleBehavior.Success) {
            nextBehavior = LifecycleBehavior.Success // One-shot for failures
        }
    }

    /**
     * Returns true if the given [behavior] was ever triggered.
     */
    fun hasTriggered(behavior: LifecycleBehavior): Boolean = eventHistory.contains(behavior)

    fun reset() {
        nextBehavior = LifecycleBehavior.Success
        eventHistory.clear()
    }
}
