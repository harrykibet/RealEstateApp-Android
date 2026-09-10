package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class Law032_DomainPurityTest {

    @Test
    fun `domain layer must not depend on android frameworks`() {
        Konsist
            .scopeFromProject()
            .files
            .filterNot { it.path.contains("canary-violations") }
            .withPackage(ArchitecturalPolicy.Layers.Domain.packagePattern)
            .assertTrue(
                additionalMessage = """
                    |
                    |WHAT: Domain layer depending on Android/Infrastructure frameworks.
                    |WHY: ${Law.LAW_032.rationale}
                    |RECOMMENDED: ${Law.LAW_032.recommendation}
                    |
                    |[LAW: ${Law.LAW_032.id} | RISK: ${Law.LAW_032.risk.name} | CONFIDENCE: ${Law.LAW_032.confidence.name}]
                """.trimMargin()
            ) { file ->
                file.imports.none { import ->
                    val isForbidden = ArchitecturalPolicy.Law003.InfrastructurePackages.any { import.name.startsWith(it) } ||
                                     import.name.startsWith("android.") ||
                                     import.name.startsWith("androidx.")
                    
                    isForbidden && ArchitecturalPolicy.Layers.Domain.allowed.none { allowed -> import.name.startsWith(allowed) }
                }
            }
    }

    @Test
    fun `model layer must not depend on android frameworks`() {
        Konsist
            .scopeFromProject()
            .files
            .filterNot { it.path.contains("canary-violations") }
            .withPackage(ArchitecturalPolicy.Layers.Model.packagePattern)
            .assertTrue(
                additionalMessage = """
                    |
                    |WHAT: Model layer depending on Android frameworks.
                    |WHY: ${Law.LAW_032.rationale}
                    |RECOMMENDED: ${Law.LAW_032.recommendation}
                    |
                    |[LAW: ${Law.LAW_032.id} | RISK: ${Law.LAW_032.risk.name} | CONFIDENCE: ${Law.LAW_032.confidence.name}]
                """.trimMargin()
            ) { file ->
                file.imports.none { import ->
                    val isForbidden = import.name.startsWith("android.") || 
                                     import.name.startsWith("androidx.")
                    
                    isForbidden && ArchitecturalPolicy.Layers.Model.allowed.none { allowed -> import.name.startsWith(allowed) }
                }
            }
    }
}
