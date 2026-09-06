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
            .assertTrue(additionalMessage = "${Law.LAW_032.id}: ${Law.LAW_032.description}") { file ->
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
            .filterNot { it.path.contains("canary-violations") }
            .withPackage(ArchitecturalPolicy.Layers.Model.packagePattern)
            .assertTrue(additionalMessage = "${Law.LAW_032.id}: ${Law.LAW_032.description}") { file ->
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
}
