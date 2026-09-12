package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_002

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.estatia.realestate.apps.core.architecture.annotations.Identity

/**
 * LAW-002: Tier 3 - Dynamic Bypasses
 * Tests type erasure and reflection-based state extraction.
 */

@Identity.ViewModelMarker
public class DynamicExposedViewModel : ViewModel() {
    // [CANARY:POSITIVE:ExposedMutableState:BLOCK:mutable state]
    // Hidden via Any erasure
    public fun getSecretState(): Any = MutableStateFlow(0)
    
    private val trulyPrivate: MutableStateFlow<Int> = MutableStateFlow(0)
}

public class StateHacker(val vm: DynamicExposedViewModel) {
    public fun hack() {
        // [CANARY:POSITIVE:ReflectionBypass:BLOCK:getDeclaredField]
        val field = vm.javaClass.getDeclaredField("trulyPrivate")
        field.isAccessible = true
        val state = field.get(vm) as MutableStateFlow<*>
    }
}
