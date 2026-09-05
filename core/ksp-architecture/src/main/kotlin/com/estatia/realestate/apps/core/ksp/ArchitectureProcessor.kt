package com.estatia.realestate.apps.core.ksp

import com.google.devtools.ksp.getDeclaredFunctions
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

class ArchitectureProcessor(
    private val logger: KSPLogger
) : SymbolProcessor {

    private val forbiddenInfrastructure = listOf(
        "com.google.firebase",
        "androidx.room",
        "okhttp3",
        "retrofit2",
        "android.database.Cursor"
    )

    private val allowedWrappers = listOf(
        "com.estatia.realestate.apps.core.common.exceptions.AppResult",
        "kotlinx.coroutines.flow.Flow",
        "kotlin.Unit",
        "void",
        "kotlin.Nothing"
    )

    override fun process(resolver: Resolver): List<KSAnnotated> {
        processResultWrapping(resolver)
        processAbstractionBoundaries(resolver)
        processViewModelState(resolver)
        return emptyList()
    }

    /**
     * LAW-009: Mandatory Result Wrapping
     */
    private fun processResultWrapping(resolver: Resolver) {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.Repository") +
                      resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.Service") +
                      resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.UseCase")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            clazz.getDeclaredFunctions().forEach { function ->
                if (function.simpleName.asString() != "<init>" && isPublic(function)) {
                    val returnType = function.returnType?.resolve()
                    val qualifiedName = returnType?.declaration?.qualifiedName?.asString() ?: ""
                    
                    if (!allowedWrappers.contains(qualifiedName)) {
                        logger.error(
                            "Architecture Violation (LAW-009): Public method '${function.simpleName.asString()}' in ${clazz.simpleName.asString()} " +
                            "must return a wrapped Result type (AppResult or Flow). Found: $qualifiedName",
                            function
                        )
                    }
                }
            }
        }
    }

    /**
     * LAW-008: Abstraction Boundaries
     */
    private fun processAbstractionBoundaries(resolver: Resolver) {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.UseCase") +
                      resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.Repository")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            // Check properties (for interfaces/abstractions)
            clazz.getDeclaredProperties().forEach { prop ->
                if (isPublic(prop)) {
                    checkForbiddenType(prop.type.resolve(), prop, "property", clazz.simpleName.asString())
                }
            }
            // Check function parameters and returns
            clazz.getDeclaredFunctions().forEach { func ->
                if (isPublic(func)) {
                    checkForbiddenType(func.returnType?.resolve(), func, "return type", clazz.simpleName.asString())
                    func.parameters.forEach { param ->
                        checkForbiddenType(param.type.resolve(), param, "parameter", clazz.simpleName.asString())
                    }
                }
            }
        }
    }

    private fun checkForbiddenType(type: KSType?, node: KSNode, nodeType: String, className: String) {
        val qualifiedName = type?.declaration?.qualifiedName?.asString() ?: ""
        if (forbiddenInfrastructure.any { qualifiedName.startsWith(it) }) {
            logger.error(
                "Architecture Violation (LAW-008): Leakage detected in $className. " +
                "Public $nodeType '${node.toString()}' exposes infrastructure type: $qualifiedName",
                node
            )
        }
        // Recursively check type arguments (e.g. List<FirebaseUser>)
        type?.arguments?.forEach { arg ->
            checkForbiddenType(arg.type?.resolve(), node, "$nodeType argument", className)
        }
    }

    /**
     * LAW-016: ViewModel State Ownership
     */
    private fun processViewModelState(resolver: Resolver) {
        val symbols = resolver.getSymbolsWithAnnotation("com.estatia.realestate.apps.core.common.annotations.ViewModelMarker")

        symbols.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
            clazz.getDeclaredProperties().forEach { prop ->
                if (isPublic(prop)) {
                    val type = prop.type.resolve()
                    val qualifiedName = type.declaration.qualifiedName?.asString() ?: ""
                    
                    val isForbiddenState = qualifiedName == "kotlinx.coroutines.flow.MutableStateFlow" ||
                                           qualifiedName == "androidx.compose.runtime.MutableState"
                    
                    if (isForbiddenState) {
                        logger.error(
                            "Architecture Violation (LAW-016): ViewModel '${clazz.simpleName.asString()}' exposes mutable state '${prop.simpleName.asString()}'. " +
                            "Expose as StateFlow or a read-only interface instead.",
                            prop
                        )
                    }
                }
            }
        }
    }

    private fun isPublic(node: KSModifierListOwner): Boolean {
        return node.modifiers.contains(Modifier.PUBLIC) || node.modifiers.isEmpty()
    }
}

class ArchitectureProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ArchitectureProcessor(environment.logger)
    }
}
