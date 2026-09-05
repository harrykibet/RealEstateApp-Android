package com.estatia.realestate.apps.core.ksp

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-008: Domain Expressiveness.
 * Prevents UseCases from returning raw primitives wrapped in Result, encouraging typed domain concepts.
 */
class DomainExpressivenessProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.UseCase")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            clazz.getDeclaredFunctions().forEach { function ->
                if (isPublic(function) && function.simpleName.asString() != "<init>") {
                    val returnType = function.returnType?.resolve()
                    val qualifiedName = returnType?.declaration?.qualifiedName?.asString() ?: ""

                    if (qualifiedName == "com.estatia.realestate.apps.core.common.exceptions.AppResult") {
                        val typeArg = returnType?.arguments?.firstOrNull()?.type?.resolve()
                        val typeArgQualified = typeArg?.declaration?.qualifiedName?.asString() ?: ""

                        if (typeArgQualified == "kotlin.Boolean" || typeArgQualified == "kotlin.Int") {
                            logger.warn(
                                "Domain Smell: UseCase method '${function.simpleName.asString()}' returns 'AppResult<$typeArgQualified>'. " +
                                "Consider using a sealed class or enum to express the domain meaning of this value.",
                                function
                            )
                        }
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

class DomainExpressivenessProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return DomainExpressivenessProcessor(environment.logger)
    }
}
