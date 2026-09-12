package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_008

import com.estatia.realestate.apps.core.architecture.annotations.Identity
import java.util.ArrayList

/**
 * LAW-008: Tier 3 - Dynamic Bypasses
 */

@Identity.Repository
public class DynamicAbstractionAdversary {
    // [CANARY:POSITIVE:ImplementationTypeInPublicApi:BLOCK:ArrayList]
    // Hiding ArrayList behind Any
    public fun getErasedData(): Any = ArrayList<String>()
}
