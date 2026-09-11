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
            "com.estatia.realestate.apps.core.architecture.annotations.Repository",
            "com.estatia.realestate.apps.core.architecture.annotations.Service",
            "com.estatia.realestate.apps.core.architecture.annotations.UseCase",
            "com.estatia.realestate.apps.core.architecture.annotations.Contract"
        )

        val symbols = pureArchitecturalAnnotations.flatMap { resolver.getSymbolsWithAnnotation(it) }

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            // 🛡️ REFINEMENT: Identity Integrity (Verifying the claim)
            // If a component claims to be a pure business role, we enforce purity.
            val annotations = clazz.annotations.map { it.annotationType.resolve().declaration.qualifiedName?.asString() }.toSet()
            
            val isContract = annotations.contains("com.estatia.realestate.apps.core.architecture.annotations.Contract")
            val isRepository = annotations.contains("com.estatia.realestate.apps.core.architecture.annotations.Repository")
            val isUseCase = annotations.contains("com.estatia.realestate.apps.core.architecture.annotations.UseCase")
            val isService = annotations.contains("com.estatia.realestate.apps.core.architecture.annotations.Service")
            
            // 💡 DataSources are EXEMPT from LAW-008 as they are infrastructure-facing by definition.
            val isDataSource = annotations.contains("com.estatia.realestate.apps.core.architecture.annotations.DataSource")

            if ((isContract || isRepository || isUseCase || isService) && !isDataSource) {
                auditDeclaration(clazz)
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
                checkForbiddenType(func.returnType?.resolve(), func, "return type", clazz.simpleName.asString())
                func.parameters.forEach { param ->
                    checkForbiddenType(param.type.resolve(), param, "parameter", clazz.simpleName.asString())
                }
            }
        }
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
