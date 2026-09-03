package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UFile

/**
 * Ensures that every Kotlin/Java file has a package name that matches its module location.
 * Expected pattern: com.estatia.realestate.apps.[layer].[module]
 */
class PackageBoundaryDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UFile::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitFile(node: UFile) {
                val packageName = node.packageName
                if (packageName.isBlank()) return

                val projectDir = context.project.dir.absolutePath.replace("\\", "/")
                
                val coreIndex = projectDir.lastIndexOf("/core/")
                val featureIndex = projectDir.lastIndexOf("/feature/")
                
                val layer: String
                val module: String
                
                if (coreIndex != -1) {
                    layer = "core"
                    module = projectDir.substring(coreIndex + 6)
                } else if (featureIndex != -1) {
                    layer = "feature"
                    module = projectDir.substring(featureIndex + 9)
                } else {
                    return
                }

                val moduleName = module.split("/").first()
                val expectedWithUnderscore = "com.estatia.realestate.apps.$layer.${moduleName.replace("-", "_")}"
                val expectedPlain = "com.estatia.realestate.apps.$layer.${moduleName.replace("-", "")}"
                
                if (!packageName.startsWith(expectedWithUnderscore) && !packageName.startsWith(expectedPlain)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Package name '$packageName' must start with either '$expectedWithUnderscore' or '$expectedPlain' to match module location ($layer:$moduleName)."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "PackageBoundaryViolation",
            description = "Package name must match module location",
            explanation = """
                Estatia follows a strict one-to-one mapping between module paths and package names.
                A file in ':core:network' must have a package starting with 'com.estatia.realestate.apps.core.network'.
                This ensures predictability and prevents naming collisions.
            """,
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(PackageBoundaryDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
