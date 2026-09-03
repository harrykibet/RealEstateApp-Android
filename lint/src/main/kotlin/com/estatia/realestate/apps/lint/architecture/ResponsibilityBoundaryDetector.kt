package com.estatia.realestate.apps.lint.architecture

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiClass
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * Enforces LAW-031: "Classes must have pure responsibilities and not mix layers."
 * Detects "Orchestration Monsters" and "Leaky Abstractions" by identifying 
 * suspicious combinations of technologies within a single class.
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
                node.methods.any { it.javaPsi.annotations.any { ann -> ann.qualifiedName?.contains("Composable") == true } } -> checkComposable(context, node)
            }
        }
    }

    private fun checkViewModel(context: JavaContext, node: UClass) {
        val forbidden = mapOf(
            "com.google.firebase" to "Firebase",
            "retrofit2" to "Retrofit/Networking",
            "androidx.room" to "Room/Database",
            "com.amplifyframework" to "Amplify"
        )
        findIllegalReferences(context, node, forbidden, "ViewModel '${node.name}'")
    }

    private fun checkBusinessLogic(context: JavaContext, node: UClass) {
        val forbidden = mapOf(
            "androidx.compose" to "Jetpack Compose",
            "android.view" to "Android Views",
            "android.app.Activity" to "Activity",
            "androidx.fragment" to "Fragment"
        )
        findIllegalReferences(context, node, forbidden, "Business component '${node.name}'")
    }

    private fun checkComposable(context: JavaContext, node: UClass) {
        val forbidden = mapOf(
            "com.estatia.realestate.apps.core.database" to "Database",
            "com.google.firebase" to "Firebase",
            "retrofit2" to "Retrofit",
            "com.estatia.realestate.apps.core.analytics" to "Analytics"
        )
        findIllegalReferences(context, node, forbidden, "Composable component '${node.name}'")
    }

    private fun findIllegalReferences(
        context: JavaContext, 
        node: UClass, 
        forbidden: Map<String, String>, 
        contextName: String
    ) {
        node.accept(object : AbstractUastVisitor() {
            override fun visitCallExpression(node: UCallExpression): Boolean {
                val resolved = node.resolve()
                val pkg = context.evaluator.getPackage(resolved?.containingClass ?: return false)?.qualifiedName ?: ""
                
                checkPackage(context, node, pkg, forbidden, contextName)
                return super.visitCallExpression(node)
            }

            override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression): Boolean {
                val resolved = node.resolve() as? PsiClass ?: return false
                val pkg = context.evaluator.getPackage(resolved)?.qualifiedName ?: ""
                
                checkPackage(context, node, pkg, forbidden, contextName)
                return super.visitSimpleNameReferenceExpression(node)
            }
        })
    }

    private fun checkPackage(
        context: JavaContext,
        node: UElement,
        pkg: String,
        forbidden: Map<String, String>,
        contextName: String
    ) {
        forbidden.forEach { (forbiddenPkg, technology) ->
            if (pkg.startsWith(forbiddenPkg)) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "Illegal Layer Mixing: $contextName directly references $technology. Move this dependency to the appropriate architectural layer (LAW-031)."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "LayerMixingViolation",
            description = "Suspicious mixing of layers or responsibilities",
            explanation = """
                To maintain a clean architecture, components must have pure responsibilities. 
                ViewModels shouldn't know about databases, and Repositories shouldn't 
                know about the UI. Mixing these signals makes the code hard to test 
                and maintain.
            """,
            category = IssueCategory.ARCHITECTURE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.ARCHITECTURE,
            implementation = Implementation(ResponsibilityBoundaryDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
