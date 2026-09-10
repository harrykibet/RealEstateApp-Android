package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-009: Mandatory Result Wrapping (Estatia Convention).
 * 
 * Enforces that non-trivial public methods in @Repository, @Service, and @UseCase return AppResult or Flow.
 * Trivial methods (non-suspend returning simple types) are exempt.
 */
class Law009_ResultWrappingProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    private val allowedWrappers = listOf(
        "com.estatia.realestate.apps.core.common.exceptions.AppResult",
        "kotlinx.coroutines.flow.Flow",
        "kotlin.Unit",
        "void",
        "kotlin.Nothing"
    )

    private val exemptSimpleTypes = listOf(
        "kotlin.String",
        "kotlin.Int",
        "kotlin.Long",
        "kotlin.Boolean",
        "kotlin.Double",
        "kotlin.Float"
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.architecture.annotations.Repository") +
                      resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.architecture.annotations.Service") +
                      resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.architecture.annotations.UseCase")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            clazz.getDeclaredFunctions().forEach { function ->
                val name = function.simpleName.asString()
                if (name != "<init>" && name != clazz.simpleName.asString() && isPublic(function)) {
                    val returnType = function.returnType?.resolve()
                    val qualifiedName = returnType?.declaration?.qualifiedName?.asString() ?: ""
                    
                    if (!isExempt(function, qualifiedName) && !allowedWrappers.contains(qualifiedName)) {
                        val message = "Public method '${function.simpleName.asString()}' in ${clazz.simpleName.asString()} " +
                                     "should return a wrapped Result type (AppResult or Flow). Found: $qualifiedName"
                        
                        logger.report(Law.LAW_009, message, function)
                    }
                }
            }
        }
        return emptyList()
    }

    private fun isExempt(function: KSFunctionDeclaration, qualifiedName: String): Boolean {
        // Non-suspend functions returning simple types are exempt (likely property getters or simple checks)
        return !function.modifiers.contains(Modifier.SUSPEND) && exemptSimpleTypes.contains(qualifiedName)
    }

    private fun isPublic(node: KSModifierListOwner): Boolean {
        return !node.modifiers.contains(Modifier.PRIVATE) &&
               !node.modifiers.contains(Modifier.INTERNAL) &&
               !node.modifiers.contains(Modifier.PROTECTED)
    }
}

class Law009_ResultWrappingProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law009_ResultWrappingProcessor(environment.logger)
    }
}
