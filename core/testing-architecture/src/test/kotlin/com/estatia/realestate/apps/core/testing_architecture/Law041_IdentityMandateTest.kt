package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import org.junit.Test

/**
 * LAW-041: Mandatory Architectural Identity.
 * 
 * authoritatively enforces that EVERY class or interface in the codebase 
 * declares its identity via annotations. This ensures that semantic detectors 
 * see the entire codebase and no contract or logic bypasses the safety system.
 */
class Law041_IdentityMandateTest {

    @Test
    fun `every class or interface in the codebase must have a formal architectural identity`() {
        val classes = Konsist.scopeFromProject().classes()
        val interfaces = Konsist.scopeFromProject().interfaces()

        (classes + interfaces)
            .filter { it.isTopLevel }
            .filterNot { decl -> 
                val path = decl.path.replace("\\", "/")
                ArchitecturalPolicy.Law041.FoundationModules.any { path.contains(it) } ||
                path.contains("/test/") || 
                path.contains("/androidTest/") ||
                path.contains("/testFixtures/")
            }
            // 3. Exclude passive types: enums, annotation classes
            .filterNot { 
                (it as? KoClassDeclaration)?.hasEnumModifier == true ||
                (it as? KoClassDeclaration)?.hasAnnotationModifier == true
            }
            // 4. Enforce identity via annotations
            .assertTrue(
                additionalMessage = """
                    |
                    |WHAT: Anonymous component detected. Every class or interface must declare its role.
                    |WHY: ${Law.LAW_041.rationale}
                    |GOVERNANCE: High-fidelity semantic checks require explicit metadata.
                    |
                    |[LAW: ${Law.LAW_041.id} | RISK: ${Law.LAW_041.risk.name} | CONFIDENCE: ${Law.LAW_041.confidence.name}]
                """.trimMargin()
            ) { decl ->
                decl.hasAnnotation { ann -> 
                    val name = ann.name.substringAfterLast(".")
                    ArchitecturalPolicy.Law041.RecognizedArchitecturalAnnotations.contains(name)
                }
            }
    }

    @Test
    fun `naming convention must be backed by matching annotation`() {
        Konsist.scopeFromProject()
            .classes()
            .filterNot { it.path.replace("\\", "/").contains("/annotations/") }
            .filterNot { it.path.replace("\\", "/").contains("/test/") || it.path.replace("\\", "/").contains("/androidTest/") || it.path.replace("\\", "/").contains("/testFixtures/") }
            .filter { it.name.endsWith("Repository") || it.name.endsWith("Service") || it.name.endsWith("UseCase") || it.name.endsWith("ViewModel") }
            .filterNot { ArchitecturalPolicy.Law041.FoundationModules.any { module -> it.path.replace("\\", "/").contains(module) } }
            .assertTrue(
                additionalMessage = "Naming convention suggests architectural role. Class must have matching annotation (LAW-041)."
            ) { clazz ->
                val expectedAnnotation = when {
                    clazz.name.endsWith("Repository") -> "Repository"
                    clazz.name.endsWith("Service") -> "Service"
                    clazz.name.endsWith("UseCase") -> "UseCase"
                    clazz.name.endsWith("ViewModel") -> "ViewModelMarker"
                    else -> ""
                }
                clazz.hasAnnotation { it.name == expectedAnnotation }
            }
    }
}
