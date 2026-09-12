package com.estatia.realestate.apps.core.canary_violations.adversarial.LAW_027

import androidx.compose.runtime.Composable
import com.estatia.realestate.apps.core.architecture.annotations.Identity
import com.estatia.realestate.apps.core.architecture.annotations.Ui
import java.io.Serializable
import java.lang.reflect.Proxy

/**
 * LAW-027: Tier 2 - Semantic Bypasses
 */

@Identity.Repository
public class ReflectionRepository {
    private fun privateAction() { println("hacked") }
}

@Composable
@Ui.UiScreen
public fun SemanticReflectionLeakage(repo: ReflectionRepository) {
    // [CANARY:POSITIVE:ReflectionBypass:BLOCK:getDeclaredMethod]
    repo.javaClass.getDeclaredMethod("privateAction").invoke(repo)
    
    // [CANARY:POSITIVE:ReflectionBypass:BLOCK:newProxyInstance]
    Proxy.newProxyInstance(
        repo.javaClass.classLoader,
        arrayOf(Serializable::class.java),
        { _, _, _ -> repo.javaClass.getDeclaredMethod("privateAction").invoke(repo) }
    )
}
