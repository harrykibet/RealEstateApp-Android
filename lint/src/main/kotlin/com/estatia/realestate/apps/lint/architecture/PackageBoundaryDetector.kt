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
 */
class PackageBoundaryDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UFile::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitFile(node: UFile) {
                val packageName = node.packageName
                if (packageName.isBlank()) return

                val filePath = context.file.path.replace("\\", "/")
                
                val coreIndex = filePath.lastIndexOf("/core/")
                val featureIndex = filePath.lastIndexOf("/feature/")
                
                val layer: String
                val modulePart: String
                
                if (coreIndex != -1) {
                    layer = "core"
                    modulePart = filePath.substring(coreIndex + 6)
                } else if (featureIndex != -1) {
                    layer = "feature"
                    modulePart = filePath.substring(featureIndex + 9)
                } else {
                    return
                }

                val moduleName = modulePart.split("/").first()
                val expected = "com.estatia.realestate.apps.$layer.${moduleName.replace("-", "_")}"
                val expectedPlain = "com.estatia.realestate.apps.$layer.${moduleName.replace("-", "")}"
                
                if (!packageName.startsWith(expected) && !packageName.startsWith(expectedPlain)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Package name '$packageName' must start with '$expected' to match module location ($layer:$moduleName) (LAW-004)."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "PackageBoundaryViolation",
            description = "Package name must match module location",
            rationale = "Predictable mapping between module paths and package names prevents collisions.",
            badExample = "// File in :core:network\npackage com.something.else",
            goodExample = "// File in :core:network\npackage com.estatia.realestate.apps.core.network",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-004",
            implementation = Implementation(PackageBoundaryDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
