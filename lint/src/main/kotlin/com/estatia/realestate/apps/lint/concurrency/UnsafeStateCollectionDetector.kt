package com.estatia.realestate.apps.lint.concurrency

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.lint.policy.EstatiaIssue
import com.estatia.realestate.apps.lint.policy.IssueCategory
import com.estatia.realestate.apps.lint.policy.IssueTier
import com.estatia.realestate.apps.lint.policy.RuleOwner
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UField

/**
 * Detects usage of MutableStateFlow or other state containers inside standard collections.
 */
class UnsafeStateCollectionDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UField::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitField(node: UField) {
            val type = node.type.canonicalText
            if ((type.contains("List") || type.contains("Map")) && type.contains("MutableStateFlow")) {
                context.report(
                    ISSUE,
                    node,
                    context.getLocation(node),
                    "State containers inside collections detected. Use 'mutableStateListOf()' or a dedicated State holder instead."
                )
            }
        }
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "UnsafeStateCollection",
            description = "State containers inside standard collections",
            explanation = """
                Wrapping StateFlows inside standard Lists or Maps makes it difficult to 
                observe changes correctly and can lead to memory leaks or missed updates.
            """,
            category = IssueCategory.CONCURRENCY,
            tier = IssueTier.ERROR,
            owner = RuleOwner.PLATFORM,
            implementation = Implementation(UnsafeStateCollectionDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
