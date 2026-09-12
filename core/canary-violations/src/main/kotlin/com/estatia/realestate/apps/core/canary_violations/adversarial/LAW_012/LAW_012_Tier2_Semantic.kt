package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_012

import com.estatia.realestate.apps.core.architecture.annotations.Identity
import java.util.ArrayList

/**
 * LAW-012: Tier 2 - Semantic Bypasses
 */

public class HashMap<K, V> // Safe dummy

@Identity.Manager
public class SemanticThreadSafetyAdversary {
    // [CANARY:NEGATIVE:ThreadSafetyViolation]
    public val safeFakeMap = HashMap<String, Int>()

    private val unsafeList: MutableList<String> = ArrayList()

    public fun mutate() {
        // [CANARY:POSITIVE:ThreadSafetyViolation:BLOCK:ArrayList]
        unsafeList += "hacked"
        
        // [CANARY:POSITIVE:ThreadSafetyViolation:BLOCK:ArrayList]
        unsafeList.apply { add("also hacked") }
    }
}
