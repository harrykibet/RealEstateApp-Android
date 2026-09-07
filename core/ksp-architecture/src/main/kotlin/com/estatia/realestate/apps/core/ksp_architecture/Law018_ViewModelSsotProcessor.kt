package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-018: ViewModel SSoT (Single Source of Truth).
 * 
 * Enforces that a ViewModel has exactly one canonical persistent UI-state owner (StateFlow).
 */
class Law018_ViewModelSsotProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    private val allowedAnnotation = "com.estatia.realestate.apps.core.common.annotations.AllowedArchitectureDependency"

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.ViewModelMarker")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            if (clazz.classKind == ClassKind.INTERFACE || clazz.modifiers.contains(Modifier.ABSTRACT)) return@forEach

            val publicProperties = clazz.getDeclaredProperties().filter { prop ->
                !prop.modifiers.contains(Modifier.PRIVATE) &&
                !prop.modifiers.contains(Modifier.INTERNAL) &&
                !prop.modifiers.contains(Modifier.PROTECTED)
            }

            val stateFlows = publicProperties.filter { prop ->
                val typeName = prop.type.resolve().declaration.qualifiedName?.asString() ?: ""
                val isStateFlow = typeName == "kotlinx.coroutines.flow.StateFlow"
                val isAuthorized = prop.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == allowedAnnotation }
                
                isStateFlow && !isAuthorized
            }.toList()

            // 1. Enforce a single canonical state owner
            if (stateFlows.size > 1) {
                logger.report(
                    Law.LAW_018,
                    "ViewModel '${clazz.simpleName.asString()}' has multiple public StateFlows (${stateFlows.joinToString { it.simpleName.asString() }}). " +
                    "A ViewModel must expose exactly one canonical persistent UI-state owner.",
                    clazz
                )
            }

            // 2. Enforce that at least one state owner is present
            if (stateFlows.isEmpty() && !clazz.annotations.any { it.annotationType.resolve().declaration.qualifiedName?.asString() == allowedAnnotation }) {
                logger.report(
                    Law.LAW_018,
                    "ViewModel '${clazz.simpleName.asString()}' has no public StateFlow. " +
                    "ViewModels must expose a canonical persistent UI-state owner (e.g., 'uiState: StateFlow<T>').",
                    clazz
                )
            }
        }
        return emptyList()
    }
}

class Law018_ViewModelSsotProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law018_ViewModelSsotProcessor(environment.logger)
    }
}
