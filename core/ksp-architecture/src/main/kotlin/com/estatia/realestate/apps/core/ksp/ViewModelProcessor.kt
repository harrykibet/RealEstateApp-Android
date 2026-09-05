package com.estatia.realestate.apps.core.ksp

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-018 / LAW-016: ViewModel Integrity.
 * Enforces SSoT (exactly one public StateFlow) and State Ownership (no public mutable state).
 */
class ViewModelProcessor(
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

            // LAW-018: Single Source of Truth
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

            // LAW-016: State Ownership
            publicProperties.forEach { prop ->
                val typeName = prop.type.resolve().declaration.qualifiedName?.asString() ?: ""
                if (typeName == "kotlinx.coroutines.flow.MutableStateFlow" || 
                    typeName == "androidx.compose.runtime.MutableState") {
                    logger.error(
                        "Architecture Violation (LAW-016): ViewModel '${clazz.simpleName.asString()}' exposes mutable state '${prop.simpleName.asString()}'. " +
                        "Expose as StateFlow or a read-only interface instead.",
                        prop
                    )
                }
            }
        }
        return emptyList()
    }
}

class ViewModelProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ViewModelProcessor(environment.logger)
    }
}
