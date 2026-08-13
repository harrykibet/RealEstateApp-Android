package com.estatia.realestate.apps.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UFile

/**
 * Ensures that every Kotlin/Java file has a package name that matches its module location.
 * Expected pattern: com.estatia.realestate.apps.[layer].[module]
 */
class ModulePackageDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UFile::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitFile(node: UFile) {
                val packageName = node.packageName
                if (packageName.isBlank()) return

                // Get relative path of the file from project root
                val projectDir = context.project.dir.absolutePath
                val fileDir = context.file.absolutePath
                val relativePath = fileDir.removePrefix(projectDir).replace("\\", "/")

                // Extract module info (e.g. /core/network/src/main/...)
                val pathSegments = relativePath.split("/").filter { it.isNotBlank() }
                if (pathSegments.size < 2) return

                val layer = pathSegments[0] // e.g. "core"
                val module = pathSegments[1] // e.g. "network"
                
                // We only enforce this for our internal core/feature layers
                if (layer != "core" && layer != "feature") return

                val expectedPrefix = "com.estatia.realestate.apps.$layer.${module.replace("-", "_")}"
                
                if (!packageName.startsWith(expectedPrefix)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Package name '$packageName' must start with '$expectedPrefix' to match module location."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = Issue.create(
            id = "ModulePackageMismatch",
            briefDescription = "Package name must match module location",
            explanation = """
                Estatia follows a strict one-to-one mapping between module paths and package names.
                A file in ':core:network' must have a package starting with 'com.estatia.realestate.apps.core.network'.
                This ensures predictability and prevents naming collisions.
            """,
            category = Category.CORRECTNESS,
            priority = 8,
            severity = Severity.ERROR,
            implementation = Implementation(ModulePackageDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
