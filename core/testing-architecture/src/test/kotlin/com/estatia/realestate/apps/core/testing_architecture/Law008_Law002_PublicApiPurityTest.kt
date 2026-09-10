package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class Law008_Law002_PublicApiPurityTest {

    /**
     * NOTE: This is a syntactic check based on type name strings.
     * Limitations: 
     * - Over-firing: 'UserHashMapAdapter' would be flagged because it contains 'HashMap'.
     * - Under-firing: FQN used inline or typealiases may evade this check.
     */
    @Test
    fun `public api must not expose mutable containers or implementation types`() {
        // LAW-008 and LAW-002
        val forbiddenTypes = setOf(
            "MutableList", "MutableMap", "MutableSet", 
            "ArrayList", "HashMap", "HashSet",
            "MutableStateFlow", "MutableSharedFlow", "MutableState"
        )

        Konsist.scopeFromProject()
            .classes()
            .filterNot { it.path.contains("canary-violations") }
            .assertTrue(
                additionalMessage = """
                    |
                    |WHAT: Public API exposes mutable containers or implementation types.
                    |WHY: ${Law.LAW_008.rationale} (and ${Law.LAW_002.rationale})
                    |RECOMMENDED: ${Law.LAW_008.recommendation}
                    |
                    |[LAW: ${Law.LAW_008.id} | RISK: ${Law.LAW_008.risk.name} | CONFIDENCE: ${Law.LAW_008.confidence.name}]
                """.trimMargin()
            ) { clazz ->
                val publicProps = clazz.properties(includeNested = true).filter { it.hasModifier(KoModifier.PUBLIC) }
                val publicFuncs = clazz.functions(includeNested = true).filter { it.hasModifier(KoModifier.PUBLIC) }
                
                val propLeak = publicProps.any { prop ->
                    forbiddenTypes.any { prop.type?.name?.contains(it) == true } ||
                    ArchitecturalPolicy.InfrastructurePackages.any { prop.type?.name?.startsWith(it) == true }
                }
                
                val funcLeak = publicFuncs.any { func ->
                    forbiddenTypes.any { func.returnType?.name?.contains(it) == true } ||
                    ArchitecturalPolicy.InfrastructurePackages.any { func.returnType?.name?.startsWith(it) == true } ||
                    func.parameters.any { param ->
                        forbiddenTypes.any { param.type.name.contains(it) } ||
                        ArchitecturalPolicy.InfrastructurePackages.any { param.type.name.startsWith(it) }
                    }
                }
                
                !propLeak && !funcLeak
            }
    }
}
