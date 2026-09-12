package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_012

import com.estatia.realestate.apps.core.architecture.annotations.Identity
import java.util.ArrayList

/**
 * LAW-012: Tier 3 - Dynamic Bypasses
 */

@Identity.Manager
public class DynamicThreadSafetyAdversary {
    private val trulyPrivate: MutableList<String> = ArrayList()

    public fun hack() {
        val field = this.javaClass.getDeclaredField("trulyPrivate")
        field.isAccessible = true
        val list = field.get(this) as MutableList<String>
        
        list.add("hacked") // [CANARY:POSITIVE:ThreadSafetyViolation:BLOCK:mutated]
    }
}
