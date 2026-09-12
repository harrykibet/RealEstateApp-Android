package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import org.jetbrains.uast.UImportStatement

/**
 * LAW-003: Infrastructure leakage prevention.
 * Ensures that pure layers (Domain, Model) remain decoupled from implementation details.
 */
class InfrastructureLeakageDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UImportStatement::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitImportStatement(node: UImportStatement) {
            val importPath = node.importReference?.asRenderString() ?: return
            val currentPackage = context.uastFile?.packageName ?: ""

            if (currentPackage.contains(".domain") || currentPackage.contains(".model")) {
                if (isInfrastructure(importPath)) {
                    context.report(
                        ISSUE,
                        node,
                        context.getLocation(node),
                        "Infrastructure implementation '$importPath' leaked into pure layer (LAW-003)."
                    )
                }
            }
        }
    }

    private fun isInfrastructure(path: String): Boolean {
        return ArchitecturalPolicy.Law003.InfrastructurePackages.any { path.contains(it) }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "InfrastructureLeakage",
            description = "Implementation detail leaked into pure layer",
            rationale = "Domain and Model layers must remain pure Kotlin and decoupled from infrastructure.",
            badExample = "import androidx.room.Entity // In core.domain",
            goodExample = "import com.estatia.realestate.apps.core.model.User",
            category = IssueCategory.ARCHITECTURE,
            architectureLaw = Law.LAW_003,
            implementation = Implementation(InfrastructureLeakageDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
