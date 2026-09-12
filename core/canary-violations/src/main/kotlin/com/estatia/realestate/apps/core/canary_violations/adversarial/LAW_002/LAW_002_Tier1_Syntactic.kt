package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_002

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.estatia.realestate.apps.core.architecture.annotations.Identity

/**
 * LAW-002: Tier 1 - Syntactic Bypasses
 * Tests basic pattern matching and standard public exposure.
 */

@Identity.ViewModelMarker
public class SyntacticExposedViewModel : ViewModel() {
    // [CANARY:POSITIVE:ExposedMutableState:BLOCK:mutable state]
    public val exposedState: MutableStateFlow<Int> = MutableStateFlow(0)
    
    // [CANARY:NEGATIVE:ExposedMutableState]
    private val privateState: MutableStateFlow<Int> = MutableStateFlow(0)
}
