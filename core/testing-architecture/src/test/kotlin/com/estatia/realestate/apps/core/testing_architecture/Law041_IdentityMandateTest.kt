package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.KoInterfaceDeclaration
import com.lemonappdev.konsist.api.declaration.KoFunctionDeclaration
import com.lemonappdev.konsist.api.declaration.KoBaseDeclaration
import org.junit.Test

/**
 * LAW-041: Mandatory Architectural Identity.
 * 
 * authoritatively enforces that EVERY class, interface, or Composable function 
 * declares its identity via annotations. This ensures that semantic detectors 
 * see the entire UI and logic layers, leaving no room for "invisible" code.
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
                path.contains("/testFixtures/") ||
                path.contains("/build/generated/") ||
                path.contains("/build/kspCaches/")
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
                decl.annotations.any { ann -> 
                    val name = ann.name.substringAfterLast(".")
                    ArchitecturalPolicy.Law041.RecognizedArchitecturalAnnotations.contains(name)
                }
            }
    }

    @Test
    fun `every composable function must have a formal architectural identity`() {
        Konsist.scopeFromProject()
            .functions()
            .filter { it.hasAnnotation { ann -> ann.name == "Composable" } }
            .filterNot { decl ->
                val path = decl.path.replace("\\", "/")
                ArchitecturalPolicy.Law041.FoundationModules.any { path.contains(it) } ||
                path.contains("/test/") || 
                path.contains("/androidTest/") ||
                path.contains("/testFixtures/") ||
                path.contains("/build/generated/") ||
                // Previews are exempt from identity mandate
                decl.hasAnnotation { ann -> ann.name == "Preview" }
            }
            .assertTrue(
                additionalMessage = "Anonymous Composable detected. Every Composable must declare its role (e.g., @UiScreen, @UiComponent, @UiPrimitiveFunction)."
            ) { decl ->
                decl.annotations.any { ann -> 
                    val name = ann.name.substringAfterLast(".")
                    ArchitecturalPolicy.Law041.RecognizedArchitecturalAnnotations.contains(name)
                }
            }
    }

    @Test
    fun `architectural identity must match structural truth`() {
        val classes = Konsist.scopeFromProject().classes()
        val interfaces = Konsist.scopeFromProject().interfaces()
        val functions = Konsist.scopeFromProject().functions()

        (classes + interfaces + functions)
            .filter { 
                if (it is KoFunctionDeclaration) it.hasAnnotation { ann -> ann.name == "Composable" }
                else it.isTopLevel
            }
            .filterNot { decl ->
                val path = decl.path.replace("\\", "/")
                // Canary violations and testing components are allowed to "pretend" for testing purposes
                path.contains("canary-violations") || path.contains("/testing/")
            }
            .assertTrue(
                additionalMessage = "Identity/Truth Mismatch: The claimed architectural role is inconsistent with the structural implementation (LAW-041)."
            ) { decl ->
                val annotations = decl.annotations.map { it.name.substringAfterLast(".") }
                val claimedRole = annotations.find { ArchitecturalPolicy.Law041.RoleInvariants.containsKey(it) }
                
                if (claimedRole == null) return@assertTrue true

                val invariant = ArchitecturalPolicy.Law041.RoleInvariants[claimedRole]!!
                val path = decl.path.replace("\\", "/")

                // 1. Path Invariant
                val pathPattern = invariant.pathContains
                if (pathPattern != null) {
                    val patterns = pathPattern.split("|")
                    if (patterns.none { path.contains(it) }) {
                        println("DEBUG: Path Truth Mismatch: ${decl.name} ($claimedRole) at $path. Expected patterns: $pathPattern")
                        return@assertTrue false
                    }
                }

                // 2. Interface Invariant
                if (invariant.mustBeInterface && decl !is KoInterfaceDeclaration) {
                    println("DEBUG: Interface Truth Mismatch: ${decl.name} ($claimedRole). Expected interface.")
                    return@assertTrue false
                }

                // 3. Data Class Invariant
                if (invariant.mustBeData && decl is KoClassDeclaration && !decl.hasDataModifier) {
                    println("DEBUG: Data Truth Mismatch: ${decl.name} ($claimedRole). Expected data class.")
                    return@assertTrue false
                }

                // 4. Data, Sealed or Value Class Invariant
                if (invariant.mustBeDataSealedOrValue && decl is KoClassDeclaration) {
                    val isValueClass = decl.hasModifier(KoModifier.VALUE) || decl.hasAnnotation { it.name == "JvmInline" }
                    if (!decl.hasDataModifier && !decl.hasSealedModifier && !isValueClass) {
                        println("DEBUG: Model Truth Mismatch: ${decl.name} ($claimedRole). Expected data/sealed/value class.")
                        return@assertTrue false
                    }
                }

                // 5. Inheritance Invariant
                val parentName = invariant.mustInheritFrom
                if (parentName != null && decl is KoClassDeclaration) {
                    val expectedParent = parentName.substringAfterLast(".")
                    val hasDirectParent = decl.hasParentWithName(expectedParent)
                    val hasIndirectParent = decl.parents().any { it.name.contains(expectedParent) }
                    
                    if (!hasDirectParent && !hasIndirectParent) {
                        println("DEBUG: Inheritance Truth Mismatch: ${decl.name} ($claimedRole). Expected parent: $expectedParent")
                        return@assertTrue false
                    }
                }

                // 6. Composable Invariant
                if (invariant.mustBeComposable && (decl !is KoFunctionDeclaration || !decl.hasAnnotation { it.name == "Composable" })) {
                    println("DEBUG: Composable Truth Mismatch: ${decl.name} ($claimedRole). Expected @Composable function.")
                    return@assertTrue false
                }

                true
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
