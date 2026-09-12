package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_012

import com.estatia.realestate.apps.core.architecture.annotations.Identity
import java.util.HashMap

/**
 * LAW-012: Tier 1 - Syntactic Bypasses
 */

@Identity.Manager
public class SyntacticThreadSafetyAdversary {
    // [CANARY:POSITIVE:ThreadSafetyViolation:BLOCK:HashMap]
    public var unsafeCache = HashMap<String, Int>()
}
