package com.estatia.realestate.apps.core.ksp

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-018: UDF Consistency.
 * Enforces that every ViewModel has exactly one public StateFlow property representing its Single Source of Truth.
 */
class UiStateProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

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
                typeName == "kotlinx.coroutines.flow.StateFlow"
            }

            if (stateFlows.toList().size > 1) {
                logger.error(
                    "Architecture Violation (LAW-018): ViewModel '${clazz.simpleName.asString()}' has multiple public StateFlows. " +
                    "Use a single 'uiState' property to ensure a Single Source of Truth.",
                    clazz
                )
            }

            if (stateFlows.toList().isEmpty()) {
                logger.warn(
                    "Architecture Smell: ViewModel '${clazz.simpleName.asString()}' has no public StateFlow. " +
                    "Ensure you are exposing UI state via a read-only StateFlow.",
                    clazz
                )
            }
        }
        return emptyList()
    }
}

class UiStateProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return UiStateProcessor(environment.logger)
    }
}
