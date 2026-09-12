package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_005

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope

/**
 * LAW-005: Tier 1 - Syntactic Bypasses
 */

public class SyntacticScopeAdversary {
    // [CANARY:POSITIVE:ForbiddenCoroutineScope:BLOCK:Forbidden]
    public val scope = CoroutineScope(Dispatchers.IO)
    
    // [CANARY:POSITIVE:ForbiddenCoroutineScope:BLOCK:Forbidden]
    public val mainScope = MainScope()
}
