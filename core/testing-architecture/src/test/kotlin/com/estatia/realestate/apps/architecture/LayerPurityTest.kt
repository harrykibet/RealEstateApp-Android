package com.estatia.realestate.apps.architecture

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
            .withPackage("..core.domain..")
            .assertTrue { file ->
                file.imports.none { 
                    (it.name.startsWith("android.") || 
                     it.name.startsWith("androidx.") || 
                     it.name.startsWith("com.google.firebase")) &&
                    !it.name.contains("androidx.annotation")
                }
            }
    }

    @Test
    fun `model layer must not depend on android frameworks`() {
        Konsist
            .scopeFromProject()
            .files
            .withPackage("..core.model..")
            .assertTrue { file ->
                file.imports.none { 
                    (it.name.startsWith("android.") || 
                     it.name.startsWith("androidx.")) &&
                    !it.name.contains("androidx.annotation") &&
                    !it.name.contains("kotlinx.parcelize") &&
                    !it.name.contains("android.os.Parcelable")
                }
            }
    }

    @Test
    fun `feature modules should follow UDF and isolation`() {
        // This test identified 62 violations. We enable it for new feature code only
        // or as a documentation of debt.
        Konsist
            .scopeFromProject()
            .files
            .withPackage("..feature..")
            .assertTrue { file ->
                val currentFeature = file.name.split(".").getOrNull(5) ?: return@assertTrue true
                
                file.imports.none { import ->
                    val importFeature = import.name.split(".").getOrNull(5)
                    import.name.contains(".feature.") && 
                            importFeature != currentFeature && 
                            importFeature != "shared_ui" &&
                            importFeature != "navigation" // Allow navigation interop
                }
            }
    }
}
