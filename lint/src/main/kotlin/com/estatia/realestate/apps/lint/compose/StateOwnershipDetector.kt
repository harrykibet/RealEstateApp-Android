package com.estatia.realestate.apps.lint.compose

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UMethod

/**
 * Enforces Unidirectional Data Flow (UDF) by preventing Composables from accepting 
 * mutable state containers as parameters.
 */
class StateOwnershipDetector : Detector(), SourceCodeScanner {

    private val mutableContainers = setOf(
        "androidx.compose.runtime.MutableState",
        "kotlinx.coroutines.flow.MutableStateFlow",
        "kotlinx.coroutines.flow.MutableSharedFlow",
        "kotlin.collections.MutableList",
        "java.util.ArrayList"
    )

    override fun getApplicableUastTypes() = listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitMethod(node: UMethod) {
            val isComposable = context.evaluator.getAnnotations(node.javaPsi, false)
                .any { it.qualifiedName == "androidx.compose.runtime.Composable" }
            if (!isComposable) return

            node.uastParameters.forEach { parameter ->
                val type = parameter.type
                val isMutable = mutableContainers.any { containerFqn ->
                    context.evaluator.inheritsFrom(context.evaluator.getTypeClass(type), containerFqn, false)
                }

                if (isMutable) {
                    context.report(
                        ISSUE,
                        parameter as UElement,
                        context.getLocation(parameter as UElement),
                        "Composable parameter '${parameter.name}' is a mutable container. " +
                                "Pass a read-only State or a simple data class and use lambda callbacks for events (UDF)."
                    )
                }
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "MutableStateParameter",
            description = "Mutable state passed to Composable",
            rationale = "Passing mutable containers to Composables violates UDF and makes state ownership ambiguous.",
            badExample = "@Composable fun User(state: MutableStateFlow<User>)",
            goodExample = "@Composable fun User(user: User, onUpdate: () -> Unit)",
            category = IssueCategory.COMPOSE,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PRODUCT,
            architectureLaw = "LAW-018 (UI Data Flow)",
            implementation = Implementation(StateOwnershipDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
