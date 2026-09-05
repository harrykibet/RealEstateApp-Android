package com.estatia.realestate.apps.core.ksp

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-030: Dependency Budget & Purity.
 * Enforces that constructors of @UseCase and @Repository classes only accept interfaces or pure Data Models.
 */
class ConstructorAbstractionProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.Repository") +
                      resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.UseCase")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            clazz.primaryConstructor?.parameters?.forEach { param ->
                val type = param.type.resolve()
                val declaration = type.declaration
                val simpleName = declaration.simpleName.asString()
                val qualifiedName = declaration.qualifiedName?.asString() ?: ""

                val isInterface = declaration is KSClassDeclaration && declaration.classKind == ClassKind.INTERFACE
                val isDataModel = qualifiedName.contains(".core.model.")
                val isPrimitive = qualifiedName.startsWith("kotlin.") || qualifiedName.startsWith("java.lang.")
                val isSafeContext = qualifiedName == "android.content.Context" || qualifiedName == "android.app.Application"

                if (!isInterface && !isDataModel && !isPrimitive && !isSafeContext) {
                    logger.error(
                        "Architecture Violation (LAW-030): Constructor parameter '${param.name?.asString()}' in ${clazz.simpleName.asString()} " +
                        "must be an interface (usually starting with 'I') or a pure Data Model. Found: $qualifiedName",
                        param
                    )
                }
                
                // Specific rule: interfaces should start with I
                if (isInterface && !simpleName.startsWith("I") && !simpleName.contains("Component")) {
                    logger.warn(
                        "Naming Smell: Interface '$simpleName' used in constructor of ${clazz.simpleName.asString()} " +
                        "should follow the 'I' prefix convention for clear abstraction visibility.",
                        param
                    )
                }
            }
        }
        return emptyList()
    }
}

class ConstructorAbstractionProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ConstructorAbstractionProcessor(environment.logger)
    }
}
