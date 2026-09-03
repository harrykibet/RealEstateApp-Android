package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * Enforces LAW-031: "Classes must have pure responsibilities and not mix layers."
 */
class ResponsibilityBoundaryDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node is UAnonymousClass) return
            
            val className = node.name ?: ""

            when {
                className.endsWith("ViewModel") -> checkViewModel(context, node)
                className.endsWith("Repository") || className.endsWith("UseCase") || className.endsWith("Service") -> checkBusinessLogic(context, node)
            }
            
            // Check Composable methods
            node.methods.forEach { method ->
                if (method.javaPsi.annotations.any { it.qualifiedName?.contains("Composable") == true }) {
                    checkComposable(context, method)
                }
            }
        }
    }

    private fun checkViewModel(context: JavaContext, node: UClass) {
        val forbidden = mapOf(
            "com.google.firebase" to "Firebase",
            "retrofit2" to "Retrofit/Networking",
            "androidx.room" to "Room/Database"
        )
        findIllegalReferences(context, node, forbidden, "ViewModel '${node.name}'")
    }

    private fun checkBusinessLogic(context: JavaContext, node: UClass) {
        val forbidden = mapOf(
            "androidx.compose" to "Jetpack Compose",
            "android.view" to "Android Views"
        )
        findIllegalReferences(context, node, forbidden, "Business component '${node.name}'")
    }

    private fun checkComposable(context: JavaContext, node: UMethod) {
        val forbidden = mapOf(
            "com.estatia.realestate.apps.core.database" to "Database",
            "com.google.firebase" to "Firebase"
        )
        findIllegalReferences(context, node, forbidden, "Composable component '${node.name}'")
    }

    private fun findIllegalReferences(
        context: JavaContext, 
        node: UElement, 
        forbidden: Map<String, String>, 
        contextName: String
    ) {
        node.accept(object : AbstractUastVisitor() {
            override fun visitCallExpression(node: UCallExpression): Boolean {
                val method = node.resolve()
                val pkg = method?.containingClass?.qualifiedName ?: node.asSourceString()
                
                checkPkg(context, node, pkg, forbidden, contextName)
                return super.visitCallExpression(node)
            }

            override fun visitImportStatement(node: UImportStatement): Boolean {
                val pkg = node.importReference?.asRenderString() ?: ""
                checkPkg(context, node, pkg, forbidden, contextName)
                return super.visitImportStatement(node)
            }
        })
    }

    private fun checkPkg(context: JavaContext, node: UElement, pkg: String, forbidden: Map<String, String>, contextName: String) {
        forbidden.forEach { (forbiddenPkg, technology) ->
            if (pkg.contains(forbiddenPkg)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Illegal Layer Mixing: $contextName directly references $technology (LAW-031)."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "LayerMixingViolation",
            description = "Suspicious mixing of layers or responsibilities",
            rationale = "Components must have pure responsibilities to remain testable.",
            badExample = "class UserRepo { fun showToast() { ... } }",
            goodExample = "class UserRepo { fun load() { ... } }",
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            architectureLaw = "LAW-031",
            implementation = Implementation(ResponsibilityBoundaryDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
