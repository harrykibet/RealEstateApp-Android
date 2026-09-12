package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_027

import androidx.compose.runtime.Composable
import com.estatia.realestate.apps.core.architecture.annotations.Identity
import com.estatia.realestate.apps.core.architecture.annotations.Ui

/**
 * LAW-027: Tier 3 - Dynamic Bypasses
 */

@Identity.Repository
public class SecretRepository {
    public fun action() {}
}

@Composable
@Ui.UiScreen
public fun DynamicReflectionLeakage(repo: SecretRepository) {
    // [CANARY:POSITIVE:ReflectionBypass:BLOCK:getDeclaredMethod]
    val method = repo.javaClass.getDeclaredMethod("action")
    
    // [CANARY:POSITIVE:ReflectionBypass:BLOCK:invoke]
    method.invoke(repo)
}
