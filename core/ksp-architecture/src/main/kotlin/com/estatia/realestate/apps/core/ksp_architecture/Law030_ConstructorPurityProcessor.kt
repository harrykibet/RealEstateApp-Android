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
    
    private val functionalRoles = setOf(
        "Repository", "Service", "UseCase", "DataSource", "Manager", 
        "Coordinator", "ErrorMapper", "Mapper", "Utility", "Foundation", "Policy"
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { clazz ->
                clazz.annotations.any { ann ->
                    val qn = ann.annotationType.resolve().declaration.qualifiedName?.asString() ?: ""
                    qn.startsWith("com.estatia.realestate.apps.core.architecture.annotations.") &&
                    functionalRoles.contains(qn.substringAfterLast("."))
                }
            }

        symbols.forEach { clazz ->
            clazz.primaryConstructor?.parameters?.forEach { param ->
                val type = param.type.resolve()
                val declaration = type.declaration
                val simpleName = declaration.simpleName.asString()
                val qualifiedName = declaration.qualifiedName?.asString() ?: ""

                // 1. Core Purity Check
                val isInterface = declaration is KSClassDeclaration && declaration.classKind == ClassKind.INTERFACE
                val isDataModel = qualifiedName.contains(".core.model.")
                val isArchComponent = declaration.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString()?.startsWith("com.estatia.realestate.apps.core.architecture.annotations.") == true }
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
                        "must be an interface, a pure Data Model, or explicitly authorized via @AllowedArchitectureDependency. " +
                        "Found: $qualifiedName",
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
