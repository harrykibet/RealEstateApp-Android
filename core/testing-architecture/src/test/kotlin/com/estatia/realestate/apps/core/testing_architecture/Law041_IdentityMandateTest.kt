package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

/**
 * LAW-041: Mandatory Architectural Identity.
 * 
 * authoritatively enforces that EVERY class in the project (except foundations)
 * declares its identity via annotations. This ensures that semantic detectors 
 * see the entire codebase, and no logic bypasses the safety system.
 */
class Law041_IdentityMandateTest {

    private val recognizedArchitecturalAnnotations = setOf(
        "Repository", "Service", "DataSource", "ViewModelMarker", "UseCase", "Manager", 
        "ChaosComponent", "Coordinator", "Helper", "ErrorMapper", "DomainModel", 
        "EntityModel", "AppEntryPoint", "UiState"
    )

    private val foundationModules = setOf(
        "core/architecture",
        "core/ksp-architecture",
        "core/testing-architecture",
        "core/canary-violations",
        "lint",
        "build-logic"
    )

    @Test
    fun `every class in the codebase must have a formal architectural identity`() {
        Konsist.scopeFromProject()
            .classes()
            // 1. Only check top-level classes (Inner classes share parent identity)
            .filter { it.isTopLevel }
            // 2. Exclude foundation and testing support modules
            .filterNot { clazz -> 
                val path = clazz.path.replace("\\", "/")
                foundationModules.any { path.contains(it) } ||
                path.contains("/test/") || 
                path.contains("/androidTest/") ||
                path.contains("/testFixtures/")
            }
            // 3. Enforce identity via annotations
            .assertTrue(
                additionalMessage = """
                    |
                    |WHAT: Anonymous component detected. Every class must declare its role.
                    |WHY: ${Law.LAW_041.rationale}
                    |GOVERNANCE: Naming convention or logic-less status does not exempt a component.
                    |
                    |[LAW: ${Law.LAW_041.id} | RISK: ${Law.LAW_041.risk.name} | CONFIDENCE: ${Law.LAW_041.confidence.name}]
                """.trimMargin()
            ) { clazz ->
                // Check if class has any of the recognized architectural annotations
                clazz.hasAnnotation { ann -> 
                    recognizedArchitecturalAnnotations.contains(ann.name)
                }
            }
    }

    @Test
    fun `naming convention must be backed by matching annotation`() {
        Konsist.scopeFromProject()
            .classes()
            .filterNot { it.path.contains("/annotations/") }
            .filterNot { it.path.contains("/test/") || it.path.contains("/androidTest/") || it.path.contains("/testFixtures/") }
            .filter { it.name.endsWith("Repository") || it.name.endsWith("Service") || it.name.endsWith("UseCase") || it.name.endsWith("ViewModel") }
            .filterNot { foundationModules.any { module -> it.path.contains(module) } }
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
