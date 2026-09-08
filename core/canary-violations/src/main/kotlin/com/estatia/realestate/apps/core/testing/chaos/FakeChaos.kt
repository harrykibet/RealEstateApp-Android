package com.estatia.realestate.apps.core.testing.chaos

/**
 * Trigger for ChaosSynchronizationDetector (LAW-012)
 */
class FakeChaos {
    @JvmField
    var chaosCounter = 0 // UnsynchronizedChaosState
}
