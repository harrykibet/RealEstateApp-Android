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

    private val allowedAnnotation = "com.estatia.realestate.apps.core.architecture.annotations.Safety.AllowedArchitectureDependency"
    private val allowedInfrastructure = ArchitecturalPolicy.Law030.AllowedInfrastructureInConstructors
    
    private val archAnnotations = listOf(
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Repository",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Service",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.UseCase",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.DataSource",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Manager",
        "com.estatia.realestate.apps.core.architecture.annotations.Identity.Coordinator",
        "com.estatia.realestate.apps.core.architecture.annotations.Logic.ErrorMapper",
        "com.estatia.realestate.apps.core.architecture.annotations.Logic.Mapper",
        "com.estatia.realestate.apps.core.architecture.annotations.Logic.Utility",
        "com.estatia.realestate.apps.core.architecture.annotations.Logic.Foundation",
        "com.estatia.realestate.apps.core.architecture.annotations.Logic.Policy"
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = archAnnotations.flatMap { resolver.getSymbolsWithAnnotation(it) }
            .filterIsInstance<KSClassDeclaration>()

        symbols.forEach { clazz ->
            clazz.primaryConstructor?.parameters?.forEach { param ->
                val type = param.type.resolve()
                val declaration = type.declaration
                val qualifiedName = declaration.qualifiedName?.asString() ?: ""

                // 1. Core Purity Check
                val isInterface = declaration is KSClassDeclaration && declaration.classKind == ClassKind.INTERFACE
                val isDataModel = qualifiedName.contains(".core.model.")
                val isArchComponent = declaration.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString()?.contains("com.estatia.realestate.apps.core.architecture.annotations.") == true }
                val isPrimitive = qualifiedName.startsWith("kotlin.") || 
                                 qualifiedName.startsWith("java.lang.") || 
                                 qualifiedName.startsWith("java.util.") ||
                                 qualifiedName.startsWith("kotlinx.coroutines.") ||
                                 qualifiedName.startsWith("androidx.media3.") ||
                                 qualifiedName.startsWith("android.net.")
                val isSafeInfra = allowedInfrastructure.contains(qualifiedName)
                
                // 2. Metadata Authorization Check
                val isExplicitlyAllowed = param.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == allowedAnnotation } ||
                                         declaration.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == allowedAnnotation }

                if (!isInterface && !isDataModel && !isArchComponent && !isPrimitive && !isSafeInfra && !isExplicitlyAllowed) {
                    logger.report(
                        Law.LAW_030,
                        "Constructor parameter '${param.name?.asString()}' in ${clazz.simpleName.asString()} " +
                        "is an external dependency ($qualifiedName). To maintain architectural purity, prefer interfaces or data models. (LAW-030).",
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
