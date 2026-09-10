package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-036: Domain Expressiveness.
 * Encourages using rich types (enums, sealed classes) instead of primitives 
 * inside AppResult to provide better business context.
 */
class Law036_DomainExpressivenessProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.architecture.annotations.UseCase")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            clazz.getDeclaredFunctions().forEach { func ->
                if (isPublic(func)) {
                    checkType(func.returnType?.resolve(), func, "return type", clazz.simpleName.asString())
                }
            }
        }
        return emptyList()
    }

    private fun checkType(type: KSType?, node: KSNode, nodeType: String, className: String) {
        val qualifiedName = type?.declaration?.qualifiedName?.asString() ?: ""
        if (qualifiedName == "com.estatia.realestate.apps.core.common.exceptions.AppResult") {
            val arg = type?.arguments?.firstOrNull()?.type?.resolve()
            val argName = arg?.declaration?.qualifiedName?.asString() ?: ""
            if (argName == "kotlin.Boolean" || argName == "kotlin.Int") {
                logger.report(
                    Law.LAW_036,
                    "UseCase method '${node.toString()}' returns 'AppResult<$argName>'. " +
                    "Consider using a sealed class or enum to express the domain meaning of this value.",
                    node
                )
            }
        }
    }

    private fun isPublic(node: KSModifierListOwner): Boolean {
        return !node.modifiers.contains(Modifier.PRIVATE) &&
               !node.modifiers.contains(Modifier.INTERNAL) &&
               !node.modifiers.contains(Modifier.PROTECTED)
    }
}

class Law036_DomainExpressivenessProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law036_DomainExpressivenessProcessor(environment.logger)
    }
}
