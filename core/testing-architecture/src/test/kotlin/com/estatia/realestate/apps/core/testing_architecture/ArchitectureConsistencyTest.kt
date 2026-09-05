package com.estatia.realestate.apps.core.testing_architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.KoModifier
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ArchitectureConsistencyTest {

    @Test
    fun `viewmodels must not reference infrastructure libraries`() {
        // LAW-031
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
        // LAW-031
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

    @Test
    fun `package names must match module structure`() {
        // LAW-004
        Konsist.scopeFromProject()
            .files
            .assertTrue { file ->
                val path = file.path.replace("\\", "/")
                val coreMatch = "/core/([^/]+)/".toRegex().find(path)
                val featureMatch = "/feature/([^/]+)/".toRegex().find(path)
                
                val (layer, moduleName) = when {
                    coreMatch != null -> "core" to coreMatch.groupValues[1]
                    featureMatch != null -> "feature" to featureMatch.groupValues[1]
                    else -> return@assertTrue true
                }
                
                val packageName = file.packagee?.name ?: ""
                val expected = "com.estatia.realestate.apps.$layer.${moduleName.replace("-", "_")}"
                val expectedPlain = "com.estatia.realestate.apps.$layer.${moduleName.replace("-", "")}"
                
                packageName.startsWith(expected) || packageName.startsWith(expectedPlain)
            }
    }

    @Test
    fun `feature modules must not depend on database or network implementation`() {
        // LAW-003
        Konsist.scopeFromProject()
            .files
            .assertTrue { file ->
                val isFeature = (file.packagee?.name ?: "").contains(".feature.")
                if (!isFeature) return@assertTrue true

                file.imports.none { import ->
                    import.name.contains(".core.database") || 
                    import.name.contains(".core.network") ||
                    import.name.contains(".core.datastore") ||
                    import.name.contains("com.google.firebase")
                }
            }
    }

    @Test
    fun `public api must not expose mutable containers or implementation types`() {
        // LAW-008 and LAW-016
        val forbiddenTypes = setOf(
            "MutableList", "MutableMap", "MutableSet", 
            "ArrayList", "HashMap", "HashSet",
            "MutableStateFlow", "MutableSharedFlow", "MutableState"
        )

        Konsist.scopeFromProject()
            .classes()
            .assertTrue { clazz ->
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

    @Test
    fun `complexity budget compliance`() {
        // LAW-028, LAW-029, LAW-030
        Konsist.scopeFromProject()
            .classes()
            .filterNot { ArchitecturalPolicy.TechnicalDebt.ComplexityBudget.contains(it.name) }
            .assertTrue { clazz ->
                // LAW-029: Class Size
                val classSizeOk = clazz.text.lines().size < 1000
                
                // LAW-030: Constructor Dependencies
                val constructorOk = clazz.constructors.all { it.parameters.size < 9 }
                
                // LAW-028: Method Size
                val methodsOk = clazz.functions().all { it.text.lines().size < 300 }
                
                classSizeOk && constructorOk && methodsOk
            }
    }
}
