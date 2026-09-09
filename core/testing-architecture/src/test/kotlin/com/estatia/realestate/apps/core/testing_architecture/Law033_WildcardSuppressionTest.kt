package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

/**
 * LAW-033: Suppression Policy Enforcement.
 * 
 * authoritatively forbids wildcard suppression (e.g. @Suppress("all")) in Kotlin source.
 */
class Law033_WildcardSuppressionTest {

    @Test
    fun `kotlin files must not use wildcard suppression`() {
        Konsist.scopeFromProject()
            .files
            // Exempt canary module and any architectural verification tests that might contain strings for matching.
            .filterNot { it.path.contains("canary-violations") || it.path.contains("Law033_WildcardSuppressionTest") }
            .assertTrue(additionalMessage = "${Law.LAW_033.id} [Fidelity: ${Law.LAW_033.primaryFidelity.name}]: ${Law.LAW_033.description}. Wildcard suppression ('all') is forbidden.") { file ->
                val content = file.text
                val hasWildcard = content.contains("""Suppress("all")""", ignoreCase = true) ||
                                 content.contains("""Suppress(names = ["all"])""", ignoreCase = true) ||
                                 content.contains("""SuppressLint("all")""", ignoreCase = true) ||
                                 content.contains("""SuppressLint(value = ["all"])""", ignoreCase = true)
                
                !hasWildcard
            }
    }
}
