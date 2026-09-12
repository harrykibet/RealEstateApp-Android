package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_002

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.estatia.realestate.apps.core.architecture.annotations.Identity

/**
 * LAW-002: Tier 1 - Syntactic Bypasses
 */

@Identity.ViewModelMarker
public class SyntacticExposedViewModel : ViewModel() {
    // [CANARY:POSITIVE:ExposedMutableState:BLOCK:mutable state]
    public val exposedState: MutableStateFlow<Int> = MutableStateFlow(0)
    
    public fun check() {
        // [CANARY:NEGATIVE:ExposedMutableState]
        val privateState: MutableStateFlow<Int> = MutableStateFlow(0)
        println(privateState.value)
    }
}
