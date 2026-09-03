package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UImportStatement
import org.jetbrains.uast.UElement

/**
 * Enforces LAW-003 and LAW-032: "Architectural layers have restricted framework dependencies."
 * Ensures that layers like 'domain' and 'model' remain pure and decoupled from infrastructure.
 */
class LayerDependencyDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes() = listOf(UImportStatement::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitImportStatement(node: UImportStatement) {
            val importPath = node.importReference?.asRenderString() ?: return
            val filePackage = context.evaluator.getPackage(context.uastFile!!)?.qualifiedName ?: return

            when {
                filePackage.contains(".core.domain") -> checkDomainLayer(context, node, importPath)
                filePackage.contains(".core.model") -> checkModelLayer(context, node, importPath)
                filePackage.contains(".feature.") && (filePackage.endsWith(".ui") || filePackage.contains(".presentation")) -> 
                    checkPresentationLayer(context, node, importPath)
            }
        }
    }

    private fun checkDomainLayer(context: JavaContext, node: UElement, importPath: String) {
        val forbidden = listOf(
            "android." to "Android Framework",
            "androidx." to "AndroidX",
            "com.google.firebase" to "Firebase",
            "okhttp3" to "OkHttp",
            "retrofit2" to "Retrofit",
            "com.amplifyframework" to "Amplify"
        )

        // Allow essential annotations
        if (importPath.startsWith("androidx.annotation")) return

        forbidden.forEach { (pkg, name) ->
            if (importPath.startsWith(pkg)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Domain layer purity violation: Cannot import $name ($pkg). The Domain layer must remain pure logic."
                )
            }
        }
    }

    private fun checkModelLayer(context: JavaContext, node: UElement, importPath: String) {
        if (importPath.startsWith("android.") || importPath.startsWith("androidx.")) {
            if (importPath.startsWith("androidx.annotation")) return
            
            context.report(
                ISSUE,
                node,
                context.getLocation(node),
                "Model layer violation: Models must be pure Kotlin and cannot depend on Android frameworks."
            )
        }
    }

    private fun checkPresentationLayer(context: JavaContext, node: UElement, importPath: String) {
        val forbidden = listOf(
            "com.google.firebase" to "Firebase",
            "androidx.room" to "Room/Database",
            "retrofit2" to "Retrofit",
            "com.amplifyframework" to "Amplify"
        )

        forbidden.forEach { (pkg, name) ->
            if (importPath.startsWith(pkg)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Presentation layer violation: Cannot use infrastructure details ($name) directly in UI. Use a ViewModel/Repository abstraction instead."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "LayerDependencyViolation",
            description = "Forbidden framework dependency in architectural layer",
            explanation = """
                To ensure testability and modularity, Estatia enforces strict rules on 
                which frameworks can be used in each layer. 
                
                - Domain/Model: Must be pure Kotlin (No Android/Infrastructure).
                - Presentation: No direct Infrastructure (Firebase/Database).
            """,
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(LayerDependencyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
