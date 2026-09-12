package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-008: Abstraction Over Implementation.
 * Enforces that every @Repository or @UseCase class must implement an interface.
 */
class Law008_InterfaceContractProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { clazz ->
                clazz.annotations.any { isEstatiaArchRole(it) }
            }
        
        symbols.forEach { clazz ->
            val hasInterface = clazz.superTypes.any { 
                val declaration = it.resolve().declaration
                declaration is KSClassDeclaration && declaration.classKind == ClassKind.INTERFACE
            }

            if (!hasInterface) {
                logger.report(
                    Law.LAW_008,
                    "'${clazz.simpleName.asString()}' must implement an interface " +
                    "to ensure decoupling from implementation details.",
                    clazz
                )
            }
        }
        return emptyList()
    }

    private fun isEstatiaArchRole(ann: KSAnnotation): Boolean {
        val qn = ann.annotationType.resolve().declaration.qualifiedName?.asString() ?: ""
        if (!qn.startsWith("com.estatia.realestate.apps.core.architecture.annotations.")) return false
        val role = qn.substringAfterLast(".")
        return role == "Repository" || role == "UseCase"
    }
}

class Law008_InterfaceContractProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law008_InterfaceContractProcessor(environment.logger)
    }
}
