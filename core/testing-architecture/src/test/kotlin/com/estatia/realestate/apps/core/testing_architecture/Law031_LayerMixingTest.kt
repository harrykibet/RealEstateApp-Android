package com.estatia.realestate.apps.core.testing_architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class Law031_LayerMixingTest {

    @Test
    fun `viewmodels must not reference infrastructure libraries`() {
        Konsist.scopeFromProject()
            .files
            .assertTrue { file ->
                val hasViewModel = file.classes().any { it.name.endsWith("ViewModel") }
                if (!hasViewModel) return@assertTrue true
                
                file.imports.none { import ->
                    ArchitecturalPolicy.InfrastructurePackages.any { import.name.contains(it) }
                }
            }
    }

    @Test
    fun `business logic components must not reference android view or compose`() {
        Konsist.scopeFromProject()
            .files
            .assertTrue { file ->
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
