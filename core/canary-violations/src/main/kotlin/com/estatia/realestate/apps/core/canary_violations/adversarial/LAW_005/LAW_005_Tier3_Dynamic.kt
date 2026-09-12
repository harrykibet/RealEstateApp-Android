package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_005

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Proxy
import kotlin.coroutines.CoroutineContext

/**
 * LAW-005: Tier 3 - Dynamic Bypasses
 */

public class DynamicScopeAdversary {
    public fun reflect() {
        // [CANARY:POSITIVE:ReflectionBypass:BLOCK:getDeclaredConstructor]
        val scopeClass = Class.forName("kotlinx.coroutines.ContextScope")
        val constructor = scopeClass.getDeclaredConstructor(CoroutineContext::class.java)
        constructor.isAccessible = true
        val scope = constructor.newInstance(Dispatchers.IO) as CoroutineScope
        scope.launch { }
    }
}
