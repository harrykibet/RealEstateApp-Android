package com.estatia.realestate.apps.core.testing_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class Law003_FeatureIsolationTest {

    @Test
    fun `feature modules should follow UDF and isolation`() {
        Konsist
            .scopeFromProject()
            .files
            .filterNot { it.path.contains("canary-violations") }
            .withPackage(ArchitecturalPolicy.Layers.Feature.packagePattern)
            .filterNot { ArchitecturalPolicy.TechnicalDebt.FeatureIsolation.contains(it.nameWithExtension) }
            .assertTrue(additionalMessage = "${Law.LAW_004.id}: ${Law.LAW_004.description}") { file ->
                val packageName = file.packagee?.name ?: return@assertTrue true
                val packageParts = packageName.split(".")
                val featureIndex = packageParts.indexOf("feature")
                if (featureIndex == -1 || featureIndex + 1 >= packageParts.size) return@assertTrue true
                
                val currentFeature = packageParts[featureIndex + 1]
                
                file.imports.none { import ->
                    val importParts = import.name.split(".")
                    val importFeatureIndex = importParts.indexOf("feature")
                    
                    if (importFeatureIndex != -1 && importFeatureIndex + 1 < importParts.size) {
                        val importFeature = importParts[importFeatureIndex + 1]
                        importFeature != currentFeature && 
                                importFeature != "shared_ui" &&
                                importFeature != "navigation"
                    } else {
                        false
                    }
                }
            }
    }

    @Test
    fun `feature modules must not depend on database or network implementation`() {
        Konsist.scopeFromProject()
            .files
            .filterNot { it.path.contains("canary-violations") }
            .assertTrue(additionalMessage = "${Law.LAW_003.id}: ${Law.LAW_003.description}") { file ->
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
}
