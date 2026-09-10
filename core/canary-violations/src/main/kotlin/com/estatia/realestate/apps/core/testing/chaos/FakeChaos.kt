package com.estatia.realestate.apps.core.testing.chaos

annotation class ChaosComponent

/**
 * Trigger for ChaosSynchronizationDetector (LAW-012)
 */
@ChaosComponent
class FakeChaos {
    @JvmField
    var chaosCounter = 0 // [CANARY:POSITIVE:UnsynchronizedChaosState] [CANARY:POSITIVE:MissingVisibilityModifier]
}
