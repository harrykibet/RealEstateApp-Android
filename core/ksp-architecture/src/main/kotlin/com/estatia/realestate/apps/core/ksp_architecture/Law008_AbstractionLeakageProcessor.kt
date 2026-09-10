package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-008: Abstraction Boundaries.
 * Prevents infrastructure leakage (Firebase, Room, etc.) in public APIs of architectural components.
 */
class Law008_AbstractionLeakageProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    private val forbiddenInfrastructure = ArchitecturalPolicy.Law003.InfrastructurePackages

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val archAnnotations = listOf(
            "com.estatia.realestate.apps.core.architecture.annotations.Repository",
            "com.estatia.realestate.apps.core.architecture.annotations.UseCase",
            "com.estatia.realestate.apps.core.architecture.annotations.Contract"
        )

        val symbols = archAnnotations.flatMap { resolver.getSymbolsWithAnnotation(it) }

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            clazz.getDeclaredProperties().forEach { prop ->
                if (isPublic(prop)) {
                    checkForbiddenType(prop.type.resolve(), prop, "property", clazz.simpleName.asString())
                }
            }
            clazz.getDeclaredFunctions().forEach { func ->
                if (isPublic(func) && func.simpleName.asString() != "<init>") {
                    checkForbiddenType(func.returnType?.resolve(), func, "return type", clazz.simpleName.asString())
                    func.parameters.forEach { param ->
                        checkForbiddenType(param.type.resolve(), param, "parameter", clazz.simpleName.asString())
                    }
                }
            }
        }
        return emptyList()
    }

    private fun checkForbiddenType(type: KSType?, node: KSNode, nodeType: String, className: String) {
        val qualifiedName = type?.declaration?.qualifiedName?.asString() ?: ""
        if (forbiddenInfrastructure.any { qualifiedName.startsWith(it) }) {
            logger.report(
                Law.LAW_008,
                "Leakage detected in $className. " +
                "Public $nodeType '${node.toString()}' exposes infrastructure type: $qualifiedName",
                node
            )
        }
        type?.arguments?.forEach { arg ->
            checkForbiddenType(arg.type?.resolve(), node, "$nodeType argument", className)
        }
    }

    private fun isPublic(node: KSModifierListOwner): Boolean {
        return !node.modifiers.contains(Modifier.PRIVATE) &&
               !node.modifiers.contains(Modifier.INTERNAL) &&
               !node.modifiers.contains(Modifier.PROTECTED)
    }
}

class Law008_AbstractionLeakageProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law008_AbstractionLeakageProcessor(environment.logger)
    }
}
