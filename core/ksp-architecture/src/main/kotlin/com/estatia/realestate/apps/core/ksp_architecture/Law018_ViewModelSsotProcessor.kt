package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-018: UDF Consistency (Single Source of Truth).
 * Enforces that every ViewModel has exactly one public StateFlow property.
 */
class Law018_ViewModelSsotProcessor(
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
                logger.report(
                    Law.LAW_018,
                    "ViewModel '${clazz.simpleName.asString()}' has multiple public StateFlows. " +
                    "Use a single 'uiState' property to ensure a Single Source of Truth.",
                    clazz
                )
            }

            if (stateFlows.toList().isEmpty()) {
                logger.report(
                    Law.LAW_018,
                    "ViewModel '${clazz.simpleName.asString()}' has no public StateFlow. " +
                    "Ensure you are exposing UI state via a read-only StateFlow.",
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
