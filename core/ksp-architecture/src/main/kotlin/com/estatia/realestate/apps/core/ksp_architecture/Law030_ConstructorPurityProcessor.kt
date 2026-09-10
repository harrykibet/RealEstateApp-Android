package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.ArchitecturalPolicy
import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-030: Dependency Budget & Purity.
 * Enforces that constructors of @UseCase and @Repository classes only accept 
 * interfaces, pure Data Models, or explicitly authorized dependencies.
 */
class Law030_ConstructorPurityProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    private val allowedAnnotation = "com.estatia.realestate.apps.core.architecture.annotations.AllowedArchitectureDependency"
    private val allowedInfrastructure = ArchitecturalPolicy.Law030.AllowedInfrastructureInConstructors

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.architecture.annotations.Repository") +
                      resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.architecture.annotations.UseCase")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            clazz.primaryConstructor?.parameters?.forEach { param ->
                val type = param.type.resolve()
                val declaration = type.declaration
                val simpleName = declaration.simpleName.asString()
                val qualifiedName = declaration.qualifiedName?.asString() ?: ""

                // 1. Core Purity Check
                val isInterface = declaration is KSClassDeclaration && declaration.classKind == ClassKind.INTERFACE
                val isDataModel = qualifiedName.contains(".core.model.")
                val isPrimitive = qualifiedName.startsWith("kotlin.") || qualifiedName.startsWith("java.lang.")
                val isSafeInfra = allowedInfrastructure.contains(qualifiedName)
                
                // 2. Metadata Authorization Check
                val isExplicitlyAllowed = param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == allowedAnnotation } ||
                                         declaration.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == allowedAnnotation }

                if (!isInterface && !isDataModel && !isPrimitive && !isSafeInfra && !isExplicitlyAllowed) {
                    logger.report(
                        Law.LAW_030,
                        "Constructor parameter '${param.name?.asString()}' in ${clazz.simpleName.asString()} " +
                        "must be an interface, a pure Data Model, or explicitly authorized via @AllowedArchitectureDependency. " +
                        "Found: $qualifiedName",
                        param
                    )
                }
                
                // 3. Naming Convention Check
                if (isInterface && !simpleName.startsWith("I") && !simpleName.contains("Component")) {
                    logger.report(
                        Law.LAW_030,
                        "Interface '$simpleName' used in constructor of ${clazz.simpleName.asString()} " +
                        "should follow the 'I' prefix convention for clear abstraction visibility.",
                        param
                    )
                }
            }
        }
        return emptyList()
    }
}

class Law030_ConstructorPurityProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law030_ConstructorPurityProcessor(environment.logger)
    }
}
