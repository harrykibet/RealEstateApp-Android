package com.estatia.realestate.apps.core.testing_architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class Law004_NamingConsistencyTest {

    @Test
    fun `package names must match module structure`() {
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
}
