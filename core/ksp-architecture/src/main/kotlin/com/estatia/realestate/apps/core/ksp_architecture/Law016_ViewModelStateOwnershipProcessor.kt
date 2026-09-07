package com.estatia.realestate.apps.core.ksp_architecture

import com.estatia.realestate.apps.core.architecture.Law
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

/**
 * LAW-016: ViewModel State Ownership.
 * Enforces that ViewModels do not expose mutable state containers.
 */
class Law016_ViewModelStateOwnershipProcessor(
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

            publicProperties.forEach { prop ->
                val typeName = prop.type.resolve().declaration.qualifiedName?.asString() ?: ""
                if (typeName == "kotlinx.coroutines.flow.MutableStateFlow" || 
                    typeName == "androidx.compose.runtime.MutableState") {
                    logger.report(
                        Law.LAW_016,
                        "ViewModel '${clazz.simpleName.asString()}' exposes mutable state '${prop.simpleName.asString()}'. " +
                        "Expose as StateFlow or a read-only interface instead.",
                        prop
                    )
                }
            }
        }
        return emptyList()
    }
}

class Law016_ViewModelStateOwnershipProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return Law016_ViewModelStateOwnershipProcessor(environment.logger)
    }
}
