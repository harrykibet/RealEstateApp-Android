package com.estatia.realestate.apps.lint.performance

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.*

/**
 * Detects storage of lifecycle-bound objects in long-lived components.
 */
class LifecycleLeakDetector : Detector(), SourceCodeScanner {

    private val lifecycleTypes = setOf("android.app.Activity", "androidx.fragment.app.Fragment", "android.view.View", "Context")

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UField::class.java, UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val containingClass = node.getParentOfType<UClass>() ?: return
            
            val isLongLived = context.evaluator.inheritsFrom(containingClass, "androidx.lifecycle.ViewModel", false) ||
                             context.evaluator.inheritsFrom(containingClass, "android.app.Service", false) ||
                             containingClass.name?.endsWith("Repository") == true ||
                             context.evaluator.getAnnotations(containingClass.javaPsi, false).any { it.qualifiedName?.contains("Singleton") == true }

            if (isLongLived) {
                val type = node.type
                val typeClass = context.evaluator.getTypeClass(type)
                
                val isLifecycleType = lifecycleTypes.any { lifecycleType ->
                    context.evaluator.inheritsFrom(typeClass, lifecycleType, false)
                }

                if (isLifecycleType) {
                    context.report(
                        LEAK_ISSUE,
                        node,
                        context.getLocation(node),
                        "Field '${node.name}' is a lifecycle-bound type. Storing it here will cause memory leaks (LAW-023)."
                    )
                }
            }
        }
        
        override fun visitClass(node: UClass) {
            val isSingleton = context.evaluator.getAnnotations(node.javaPsi, false)
                .any { it.qualifiedName?.contains("Singleton") == true }
            
            if (isSingleton) {
                node.fields.forEach { field ->
                    val typeClass = context.evaluator.getTypeClass(field.type)
                    if (context.evaluator.inheritsFrom(typeClass, "android.content.Context", false)) {
                        context.report(
                            LEAK_ISSUE,
                            field,
                            context.getLocation(field),
                            "Context stored in a Singleton object (LAW-024)."
                        )
                    }
                }
            }
        }
    }

    companion object {
        val LEAK_ISSUE = EstatiaIssue.create(
            id = "LifecycleLeak",
            description = "Lifecycle-bound object stored in long-lived component",
            rationale = "Storing references to Activities or Views in long-lived components causes leaks.",
            badExample = "class MyViewModel(val activity: Activity) : ViewModel()",
            goodExample = "class MyViewModel(val application: Application) : ViewModel()",
            category = IssueCategory.PERFORMANCE,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = "LAW-023",
            implementation = Implementation(LifecycleLeakDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
