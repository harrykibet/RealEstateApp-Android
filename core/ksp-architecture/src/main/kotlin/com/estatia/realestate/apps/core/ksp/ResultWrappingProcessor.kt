package com.estatia.realestate.apps.core.ksp

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-009: Mandatory Result Wrapping.
 * Enforces that all public methods in @Repository, @Service, and @UseCase return AppResult or Flow.
 */
class ResultWrappingProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    private val allowedWrappers = listOf(
        "com.estatia.realestate.apps.core.common.exceptions.AppResult",
        "kotlinx.coroutines.flow.Flow",
        "kotlin.Unit",
        "void",
        "kotlin.Nothing"
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.Repository") +
                      resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.Service") +
                      resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.UseCase")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            clazz.getDeclaredFunctions().forEach { function ->
                val name = function.simpleName.asString()
                if (name != "<init>" && name != clazz.simpleName.asString() && isPublic(function)) {
                    val returnType = function.returnType?.resolve()
                    val qualifiedName = returnType?.declaration?.qualifiedName?.asString() ?: ""
                    
                    if (!allowedWrappers.contains(qualifiedName)) {
                        logger.error(
                            "Architecture Violation (LAW-009): Public method '${function.simpleName.asString()}' in ${clazz.simpleName.asString()} " +
                            "must return a wrapped Result type (AppResult or Flow). Found: $qualifiedName",
                            function
                        )
                    }
                }
            }
        }
        return emptyList()
    }

    private fun isPublic(node: KSModifierListOwner): Boolean {
        return !node.modifiers.contains(Modifier.PRIVATE) &&
               !node.modifiers.contains(Modifier.INTERNAL) &&
               !node.modifiers.contains(Modifier.PROTECTED)
    }
}

class ResultWrappingProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ResultWrappingProcessor(environment.logger)
    }
}
