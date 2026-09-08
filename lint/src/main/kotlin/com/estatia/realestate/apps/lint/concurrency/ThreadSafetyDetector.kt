package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.core.architecture.Law
import com.estatia.realestate.apps.lint.policy.RuleOwner
import com.intellij.psi.PsiModifier
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.AbstractUastVisitor

/**
 * LAW-012: State Synchronization.
 * Detects usage of non-thread-safe collections or state in multi-threaded environments.
 * 
 * This detector is mutation-aware: it flags 'var' collections immediately, but 'val'
 * collections are only flagged if they are mutated outside of initialization.
 */
class ThreadSafetyDetector : Detector(), SourceCodeScanner {

    private val unsafeCollections = mapOf(
        "java.util.HashMap" to "ConcurrentHashMap",
        "java.util.ArrayList" to "CopyOnWriteArrayList",
        "java.util.HashSet" to "ConcurrentHashMap.newKeySet()",
        "HashMap" to "ConcurrentHashMap",
        "ArrayList" to "CopyOnWriteArrayList",
        "HashSet" to "ConcurrentHashMap.newKeySet()"
    )

    private val mutatingMethods = setOf(
        "put", "putAll", "remove", "clear", "replace", "replaceAll", "compute", "merge", // Map
        "add", "addAll", "remove", "removeAll", "retainAll", "set", "plusAssign", "minusAssign" // Collection/List
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node is UAnonymousClass) return
            if (!isAtRiskComponent(context, node)) return

            val unsafeFields = node.fields.filter { field ->
                unsafeCollections.keys.any { unsafe -> 
                    context.evaluator.inheritsFrom(context.evaluator.getTypeClass(field.type), unsafe, false)
                }
            }

            if (unsafeFields.isEmpty()) return

            val violations = mutableSetOf<UField>()
            
            // 1. var fields are always violations in shared components
            unsafeFields.filter { !it.hasModifierProperty(PsiModifier.FINAL) }.forEach { 
                violations.add(it) 
            }

            // 2. val fields are only violations if mutated post-init
            val candidateValFields = unsafeFields.filter { it.hasModifierProperty(PsiModifier.FINAL) }
            if (candidateValFields.isNotEmpty()) {
                val fieldNames = candidateValFields.map { it.name }.toSet()
                val mutatedFields = mutableSetOf<String>()

                node.accept(object : AbstractUastVisitor() {
                    override fun visitCallExpression(node: UCallExpression): Boolean {
                        val methodName = node.methodName
                        if (mutatingMethods.contains(methodName)) {
                            val receiver = node.receiver
                            if (receiver != null) {
                                val resolvedReceiver = receiver.tryResolve()
                                if (resolvedReceiver != null) {
                                    val field = candidateValFields.find { it.javaPsi == resolvedReceiver }
                                    if (field != null && !isInsideInitialization(node)) {
                                        mutatedFields.add(field.name)
                                    }
                                } else {
                                    // Fallback for cases where resolution fails: strip parentheses from render string
                                    val receiverName = receiver.asRenderString().replace("(", "").replace(")", "")
                                    if (fieldNames.contains(receiverName) && !isInsideInitialization(node)) {
                                        mutatedFields.add(receiverName)
                                    }
                                }
                            }
                        }
                        return super.visitCallExpression(node)
                    }

                    private fun isInsideInitialization(element: UElement): Boolean {
                        var current = element.uastParent
                        while (current != null) {
                            if (current is UMethod && current.isConstructor) return true
                            if (current is UClassInitializer) return true
                            current = current.uastParent
                        }
                        return false
                    }
                })

                candidateValFields.filter { mutatedFields.contains(it.name) }.forEach {
                    violations.add(it)
                }
            }

            violations.forEach { field ->
                val unsafeType = unsafeCollections.keys.find { 
                    context.evaluator.inheritsFrom(context.evaluator.getTypeClass(field.type), it, false)
                } ?: "Collection"
                val safeType = unsafeCollections[unsafeType] ?: "thread-safe alternative"

                context.report(
                    ISSUE,
                    field,
                    context.getLocation(field as UElement),
                    "Unsafe collection '$unsafeType' mutated in a multi-threaded component. " +
                            "Use '$safeType' or wrap in a mutex (LAW-012)."
                )
            }
        }
    }

    private fun isAtRiskComponent(context: JavaContext, node: UClass): Boolean {
        val name = node.name ?: ""
        
        // 1. Long-lived singletons (Hilt)
        val hasSingletonAnnotation = context.evaluator.getAnnotations(node.javaPsi, false)
            .any { 
                val qn = it.qualifiedName ?: ""
                qn.contains("Singleton") || qn.contains("Service") || qn.contains("Repository") 
            }
            
        // 2. ViewModels (Implicitly multi-threaded via viewModelScope)
        val isViewModel = context.evaluator.inheritsFrom(node, "androidx.lifecycle.ViewModel", false) ||
                          name.endsWith("ViewModel")
                          
        // 3. Explicit architectural markers
        val isArchComponent = name.endsWith("Repository") || 
                              name.endsWith("Service") || 
                              name.endsWith("UseCase") ||
                              name.endsWith("Manager")

        return hasSingletonAnnotation || isViewModel || isArchComponent
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "ThreadSafetyViolation",
            description = "Non-thread-safe state in multi-threaded component",
            rationale = "Standard collections used in shared components (Singletons, Repositories, ViewModels) " +
                        "lead to data races and crashes when accessed from multiple coroutines.",
            badExample = "val map = HashMap<String, String>()",
            goodExample = "val map = ConcurrentHashMap<String, String>()",
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.FATAL,
            owner = RuleOwner.PLATFORM,
            architectureLaw = Law.LAW_012,
            implementation = Implementation(ThreadSafetyDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
