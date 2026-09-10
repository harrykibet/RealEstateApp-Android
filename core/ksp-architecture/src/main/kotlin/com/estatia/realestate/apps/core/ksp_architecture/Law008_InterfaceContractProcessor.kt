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
        val repoSymbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.architecture.annotations.Repository")
        val useCaseSymbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.architecture.annotations.UseCase")
        
        (repoSymbols + useCaseSymbols).filterIsInstance<KSClassDeclaration>().forEach { clazz ->
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
}

class Law008_InterfaceContractProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law008_InterfaceContractProcessor(environment.logger)
    }
}
