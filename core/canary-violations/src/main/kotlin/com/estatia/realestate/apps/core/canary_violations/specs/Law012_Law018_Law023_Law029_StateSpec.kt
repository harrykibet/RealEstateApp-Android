package com.estatia.realestate.apps.core.canary_violations.specs

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.estatia.realestate.apps.core.architecture.annotations.Identity.ViewModelMarker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import java.util.concurrent.ConcurrentHashMap

/**
 * State and Concurrency Enforcement [CANARY:POSITIVE:GodObjectFatal:WARN:God Objects]
 */

@ViewModelMarker
public class PositiveStateViewModel : ViewModel() {
    
    public val s1: StateFlow<Int> = MutableStateFlow(0)
    
    // KSP RULE: [CANARY:POSITIVE:ViewModelSsot]
    public val s2: StateFlow<String> = MutableStateFlow("")
    
    // [CANARY:POSITIVE:ExposedMutableState:BLOCK:Exposing mutable state] [CANARY:POSITIVE:BackingPropertyConvention:INFO:backing property]
    public val mutableState: MutableStateFlow<Int> = MutableStateFlow(0)
    
    // [CANARY:POSITIVE:ThreadSafetyViolation:BLOCK:Unsafe collection]
    public var unsafeMap: HashMap<String, String> = HashMap()
    
    // [CANARY:POSITIVE:LifecycleLeak:BLOCK:lifecycle-bound type]
    public var leakedActivity: Activity? = null
    
    // [CANARY:POSITIVE:UnsafeStateCollection:BLOCK:State containers inside collections]
    public val collectedState: List<MutableStateFlow<Int>> = listOf(MutableStateFlow(0))
    
    // God Object State Trigger (LAW-029)
    public var m1: Int = 0; public var m2: Int = 0; public var m3: Int = 0; public var m4: Int = 0; public var m5: Int = 0; public var m6: Int = 0
    public var m7: Int = 0; public var m8: Int = 0; public var m9: Int = 0; public var m10: Int = 0; public var m11: Int = 0; public var m12: Int = 0; public var m13: Int = 0
    
    public fun mutate() {
        unsafeMap.put("a", "b")
    }
}

@ViewModelMarker
public class NegativeStateViewModel : ViewModel() {
    // [CANARY:NEGATIVE:ViewModelSsot]
    public val uiState: StateFlow<Int> = MutableStateFlow(0)
    
    // [CANARY:NEGATIVE:ExposedMutableState] [CANARY:NEGATIVE:BackingPropertyConvention]
    private val _internalState = MutableStateFlow(0)
    
    // [CANARY:NEGATIVE:ThreadSafetyViolation]
    private val safeMap = ConcurrentHashMap<String, String>()
    
    // [CANARY:NEGATIVE:ThreadSafetyViolation]
    private val readOnlyMap = mapOf("a" to "b")
}
