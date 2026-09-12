package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_002

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.estatia.realestate.apps.core.architecture.annotations.Identity

/**
 * LAW-002: Tier 2 - Semantic Bypasses
 */

public typealias HiddenMutableState<T> = MutableStateFlow<T>
public class FakeMutableStateFlow<T>(public val value: T) 

@Identity.ViewModelMarker
public class SemanticExposedViewModel : ViewModel() {
    // [CANARY:POSITIVE:ExposedMutableState:BLOCK:mutable state]
    public val aliasedState: HiddenMutableState<Int> = MutableStateFlow(0)

    // [CANARY:POSITIVE:ExposedMutableState:BLOCK:mutable state]
    public val delegatedState: MutableStateFlow<Int> by lazy { MutableStateFlow(0) }

    public fun checkShadowing() {
        // [CANARY:NEGATIVE:ExposedMutableState]
        val shadowedState: FakeMutableStateFlow<Int> = FakeMutableStateFlow(0)
        println(shadowedState.value)
    }
}
