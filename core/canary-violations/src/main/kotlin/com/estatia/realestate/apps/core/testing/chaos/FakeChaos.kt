package com.estatia.realestate.apps.core.testing.chaos

annotation class ChaosComponent

/**
 * Trigger for ChaosSynchronizationDetector (LAW-012) [CANARY:POSITIVE:MissingVisibilityModifier:BLOCK:Explicit visibility]
 */
@ChaosComponent
class FakeChaos {
    @JvmField // [CANARY:POSITIVE:UnsynchronizedChaosState:BLOCK:Chaos/Fake component state]
    var chaosCounter = 0 
}
