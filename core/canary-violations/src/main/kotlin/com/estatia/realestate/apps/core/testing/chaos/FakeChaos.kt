package com.estatia.realestate.apps.core.testing.chaos

annotation class ChaosComponent

/**
 * Trigger for ChaosSynchronizationDetector (LAW-012) [CANARY:POSITIVE:MissingVisibilityModifier:BLOCK:explicit visibility]
 */
@ChaosComponent
class FakeChaos {
    @JvmField // [CANARY:POSITIVE:UnsynchronizedChaosState:BLOCK:Unsynchronized Test Infrastructure]
    var chaosCounter = 0 
}
