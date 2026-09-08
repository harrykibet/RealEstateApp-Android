package com.estatia.realestate.apps.core.player_engine

import javax.inject.Singleton

/**
 * Trigger for ConfinementDetector (LAW-014)
 */
@Singleton
class CanaryPlayerEngine {
    fun play() {
        // Violation: Missing Confinement.checkMainThread()
    }
}
