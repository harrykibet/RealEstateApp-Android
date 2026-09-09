package com.estatia.realestate.apps.core.player_engine

import javax.inject.Singleton

/**
 * Trigger for ConfinementDetector (LAW-014)
 */
@Singleton
class CanaryPlayerEngine {
    // [CANARY:POSITIVE:MissingConcurrencyCheck] [CANARY:POSITIVE:MissingVisibilityModifier]
    fun play() {
        // Violation: Missing Confinement.checkMainThread()
    }
}
