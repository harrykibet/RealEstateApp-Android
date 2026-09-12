package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-002: Mutable state never crosses an ownership boundary.
 * 
 * Enforces that ViewModels and other architectural components do not expose 
 * mutable state containers (MutableStateFlow, MutableState) to external callers.
 */
class Law002_ExposedMutableStateProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    private val allowedAnnotation = "com.estatia.realestate.apps.core.architecture.annotations.Safety.AllowedArchitectureDependency"
    
    private val targetArchRoles = setOf(
        "ViewModelMarker", "Repository", "Service", "UseCase", "Manager", 
        "Coordinator", "DataSource", "Utility", "Foundation", "Policy"
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val viewModelFqn = "androidx.lifecycle.ViewModel"
        val viewModelType = resolver.getClassDeclarationByName(resolver.getKSNameFromString(viewModelFqn))?.asStarProjectedType()

        val symbols = resolver.getAllFiles()
            .flatMap { it.declarations }
            .filterIsInstance<KSClassDeclaration>()
            .filter { clazz ->
                val hasArchRole = clazz.annotations.any { isEstatiaArchRole(it) }
                val isViewModel = viewModelType?.isAssignableFrom(clazz.asStarProjectedType()) == true
                hasArchRole || isViewModel
            }

        symbols.forEach { clazz ->
            if (clazz.classKind == ClassKind.INTERFACE || clazz.modifiers.contains(Modifier.ABSTRACT)) return@forEach

            val publicProperties = clazz.getDeclaredProperties().filter { prop ->
                !prop.modifiers.contains(Modifier.PRIVATE) &&
                !prop.modifiers.contains(Modifier.INTERNAL) &&
                !prop.modifiers.contains(Modifier.PROTECTED)
            }

            publicProperties.forEach { prop ->
                val typeName = prop.type.resolve().declaration.qualifiedName?.asString() ?: ""
                val isMutable = typeName == "kotlinx.coroutines.flow.MutableStateFlow" || 
                               typeName == "androidx.compose.runtime.MutableState"
                
                val isAuthorized = prop.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == allowedAnnotation }

                if (isMutable && !isAuthorized) {
                    logger.report(
                        Law.LAW_002,
                        "ViewModel '${clazz.simpleName.asString()}' exposes mutable state '${prop.simpleName.asString()}'. " +
                        "Mutable state must remain private. Expose as a read-only StateFlow instead (LAW-002).",
                        prop
                    )
                }
            }
        }
        return emptyList()
    }

    private fun isEstatiaArchRole(ann: KSAnnotation): Boolean {
        val qn = ann.annotationType.resolve().declaration.qualifiedName?.asString() ?: ""
        if (!qn.startsWith("com.estatia.realestate.apps.core.architecture.annotations.")) return false
        val simpleName = qn.substringAfterLast(".")
        return targetArchRoles.contains(simpleName)
    }
}

class Law002_ExposedMutableStateProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law002_ExposedMutableStateProcessor(environment.logger)
    }
}
