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
        val pureArchitecturalAnnotations = listOf(
            "com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository",
            "com.estatia.realestate.apps.core.architecture.annotations.Identity.Service",
            "com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase",
            "com.estatia.realestate.apps.core.architecture.annotations.Identity.Contract"
        )

        val symbols = pureArchitecturalAnnotations.flatMap { resolver.getSymbolsWithAnnotation(it) }
            .filterIsInstance<KSClassDeclaration>()

        symbols.forEach { auditDeclaration(it) }

        // 🛡️ REFINEMENT: Audit public extension functions targeting architectural roles
        resolver.getAllFiles().flatMap { it.declarations }
            .filterIsInstance<KSFunctionDeclaration>()
            .filter { it.extensionReceiver != null && isPublic(it) }
            .forEach { func ->
                val receiverType = func.extensionReceiver?.resolve()
                val receiverDeclaration = receiverType?.declaration
                if (receiverDeclaration is KSClassDeclaration) {
                    val annotations = receiverDeclaration.annotations.mapNotNull { it.annotationType.resolve().declaration.qualifiedName?.asString() }
                    if (annotations.any { pureArchitecturalAnnotations.contains(it) }) {
                        auditFunction(func, receiverDeclaration.simpleName.asString())
                    }
                }
            }

        return emptyList()
    }

    private fun auditDeclaration(clazz: KSClassDeclaration) {
        clazz.getDeclaredProperties().forEach { prop ->
            if (isPublic(prop)) {
                checkForbiddenType(prop.type.resolve(), prop, "property", clazz.simpleName.asString())
            }
        }
        clazz.getDeclaredFunctions().forEach { func ->
            if (isPublic(func) && func.simpleName.asString() != "<init>") {
                auditFunction(func, clazz.simpleName.asString())
            }
        }
    }

    private fun auditFunction(func: KSFunctionDeclaration, className: String) {
        checkForbiddenType(func.returnType?.resolve(), func, "return type", className)
        func.parameters.forEach { param ->
            checkForbiddenType(param.type.resolve(), param, "parameter", className)
        }
    }

    private fun checkForbiddenType(type: KSType?, node: KSNode, nodeType: String, className: String) {
        val qualifiedName = type?.declaration?.qualifiedName?.asString() ?: ""
        
        val isConcreteCollection = qualifiedName == "java.util.ArrayList" || 
                                  qualifiedName == "java.util.HashMap" || 
                                  qualifiedName == "java.util.HashSet"

        if (forbiddenInfrastructure.any { qualifiedName.startsWith(it) } || isConcreteCollection) {
            logger.report(
                Law.LAW_008,
                "Leakage detected in $className. " +
                "Public $nodeType '${node.toString()}' exposes infrastructure/implementation type: $qualifiedName",
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
