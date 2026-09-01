package com.estatia.realestate.apps.core.testing.chaos.lifecycle

import com.estatia.realestate.apps.core.testing.chaos.resources.ChaosResourceController
import java.io.IOException
import java.util.concurrent.atomic.AtomicReference

/**
 * Controller for simulating lifecycle-related chaos (process death, screen destruction, etc.)
 */
class LifecycleChaosController(
    private val resourceController: ChaosResourceController? = null
) {
    
    private val nextBehavior = AtomicReference<LifecycleBehavior>(LifecycleBehavior.Success)
    private val eventHistory = java.util.concurrent.ConcurrentLinkedQueue<LifecycleBehavior>()
    
    fun setNextBehavior(behavior: LifecycleBehavior) {
        nextBehavior.set(behavior)
    }
    
    /**
     * Checks if a lifecycle-related failure should be injected or state toggled.
     * Often called at the start or end of an operation.
     */
    fun checkChaos() {
        val behavior = nextBehavior.getAndSet(LifecycleBehavior.Success)
        eventHistory.add(behavior)
        
        when (behavior) {
            LifecycleBehavior.ProcessDeath -> throw IOException("Simulated Process Death (Chaos)")
            LifecycleBehavior.ViewModelCleared -> throw IllegalStateException("ViewModel already cleared (Chaos)")
            LifecycleBehavior.DependencyDisposed -> throw IllegalStateException("Dependency already disposed (Chaos)")
            LifecycleBehavior.ScreenDestructionDuringOp -> throw IllegalStateException("Screen destroyed (Chaos)")
            
            // --- State Transitions (Toggled in ResourceController) ---
            LifecycleBehavior.AppBackgrounded -> {
                resourceController?.isAppVisible = false
            }
            LifecycleBehavior.AppForegrounded -> {
                resourceController?.isAppVisible = true
            }
            LifecycleBehavior.NavigationAway -> {
                resourceController?.isInteractive = false
            }
            LifecycleBehavior.NavigationBack -> {
                resourceController?.isInteractive = true
            }
            LifecycleBehavior.ConfigurationChange -> {
                // Simulates the momentary "not interactive" state during rotation
                resourceController?.isInteractive = false
                // Note: In real Android, this would be followed by restoration
            }
            LifecycleBehavior.Success -> Unit
        }
    }

    /**
     * Returns true if the given [behavior] was ever triggered.
     */
    fun hasTriggered(behavior: LifecycleBehavior): Boolean = eventHistory.contains(behavior)

    fun reset() {
        nextBehavior.set(LifecycleBehavior.Success)
        eventHistory.clear()
    }
}
