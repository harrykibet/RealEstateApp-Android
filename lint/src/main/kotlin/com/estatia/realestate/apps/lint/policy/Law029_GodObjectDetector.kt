package com.estatia.realestate.apps.lint.policy

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import com.estatia.realestate.apps.core.architecture.Law
import com.intellij.psi.PsiModifier
import org.jetbrains.uast.*

/**
 * LAW-029: Classes must have a single responsibility (Size Limit).
 * 
 * Enforces:
 * 1. Size Budget (Lines)
 * 2. Public Surface Area (Number of public methods/properties)
 * 3. Mutable State Count (var or mutable state containers)
 */
class Law029_GodObjectDetector : Detector(), SourceCodeScanner {

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = object : UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node is UAnonymousClass) return
            
            // 1. Size Check (Effective Lines of Code)
            val source = node.asSourceString()
            val effectiveLineCount = calculateEffectiveLineCount(source)
            checkThreshold(context, node, "Size", effectiveLineCount, "maxLines", 300, 600, 1000)

            // 2. Public Surface Area
            val publicMembers = node.methods.count { context.evaluator.isPublic(it) && !it.isConstructor } +
                               node.fields.count { context.evaluator.isPublic(it) }
            checkThreshold(context, node, "Public Surface Area", publicMembers, "maxPublicSurface", 15, 25, 40)

            // 3. Mutable State Count
            val mutableState = node.fields.count { isMutable(it) }
            checkThreshold(context, node, "Mutable State", mutableState, "maxMutableState", 5, 8, 12)
        }
    }

    /**
     * Calculates line count excluding blank lines and comments.
     */
    private fun calculateEffectiveLineCount(source: String): Int {
        // Strip block comments (/* ... */) including multi-line
        val noBlockComments = source.replace(Regex("/\\*([\\s\\S]*?)\\*/"), "")
        
        return noBlockComments.lines()
            .map { it.trim() }
            .filter { line ->
                // Filter blank lines, single-line comments, and KDoc star-prefixes
                line.isNotEmpty() && !line.startsWith("//") && !line.startsWith("*")
            }
            .size
    }

    private fun isMutable(field: UField): Boolean {
        if (!field.hasModifierProperty(PsiModifier.FINAL)) return true
        
        val typeName = field.type.canonicalText
        return typeName.contains("MutableState") || 
               typeName.contains("MutableStateFlow") || 
               typeName.contains("MutableSharedFlow")
    }

    private fun checkThreshold(
        context: JavaContext, 
        node: UClass, 
        label: String, 
        value: Int, 
        optionPrefix: String,
        warn: Int, 
        err: Int, 
        fatal: Int
    ) {
        val fLimit = context.getOption(ISSUE, "${optionPrefix}Fatal", fatal)
        val eLimit = context.getOption(ISSUE, "${optionPrefix}Error", err)
        val wLimit = context.getOption(ISSUE, "${optionPrefix}Warning", warn)

        when {
            value > fLimit -> report(context, node, label, value, "CRITICAL", fLimit)
            value > eLimit -> report(context, node, label, value, "HIGH", eLimit)
            value > wLimit -> report(context, node, label, value, "MEDIUM", wLimit)
        }
    }

    private fun report(context: JavaContext, node: UClass, type: String, value: Int, level: String, limit: Int) {
        context.report(
            ISSUE,
            node,
            context.getLocation(node as UElement),
            "Class '${node.name}' has $level $type Risk ($value). Recommended limit is $limit. 'God Objects' are harder to maintain and test (LAW-029)."
        )
    }

    private fun JavaContext.getOption(issue: Issue, name: String, default: Int): Int {
        return configuration.getOption(issue, name)?.toIntOrNull() ?: default
    }

    companion object {
        val ISSUE = EstatiaIssue.create(
            id = "GodObjectFatal",
            description = "Class violates size or complexity budget",
            rationale = "Large classes usually have too many responsibilities. Estatia enforces budgets for lines of code, public surface area, and mutable state.",
            badExample = "class EverythingManager { ... 1000 lines ... }",
            goodExample = "class FocusedComponent { ... 200 lines ... }",
            category = IssueCategory.CODE_HEALTH,
            architectureLaw = Law.LAW_029,
            implementation = Implementation(Law029_GodObjectDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
