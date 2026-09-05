package com.estatia.realestate.apps.core.testing_architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.imports
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class LayerPurityTest {

    @Test
    fun `domain layer must not depend on android frameworks`() {
        Konsist
            .scopeFromProject()
            .files
            .withPackage(ArchitecturalPolicy.Layers.Domain.packagePattern)
            .assertTrue { file ->
                file.imports.none { import ->
                    val isForbidden = ArchitecturalPolicy.InfrastructurePackages.any { import.name.startsWith(it) } ||
                                     import.name.startsWith("android.") ||
                                     import.name.startsWith("androidx.")
                    
                    isForbidden && !import.name.contains("androidx.annotation")
                }
            }
    }

    @Test
    fun `model layer must not depend on android frameworks`() {
        Konsist
            .scopeFromProject()
            .files
            .withPackage(ArchitecturalPolicy.Layers.Model.packagePattern)
            .assertTrue { file ->
                file.imports.none { import ->
                    val isForbidden = import.name.startsWith("android.") || 
                                     import.name.startsWith("androidx.")
                    
                    isForbidden && 
                    !import.name.contains("androidx.annotation") &&
                    !import.name.contains("kotlinx.parcelize") &&
                    !import.name.contains("android.os.Parcelable")
                }
            }
    }

    @Test
    fun `feature modules should follow UDF and isolation`() {
        Konsist
            .scopeFromProject()
            .files
            .withPackage(ArchitecturalPolicy.Layers.Feature.packagePattern)
            .filterNot { ArchitecturalPolicy.TechnicalDebt.FeatureIsolation.contains(it.nameWithExtension) }
            .assertTrue { file ->
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
                                importFeature != "navigation" // Allow navigation interop
                    } else {
                        false
                    }
                }
            }
    }
}
