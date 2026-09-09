package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class Law031_LayerMixingTest {

    /**
     * NOTE: This check relies on import string analysis.
     * Limitations: Star imports or FQN usage without imports will be missed.
     */
    @Test
    fun `viewmodels must not reference infrastructure libraries`() {
        Konsist.scopeFromProject()
            .files
            .filterNot { it.path.contains("canary-violations") }
            .assertTrue(additionalMessage = "${Law.LAW_031.id} [Fidelity: ${Law.LAW_031.primaryFidelity.name}]: ${Law.LAW_031.description}") { file ->
                val hasViewModel = file.classes().any { it.name.endsWith("ViewModel") }
                if (!hasViewModel) return@assertTrue true
                
                file.imports.none { import ->
                    ArchitecturalPolicy.InfrastructurePackages.any { import.name.contains(it) }
                }
            }
    }

    /**
     * NOTE: This check relies on import string analysis.
     * Limitations: Star imports or FQN usage without imports will be missed.
     */
    @Test
    fun `business logic components must not reference android view or compose`() {
        Konsist.scopeFromProject()
            .files
            .filterNot { it.path.contains("canary-violations") }
            .assertTrue(additionalMessage = "${Law.LAW_031.id} [Fidelity: ${Law.LAW_031.primaryFidelity.name}]: ${Law.LAW_031.description}") { file ->
                val isBusinessLogic = file.classes().any { clazz ->
                    clazz.name.endsWith("Repository") || 
                    clazz.name.endsWith("UseCase") || 
                    clazz.name.endsWith("Service")
                }
                if (!isBusinessLogic) return@assertTrue true
                
                file.imports.none { import ->
                    import.name.contains("androidx.compose") ||
                    import.name.contains("android.view") ||
                    import.name.contains("android.widget")
                }
            }
    }
}
