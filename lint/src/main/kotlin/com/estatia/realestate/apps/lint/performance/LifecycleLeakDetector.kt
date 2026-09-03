package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*
import com.intellij.psi.PsiType

/**
 * Enforces LAW-023 and LAW-024: "Lifecycle-bound objects must not be leaked."
 * Detects storage of Activity, Context, or Views in long-lived components.
 */
class LifecycleLeakDetector : Detector(), SourceCodeScanner {

    private val lifecycleTypes = setOf(
        "android.app.Activity",
        "androidx.fragment.app.Fragment",
        "android.view.View",
        "androidx.lifecycle.LifecycleOwner"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UField::class.java, UParameter::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        
        override fun visitField(node: UField) {
            val containingClass = node.getParentOfType<UClass>() ?: return
            val isLongLived = isLongLived(containingClass)
            
            if (isLongLived) {
                checkType(context, node, node.type, "Field '${node.name}' in long-lived class '${containingClass.name}'")
            }
            
            // Special check for Singleton objects (object Foo)
            if (containingClass.isStatic && containingClass.sourcePsi?.text?.startsWith("object") == true) {
                if (node.type.canonicalText.contains("android.content.Context")) {
                    context.report(
                        LEAK_ISSUE,
                        node,
                        context.getLocation(node),
                        "Context stored in a Singleton object. This is a severe memory leak. Use ApplicationContext or pass Context as a parameter to functions instead (LAW-024)."
                    )
                }
            }
        }

        override fun visitParameter(node: UParameter) {
            val method = node.getParentOfType<UMethod>() ?: return
            
            // Check Hilt injection into long-lived components
            if (method.isConstructor && (method.hasAnnotation("javax.inject.Inject") || method.hasAnnotation("jakarta.inject.Inject"))) {
                val containingClass = method.getParentOfType<UClass>() ?: return
                if (isLongLived(containingClass)) {
                    checkType(context, node, node.type, "Constructor parameter '${node.name}' of long-lived class '${containingClass.name}'")
                }
            }
        }
    }

    private fun checkType(context: JavaContext, node: UElement, type: PsiType, locationDescription: String) {
        val canonicalText = type.canonicalText
        if (lifecycleTypes.any { canonicalText.contains(it) }) {
            context.report(
                LEAK_ISSUE,
                node,
                context.getLocation(node),
                "$locationDescription is a lifecycle-bound type. Storing it here will cause memory leaks and crashes (LAW-023)."
            )
        }
    }

    private fun isLongLived(uClass: UClass): Boolean {
        val name = uClass.qualifiedName ?: ""
        val hasLongLivedAnnotation = uClass.annotations.any { 
            val qName = it.qualifiedName ?: ""
            qName.contains("Singleton") || qName.contains("ViewModel") || qName.contains("Scope") 
        }
        val isRepositoryOrService = name.contains("Repository") || name.contains("Service")
        
        return hasLongLivedAnnotation || isRepositoryOrService
    }

    companion object {
        val LEAK_ISSUE = EstatiaIssue.create(
            id = "LifecycleLeak",
            description = "Lifecycle-bound object stored in long-lived component",
            rationale = """
                Storing references to Activities or Views in long-lived classes 
                (ViewModels, Repositories) prevents garbage collection and causes leaks.
            """,
            badExample = "class MyViewModel(val activity: Activity) : ViewModel()",
            goodExample = "class MyViewModel(val application: Application) : ViewModel()",
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-023 (Memory Leak Protection)",
            implementation = Implementation(LifecycleLeakDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
