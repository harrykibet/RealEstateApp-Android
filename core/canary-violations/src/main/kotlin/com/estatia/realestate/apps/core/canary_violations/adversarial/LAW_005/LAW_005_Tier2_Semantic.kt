package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_005

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * LAW-005: Tier 2 - Semantic Bypasses
 */

public typealias HiddenScope = GlobalScope

public class SemanticScopeAdversary {
    public fun leak() {
        // [CANARY:POSITIVE:ForbiddenCoroutineScope:BLOCK:GlobalScope]
        HiddenScope.launch { }
    }
    
    public fun bypass() {
        // [CANARY:POSITIVE:ForbiddenCoroutineScope:BLOCK:GlobalScope]
        GlobalScope.launch { }
    }
}
