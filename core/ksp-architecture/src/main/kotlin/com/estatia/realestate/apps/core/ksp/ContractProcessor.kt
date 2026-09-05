package com.estatia.realestate.apps.core.ksp

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-008: Abstraction Over Implementation.
 * Enforces that every @Repository or @UseCase class must implement an interface.
 * Also checks for domain expressiveness (primitive return smells).
 */
class ContractProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val repoSymbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.Repository")
        val useCaseSymbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.UseCase")
        
        (repoSymbols + useCaseSymbols).filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            checkInterfaceImplementation(clazz)
            if (useCaseSymbols.contains(clazz)) {
                checkDomainExpressiveness(clazz)
            }
        }
        return emptyList()
    }

    private fun checkInterfaceImplementation(clazz: KSClassDeclaration) {
        val hasInterface = clazz.superTypes.any { 
            val declaration = it.resolve().declaration
            declaration is KSClassDeclaration && declaration.classKind == ClassKind.INTERFACE
        }

        if (!hasInterface) {
            logger.error(
                "Architecture Violation (LAW-008): '${clazz.simpleName.asString()}' must implement an interface " +
                "to ensure decoupling from implementation details.",
                clazz
            )
        }
    }

    private fun checkDomainExpressiveness(clazz: KSClassDeclaration) {
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

    private fun isPublic(node: KSModifierListOwner): Boolean {
        return !node.modifiers.contains(Modifier.PRIVATE) &&
               !node.modifiers.contains(Modifier.INTERNAL) &&
               !node.modifiers.contains(Modifier.PROTECTED)
    }
}

class ContractProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ContractProcessor(environment.logger)
    }
}
